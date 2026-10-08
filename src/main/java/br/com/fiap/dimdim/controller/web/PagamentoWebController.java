package br.com.fiap.dimdim.controller.web;

import br.com.fiap.dimdim.dto.PagamentoRequest;
import br.com.fiap.dimdim.dto.PagamentoResponse;
import br.com.fiap.dimdim.exception.NotFoundException;
import br.com.fiap.dimdim.model.MetodoPagamento;
import br.com.fiap.dimdim.model.StatusPagamento;
import br.com.fiap.dimdim.service.ClienteService;
import br.com.fiap.dimdim.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoWebController {

    private final PagamentoService service;
    private final ClienteService clienteService;

    public PagamentoWebController(PagamentoService service, ClienteService clienteService) {
        this.service = service;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("titulo", "Pagamentos");
        model.addAttribute("pagamentos", service.listar(null));
        return "pagamentos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        PagamentoRequest form = new PagamentoRequest();
        form.setStatus(StatusPagamento.PENDENTE);
        model.addAttribute("form", form);
        return formulario(model, "Novo pagamento", "/pagamentos");
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") PagamentoRequest form, BindingResult br,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return formulario(model, "Novo pagamento", "/pagamentos");
        }
        try {
            PagamentoResponse criado = service.criar(form);
            ra.addFlashAttribute("msg", "Pagamento " + criado.getId() + " registrado para " + criado.getClienteNome() + ".");
            return "redirect:/pagamentos";
        } catch (NotFoundException e) {
            br.reject("naoEncontrado", e.getMessage());
            return formulario(model, "Novo pagamento", "/pagamentos");
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            PagamentoResponse p = service.buscar(id);
            PagamentoRequest form = new PagamentoRequest();
            form.setClienteId(p.getClienteId());
            form.setDescricao(p.getDescricao());
            form.setValor(p.getValor());
            form.setMetodo(p.getMetodo());
            form.setStatus(p.getStatus());
            model.addAttribute("form", form);
            return formulario(model, "Editar pagamento #" + id, "/pagamentos/" + id);
        } catch (NotFoundException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/pagamentos";
        }
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") PagamentoRequest form,
                            BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return formulario(model, "Editar pagamento #" + id, "/pagamentos/" + id);
        }
        try {
            service.atualizar(id, form);
            ra.addFlashAttribute("msg", "Pagamento " + id + " atualizado com sucesso.");
            return "redirect:/pagamentos";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/pagamentos";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("msg", "Pagamento " + id + " excluído com sucesso.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pagamentos";
    }

    private String formulario(Model model, String titulo, String action) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("action", action);
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("metodos", MetodoPagamento.values());
        model.addAttribute("statusLista", StatusPagamento.values());
        return "pagamentos/form";
    }
}
