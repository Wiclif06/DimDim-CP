package br.com.fiap.dimdim.dto;

import br.com.fiap.dimdim.model.MetodoPagamento;
import br.com.fiap.dimdim.model.StatusPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Corpo de POST/PUT de pagamento (tambem usado como objeto dos formularios web). */
public class PagamentoRequest {

    @NotNull(message = "Selecione o cliente")
    private Long clienteId;

    @NotBlank(message = "Informe a descrição")
    @Size(max = 200, message = "Descrição com no máximo 200 caracteres")
    private String descricao;

    @NotNull(message = "Informe o valor")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Valor inválido (até 2 casas decimais)")
    private BigDecimal valor;

    @NotNull(message = "Selecione o método de pagamento")
    private MetodoPagamento metodo;

    /** Opcional: se vazio, o pagamento nasce como PENDENTE. */
    private StatusPagamento status;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public MetodoPagamento getMetodo() { return metodo; }
    public void setMetodo(MetodoPagamento metodo) { this.metodo = metodo; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
}
