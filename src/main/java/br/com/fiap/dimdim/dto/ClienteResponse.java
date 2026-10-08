package br.com.fiap.dimdim.dto;

import br.com.fiap.dimdim.model.Cliente;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class ClienteResponse {

    private final Long id;
    private final String nome;
    private final String email;
    private final String cpf;
    private final String telefone;
    private final LocalDateTime dtCadastro;

    public ClienteResponse(Long id, String nome, String email, String cpf, String telefone, LocalDateTime dtCadastro) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.dtCadastro = dtCadastro;
    }

    public static ClienteResponse from(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getCpf(), c.getTelefone(), c.getDtCadastro());
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }

    /** Data de cadastro em UTC (formato ISO-8601 no JSON). */
    public LocalDateTime getDtCadastro() { return dtCadastro; }

    @JsonIgnore
    public String getDtCadastroFormatado() {
        return FormatoData.paraTela(dtCadastro);
    }
}
