#!/usr/bin/env bash
# =============================================================================
# Passo 01 - Cria TODOS os recursos na Azure via Azure CLI:
#   Resource Group, Azure SQL Server + Database (PaaS), Log Analytics,
#   Application Insights, App Service Plan (Linux) e Web App (Java 17).
# Tambem grava as App Settings (conexao com o banco e Application Insights).
# =============================================================================
set -euo pipefail
source "$(dirname "${BASH_SOURCE[0]}")/00-variaveis.sh"
pede_senha

echo "==> Assinatura em uso:"
az account show --query "{nome:name, id:id}" -o table

# Instala extensoes do CLI sob demanda sem perguntar (application-insights)
az config set extension.use_dynamic_install=yes_without_prompt >/dev/null 2>&1 || true
az config set extension.dynamic_install_allow_preview=true >/dev/null 2>&1 || true

echo "==> Registrando providers (so demora na 1a vez da assinatura)"
for ns in Microsoft.Sql Microsoft.Web Microsoft.Insights Microsoft.OperationalInsights; do
  az provider register --namespace "${ns}" --wait >/dev/null
done

echo "==> [1/7] Resource Group ${RG} (${LOCATION})"
az group create --name "${RG}" --location "${LOCATION}" --output table

echo "==> [2/7] Azure SQL Server ${SQL_SERVER}"
az sql server create \
  --resource-group "${RG}" --name "${SQL_SERVER}" --location "${LOCATION}" \
  --admin-user "${SQL_ADMIN_USER}" --admin-password "${SQL_ADMIN_PASSWORD}" \
  --output table

# Libera acesso de servicos Azure (Web App e Cloud Shell) ao servidor SQL
az sql server firewall-rule create \
  --resource-group "${RG}" --server "${SQL_SERVER}" \
  --name AllowAzureServices --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0 \
  --output table

echo "==> [3/7] Azure SQL Database ${SQL_DB} (Basic)"
az sql db create \
  --resource-group "${RG}" --server "${SQL_SERVER}" --name "${SQL_DB}" \
  --edition Basic --service-objective Basic --backup-storage-redundancy Local \
  --output table

echo "==> [4/7] Log Analytics + Application Insights"
az monitor log-analytics workspace create \
  --resource-group "${RG}" --workspace-name "${LOG_WS}" --location "${LOCATION}" \
  --output table
LOG_WS_ID="$(az monitor log-analytics workspace show \
  --resource-group "${RG}" --workspace-name "${LOG_WS}" --query id -o tsv)"
az monitor app-insights component create \
  --app "${APPI}" --resource-group "${RG}" --location "${LOCATION}" \
  --kind web --application-type web --workspace "${LOG_WS_ID}" \
  --output table
APPI_CONN="$(az monitor app-insights component show \
  --app "${APPI}" --resource-group "${RG}" --query connectionString -o tsv)"

echo "==> [5/7] App Service Plan ${PLANO} (Linux ${SKU_PLANO})"
az appservice plan create \
  --resource-group "${RG}" --name "${PLANO}" --location "${LOCATION}" \
  --is-linux --sku "${SKU_PLANO}" --output table

echo "==> [6/7] Web App ${WEBAPP} (${RUNTIME})"
az webapp create \
  --resource-group "${RG}" --plan "${PLANO}" --name "${WEBAPP}" \
  --runtime "${RUNTIME}" --output table
az webapp config set \
  --resource-group "${RG}" --name "${WEBAPP}" --always-on true --output none
az webapp log config \
  --resource-group "${RG}" --name "${WEBAPP}" \
  --application-logging filesystem --level information --output none

echo "==> [7/7] App Settings (conexao SQL + Application Insights)"
JDBC_URL="jdbc:sqlserver://${SQL_SERVER}.database.windows.net:1433;database=${SQL_DB};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
# --output none: o comando ecoaria os valores (inclusive a senha) na tela
az webapp config appsettings set \
  --resource-group "${RG}" --name "${WEBAPP}" \
  --settings \
    "SPRING_DATASOURCE_URL=${JDBC_URL}" \
    "SPRING_DATASOURCE_USERNAME=${SQL_ADMIN_USER}" \
    "SPRING_DATASOURCE_PASSWORD=${SQL_ADMIN_PASSWORD}" \
    "SERVER_PORT=8080" \
    "WEBSITES_PORT=8080" \
    "APPLICATIONINSIGHTS_CONNECTION_STRING=${APPI_CONN}" \
    "ApplicationInsightsAgent_EXTENSION_VERSION=~3" \
  --output none

echo
echo "Recursos criados com sucesso."
echo "  Web App ........: https://${WEBAPP}.azurewebsites.net"
echo "  SQL Server .....: ${SQL_SERVER}.database.windows.net  (banco: ${SQL_DB})"
echo "  App Insights ...: ${APPI}"
echo "Proximo passo: bash scripts/02-criar-tabelas.sh"
