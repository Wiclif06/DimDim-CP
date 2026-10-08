#!/usr/bin/env bash
# =============================================================================
# Passo 02 - Executa o DDL (scripts/ddl_dimdim.sql) no Azure SQL Database
# e lista as tabelas criadas.
# =============================================================================
set -euo pipefail
source "$(dirname "${BASH_SOURCE[0]}")/00-variaveis.sh"
pede_senha

if ! command -v sqlcmd >/dev/null 2>&1; then
  echo "ERRO: 'sqlcmd' nao encontrado. Use o Azure Cloud Shell (ja vem com sqlcmd) ou execute o"
  echo "conteudo de scripts/ddl_dimdim.sql no 'Query editor' do portal Azure (SQL Database > Query editor)."
  exit 1
fi

SERVIDOR="${SQL_SERVER}.database.windows.net"

echo "==> Executando scripts/ddl_dimdim.sql em ${SERVIDOR}/${SQL_DB}"
sqlcmd -S "${SERVIDOR}" -d "${SQL_DB}" -U "${SQL_ADMIN_USER}" -P "${SQL_ADMIN_PASSWORD}" \
       -l 60 -b -i "${DIR_SCRIPTS}/ddl_dimdim.sql"

echo "==> Tabelas existentes no banco:"
sqlcmd -S "${SERVIDOR}" -d "${SQL_DB}" -U "${SQL_ADMIN_USER}" -P "${SQL_ADMIN_PASSWORD}" \
       -l 60 -b -Q "SET NOCOUNT ON; SELECT name AS tabela FROM sys.tables ORDER BY name;"

echo "Proximo passo: bash scripts/03-deploy-app.sh"
