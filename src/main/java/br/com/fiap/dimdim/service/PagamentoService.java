package br.com.fiap.dimdim.service;

import br.com.fiap.dimdim.dto.PagamentoRequest;
import br.com.fiap.dimdim.dto.PagamentoResponse;
import br.com.fiap.dimdim.exception.NotFoundException;
import br.com.fiap.dimdim.model.Cliente;
import br.com.fiap.dimdim.model.Pagamento;
import br.com.fiap.dimdim.model.StatusPagamento;
import br.com.fiap.dimdim.repository.ClienteRepository;
import br.com.fiap.dimdim.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentos;
    private final ClienteRepository clientes;

    public PagamentoService(PagamentoRepository pagamentos, ClienteRepository clientes) {
        this.pagamentos = pagamentos;
        this.clientes = clientes;
    }

    /** Se clienteId vier preenchido, filtra os pagamentos daquele cliente. */
    @Transactional(readOnly = true)
    public List<PagamentoResponse> listar(Long clienteId) {
        List<Pagamento> lista = clienteId == null ? pagamentos.listarTodos() : pagamentos.listarPorCliente(clienteId);
        return lista.stream().map(PagamentoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PagamentoResponse buscar(Long id) {
        return PagamentoResponse.from(obter(id));
    }

    @Transactional(readOnly = true)
    public long total() {
        return pagamentos.count();
    }

    @Transactional
    public PagamentoResponse criar(PagamentoRequest req) {
        Pagamento p = new Pagamento();
        copiar(req, p);
        return PagamentoResponse.from(pagamentos.save(p));
    }

    @Transactional
    public PagamentoResponse atualizar(Long id, PagamentoRequest req) {
        Pagamento p = obter(id);
        copiar(req, p);
        return PagamentoResponse.from(pagamentos.save(p));
    }

    @Transactional
    public void excluir(Long id) {
        pagamentos.delete(obter(id));
    }

    private Pagamento obter(Long id) {
        return pagamentos.findById(id)
                .orElseThrow(() -> new NotFoundException("Pagamento " + id + " não encontrado"));
    }

    private void copiar(PagamentoRequest req, Pagamento p) {
        Cliente cliente = clientes.findById(req.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente " + req.getClienteId() + " não encontrado"));
        p.setCliente(cliente);
        p.setDescricao(req.getDescricao().trim());
        p.setValor(req.getValor());
        p.setMetodo(req.getMetodo());
        if (req.getStatus() != null) {
            p.setStatus(req.getStatus());
        } else if (p.getStatus() == null) {
            p.setStatus(StatusPagamento.PENDENTE);
        }
    }
}
