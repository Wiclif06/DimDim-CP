package br.com.fiap.dimdim.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Corpo de POST/PUT de cliente (tambem usado como objeto dos formularios web). */
public class ClienteRequest {

    @NotBlank(message = "Informe o nome")
    @Size(max = 100, message = "Nome com no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "E-mail inválido")
    @Size(max = 120, message = "E-mail com no máximo 120 caracteres")
    private String email;

    @NotBlank(message = "Informe o CPF")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos numéricos (sem pontos ou traços)")
    private String cpf;

    @Size(max = 20, message = "Telefone com no máximo 20 caracteres")
    private String telefone;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
