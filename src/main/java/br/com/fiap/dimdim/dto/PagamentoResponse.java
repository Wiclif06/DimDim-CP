package br.com.fiap.dimdim.dto;

import br.com.fiap.dimdim.model.MetodoPagamento;
import br.com.fiap.dimdim.model.Pagamento;
import br.com.fiap.dimdim.model.StatusPagamento;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoResponse {

    private final Long id;
    private final Long clienteId;
    private final String clienteNome;
    private final String descricao;
    private final BigDecimal valor;
    private final MetodoPagamento metodo;
    private final StatusPagamento status;
    private final LocalDateTime dtPagamento;

    public PagamentoResponse(Long id, Long clienteId, String clienteNome, String descricao, BigDecimal valor,
                             MetodoPagamento metodo, StatusPagamento status, LocalDateTime dtPagamento) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.descricao = descricao;
        this.valor = valor;
        this.metodo = metodo;
        this.status = status;
        this.dtPagamento = dtPagamento;
    }

    public static PagamentoResponse from(Pagamento p) {
        return new PagamentoResponse(
                p.getId(),
                p.getCliente().getId(),
                p.getCliente().getNome(),
                p.getDescricao(),
                p.getValor(),
                p.getMetodo(),
                p.getStatus(),
                p.getDtPagamento());
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public String getClienteNome() { return clienteNome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public MetodoPagamento getMetodo() { return metodo; }
    public StatusPagamento getStatus() { return status; }

    /** Data do pagamento em UTC (formato ISO-8601 no JSON). */
    public LocalDateTime getDtPagamento() { return dtPagamento; }

    @JsonIgnore
    public String getDtPagamentoFormatado() {
        return FormatoData.paraTela(dtPagamento);
    }
}
