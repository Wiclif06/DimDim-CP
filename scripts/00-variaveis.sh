#!/usr/bin/env bash
# =============================================================================
# Variaveis compartilhadas pelos scripts do DimDim (NAO contem segredos).
# Os nomes de SQL Server e Web App precisam ser unicos no mundo todo; por isso
# geramos um sufixo aleatorio na 1a execucao e o guardamos em scripts/.dimdim.env
# (arquivo ignorado pelo git) para que os demais scripts usem os mesmos nomes.
# =============================================================================

DIR_SCRIPTS="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ARQ_ESTADO="${DIR_SCRIPTS}/.dimdim.env"

if [ -f "${ARQ_ESTADO}" ]; then
  # shellcheck disable=SC1090
  source "${ARQ_ESTADO}"
else
  SUFFIX="${SUFFIX:-$(printf '%05d' $((RANDOM % 100000)))}"
  echo "SUFFIX=${SUFFIX}" > "${ARQ_ESTADO}"
fi

export LOCATION="${LOCATION:-brazilsouth}"          # regiao (mude se a assinatura bloquear)
export RG="${RG:-rg-dimdim-cp5}"                      # resource group
export SQL_SERVER="${SQL_SERVER:-sql-dimdim-${SUFFIX}}"
export SQL_DB="${SQL_DB:-dimdimdb}"
export SQL_ADMIN_USER="${SQL_ADMIN_USER:-dimdimadmin}"
export PLANO="${PLANO:-plan-dimdim-${SUFFIX}}"
export WEBAPP="${WEBAPP:-app-dimdim-${SUFFIX}}"
export LOG_WS="${LOG_WS:-log-dimdim-${SUFFIX}}"       # Log Analytics workspace
export APPI="${APPI:-appi-dimdim-${SUFFIX}}"          # Application Insights
export RUNTIME="${RUNTIME:-JAVA:17-java17}"           # Java 17 no Linux
export SKU_PLANO="${SKU_PLANO:-B1}"

# Le a senha do administrador do SQL sem mostrar na tela e sem gravar em arquivo.
# Dica: para digitar uma unica vez, rode antes:  read -r -s SQL_ADMIN_PASSWORD; export SQL_ADMIN_PASSWORD
pede_senha() {
  if [ -z "${SQL_ADMIN_PASSWORD:-}" ]; then
    read -r -s -p "Senha do admin do SQL (min. 12 caracteres com maiuscula, minuscula, numero e simbolo): " SQL_ADMIN_PASSWORD
    echo
    export SQL_ADMIN_PASSWORD
  fi
}
