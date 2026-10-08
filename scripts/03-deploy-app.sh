#!/usr/bin/env bash
# =============================================================================
# Passo 03 - Compila o projeto (Maven) e faz o deploy automatizado no Web App
# com o Azure CLI:  az webapp deploy --type jar
# Depois aguarda a aplicacao responder em /actuator/health.
# =============================================================================
set -euo pipefail
source "$(dirname "${BASH_SOURCE[0]}")/00-variaveis.sh"

cd "${DIR_SCRIPTS}/.."

echo "==> Compilando (mvn clean package)"
mvn -B --no-transfer-progress clean package -DskipTests

JAR="target/dimdim.jar"
[ -f "${JAR}" ] || { echo "ERRO: ${JAR} nao foi gerado."; exit 1; }

echo "==> Deploy: az webapp deploy (${JAR} -> ${WEBAPP})"
az webapp deploy \
  --resource-group "${RG}" --name "${WEBAPP}" \
  --src-path "${JAR}" --type jar --restart true

URL="https://${WEBAPP}.azurewebsites.net"
echo "==> Aguardando a aplicacao subir (ate ~5 min) em ${URL}/actuator/health"
for i in $(seq 1 30); do
  CODIGO="$(curl -s -o /dev/null -w '%{http_code}' "${URL}/actuator/health" || true)"
  if [ "${CODIGO}" = "200" ]; then
    echo "Aplicacao no ar! (HTTP 200)"
    echo "  Telas .....: ${URL}/"
    echo "  Swagger ...: ${URL}/swagger-ui.html"
    exit 0
  fi
  echo "   tentativa ${i}/30 -> HTTP ${CODIGO:-000}"
  sleep 10
done

echo "A aplicacao nao respondeu 200 a tempo. Veja os logs com:"
echo "  az webapp log tail --resource-group ${RG} --name ${WEBAPP}"
exit 1
