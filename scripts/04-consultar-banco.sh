#!/usr/bin/env bash
# =============================================================================
# Passo 04 (usado nos testes/video) - Mostra o conteudo das DUAS tabelas direto
# no Azure SQL Database, para evidenciar a persistencia apos cada operacao CRUD.
# =============================================================================
set -euo pipefail
source "$(dirname "${BASH_SOURCE[0]}")/00-variaveis.sh"
pede_senha

SERVIDOR="${SQL_SERVER}.database.windows.net"

sqlcmd -S "${SERVIDOR}" -d "${SQL_DB}" -U "${SQL_ADMIN_USER}" -P "${SQL_ADMIN_PASSWORD}" -l 60 -b -s "|" -W -Q "
SET NOCOUNT ON;
PRINT '=== TB_DD_CLIENTE ===';
SELECT id_cliente, nome, email, cpf, telefone, dt_cadastro FROM dbo.TB_DD_CLIENTE ORDER BY id_cliente;
PRINT '';
PRINT '=== TB_DD_PAGAMENTO ===';
SELECT id_pagamento, id_cliente, descricao, valor, metodo, status, dt_pagamento FROM dbo.TB_DD_PAGAMENTO ORDER BY id_pagamento;
"
