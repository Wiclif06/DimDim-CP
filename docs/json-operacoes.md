# JSON das operações da API (GET, POST, PUT e DELETE)

Base URL: `https://<WEBAPP>.azurewebsites.net` (o nome do Web App é mostrado no final do script `01-criar-recursos.sh`).
Todas as operações podem ser executadas pelo **Swagger**: `https://<WEBAPP>.azurewebsites.net/swagger-ui.html`.

> Datas (`dtCadastro`, `dtPagamento`) são retornadas em **UTC**. Os CPFs abaixo são fictícios.

---

## Clientes — tabela `TB_DD_CLIENTE`

### POST `/api/clientes` — cadastrar (201 Created)

Requisição:
```json
{
  "nome": "Maria Souza",
  "email": "maria.souza@example.com",
  "cpf": "11144477735",
  "telefone": "11 91234-5678"
}
```
Resposta (`Location: /api/clientes/1`):
```json
{
  "id": 1,
  "nome": "Maria Souza",
  "email": "maria.souza@example.com",
  "cpf": "11144477735",
  "telefone": "11 91234-5678",
  "dtCadastro": "2026-10-06T17:42:10.123"
}
```

### GET `/api/clientes` — listar (200 OK)
```json
[
  {
    "id": 1,
    "nome": "Maria Souza",
    "email": "maria.souza@example.com",
    "cpf": "11144477735",
    "telefone": "11 91234-5678",
    "dtCadastro": "2026-10-06T17:42:10.123"
  }
]
```

### GET `/api/clientes/1` — buscar por id (200 OK)
```json
{
  "id": 1,
  "nome": "Maria Souza",
  "email": "maria.souza@example.com",
  "cpf": "11144477735",
  "telefone": "11 91234-5678",
  "dtCadastro": "2026-10-06T17:42:10.123"
}
```

### PUT `/api/clientes/1` — atualizar (200 OK)

Requisição:
```json
{
  "nome": "Maria Souza Lima",
  "email": "maria.souza@example.com",
  "cpf": "11144477735",
  "telefone": "11 98888-7777"
}
```
Resposta:
```json
{
  "id": 1,
  "nome": "Maria Souza Lima",
  "email": "maria.souza@example.com",
  "cpf": "11144477735",
  "telefone": "11 98888-7777",
  "dtCadastro": "2026-10-06T17:42:10.123"
}
```

### DELETE `/api/clientes/1` — excluir (204 No Content, sem corpo)

Só é permitido se o cliente **não tiver pagamentos** (senão retorna 409, veja "Erros").

---

## Pagamentos — tabela `TB_DD_PAGAMENTO` (cada pagamento pertence a um cliente)

Valores aceitos: `metodo` = `PIX` | `CARTAO` | `BOLETO`; `status` = `PENDENTE` | `APROVADO` | `CANCELADO` (se omitido, nasce `PENDENTE`).

### POST `/api/pagamentos` — registrar (201 Created)

Requisição:
```json
{
  "clienteId": 1,
  "descricao": "Pagamento da mensalidade de outubro",
  "valor": 150.50,
  "metodo": "PIX"
}
```
Resposta (`Location: /api/pagamentos/1`):
```json
{
  "id": 1,
  "clienteId": 1,
  "clienteNome": "Maria Souza Lima",
  "descricao": "Pagamento da mensalidade de outubro",
  "valor": 150.50,
  "metodo": "PIX",
  "status": "PENDENTE",
  "dtPagamento": "2026-10-06T17:45:31.870"
}
```

### GET `/api/pagamentos` — listar (200 OK)
Aceita o filtro opcional `?clienteId=1`.
```json
[
  {
    "id": 1,
    "clienteId": 1,
    "clienteNome": "Maria Souza Lima",
    "descricao": "Pagamento da mensalidade de outubro",
    "valor": 150.50,
    "metodo": "PIX",
    "status": "PENDENTE",
    "dtPagamento": "2026-10-06T17:45:31.870"
  }
]
```

### GET `/api/pagamentos/1` — buscar por id (200 OK)
```json
{
  "id": 1,
  "clienteId": 1,
  "clienteNome": "Maria Souza Lima",
  "descricao": "Pagamento da mensalidade de outubro",
  "valor": 150.50,
  "metodo": "PIX",
  "status": "PENDENTE",
  "dtPagamento": "2026-10-06T17:45:31.870"
}
```

### PUT `/api/pagamentos/1` — atualizar (200 OK)

Requisição:
```json
{
  "clienteId": 1,
  "descricao": "Pagamento da mensalidade de outubro",
  "valor": 150.50,
  "metodo": "CARTAO",
  "status": "APROVADO"
}
```
Resposta:
```json
{
  "id": 1,
  "clienteId": 1,
  "clienteNome": "Maria Souza Lima",
  "descricao": "Pagamento da mensalidade de outubro",
  "valor": 150.50,
  "metodo": "CARTAO",
  "status": "APROVADO",
  "dtPagamento": "2026-10-06T17:45:31.870"
}
```

### DELETE `/api/pagamentos/1` — excluir (204 No Content, sem corpo)

---

## Erros

**400 Bad Request** — validação (ex.: CPF com menos de 11 dígitos e valor zero):
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Dados inválidos",
  "instance": "/api/clientes",
  "campos": {
    "cpf": "CPF deve ter 11 dígitos numéricos (sem pontos ou traços)"
  }
}
```

**404 Not Found** — id inexistente:
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Cliente 999 não encontrado",
  "instance": "/api/clientes/999"
}
```

**409 Conflict** — e-mail/CPF duplicado ou exclusão de cliente que possui pagamentos:
```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "Não é possível excluir: o cliente possui pagamentos. Exclua os pagamentos primeiro.",
  "instance": "/api/clientes/1"
}
```
