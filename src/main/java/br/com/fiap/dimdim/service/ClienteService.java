package br.com.fiap.dimdim.service;

import br.com.fiap.dimdim.dto.ClienteRequest;
import br.com.fiap.dimdim.dto.ClienteResponse;
import br.com.fiap.dimdim.exception.ConflictException;
import br.com.fiap.dimdim.exception.NotFoundException;
import br.com.fiap.dimdim.model.Cliente;
import br.com.fiap.dimdim.repository.ClienteRepository;
import br.com.fiap.dimdim.repository.PagamentoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clientes;
    private final PagamentoRepository pagamentos;

    public ClienteService(ClienteRepository clientes, PagamentoRepository pagamentos) {
        this.clientes = clientes;
        this.pagamentos = pagamentos;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clientes.findAll(Sort.by("nome")).stream().map(ClienteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
        return ClienteResponse.from(obter(id));
    }

    @Transactional(readOnly = true)
    public long total() {
        return clientes.count();
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest req) {
        if (clientes.existsByEmail(req.getEmail())) {
            throw new ConflictException("Já existe um cliente com este e-mail");
        }
        if (clientes.existsByCpf(req.getCpf())) {
            throw new ConflictException("Já existe um cliente com este CPF");
        }
        Cliente c = new Cliente();
        copiar(req, c);
        return ClienteResponse.from(clientes.save(c));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente c = obter(id);
        if (clientes.existsByEmailAndIdNot(req.getEmail(), id)) {
            throw new ConflictException("Já existe outro cliente com este e-mail");
        }
        if (clientes.existsByCpfAndIdNot(req.getCpf(), id)) {
            throw new ConflictException("Já existe outro cliente com este CPF");
        }
        copiar(req, c);
        return ClienteResponse.from(clientes.save(c));
    }

    @Transactional
    public void excluir(Long id) {
        Cliente c = obter(id);
        if (pagamentos.existsByClienteId(id)) {
            throw new ConflictException("Não é possível excluir: o cliente possui pagamentos. Exclua os pagamentos primeiro.");
        }
        clientes.delete(c);
    }

    private Cliente obter(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente " + id + " não encontrado"));
    }

    private void copiar(ClienteRequest req, Cliente c) {
        c.setNome(req.getNome().trim());
        c.setEmail(req.getEmail().trim().toLowerCase());
        c.setCpf(req.getCpf());
        String tel = req.getTelefone();
        c.setTelefone(tel == null || tel.isBlank() ? null : tel.trim());
    }
}
