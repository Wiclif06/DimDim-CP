package br.com.fiap.dimdim.controller.api;

import br.com.fiap.dimdim.dto.PagamentoRequest;
import br.com.fiap.dimdim.dto.PagamentoResponse;
import br.com.fiap.dimdim.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "CRUD da tabela TB_DD_PAGAMENTO (cada pagamento pertence a um cliente)")
public class PagamentoApiController {

    private final PagamentoService service;

    public PagamentoApiController(PagamentoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista os pagamentos (opcionalmente filtrando por clienteId)")
    public List<PagamentoResponse> listar(@RequestParam(required = false) Long clienteId) {
        return service.listar(clienteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um pagamento pelo id")
    public PagamentoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Registra um pagamento para um cliente existente")
    public ResponseEntity<PagamentoResponse> criar(@Valid @RequestBody PagamentoRequest req) {
        PagamentoResponse criado = service.criar(req);
        return ResponseEntity.created(URI.create("/api/pagamentos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um pagamento")
    public PagamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PagamentoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um pagamento")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
