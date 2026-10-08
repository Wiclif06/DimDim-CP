#!/usr/bin/env bash
# =============================================================================
# Passo final (opcional) - Remove o Resource Group inteiro e evita cobrancas.
# Rode SOMENTE depois de gravar o video e de a entrega estar corrigida.
# =============================================================================
set -euo pipefail
source "$(dirname "${BASH_SOURCE[0]}")/00-variaveis.sh"

read -r -p "Isso apaga o resource group '${RG}' e TUDO dentro dele. Digite o nome do grupo para confirmar: " CONF
if [ "${CONF}" != "${RG}" ]; then
  echo "Cancelado."
  exit 1
fi
az group delete --name "${RG}" --yes --no-wait
rm -f "${ARQ_ESTADO}"
echo "Exclusao iniciada (roda em segundo plano)."
