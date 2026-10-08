package br.com.fiap.dimdim.controller.web;

import br.com.fiap.dimdim.dto.ClienteRequest;
import br.com.fiap.dimdim.dto.ClienteResponse;
import br.com.fiap.dimdim.exception.ConflictException;
import br.com.fiap.dimdim.exception.NotFoundException;
import br.com.fiap.dimdim.service.ClienteService;
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
@RequestMapping("/clientes")
public class ClienteWebController {

    private final ClienteService service;

    public ClienteWebController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("titulo", "Clientes");
        model.addAttribute("clientes", service.listar());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new ClienteRequest());
        return formulario(model, "Novo cliente", "/clientes");
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ClienteRequest form, BindingResult br,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return formulario(model, "Novo cliente", "/clientes");
        }
        try {
            ClienteResponse criado = service.criar(form);
            ra.addFlashAttribute("msg", "Cliente " + criado.getNome() + " cadastrado com sucesso (id " + criado.getId() + ").");
            return "redirect:/clientes";
        } catch (ConflictException e) {
            br.reject("conflito", e.getMessage());
            return formulario(model, "Novo cliente", "/clientes");
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            ClienteResponse c = service.buscar(id);
            ClienteRequest form = new ClienteRequest();
            form.setNome(c.getNome());
            form.setEmail(c.getEmail());
            form.setCpf(c.getCpf());
            form.setTelefone(c.getTelefone());
            model.addAttribute("form", form);
            return formulario(model, "Editar cliente #" + id, "/clientes/" + id);
        } catch (NotFoundException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") ClienteRequest form,
                            BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return formulario(model, "Editar cliente #" + id, "/clientes/" + id);
        }
        try {
            service.atualizar(id, form);
            ra.addFlashAttribute("msg", "Cliente " + id + " atualizado com sucesso.");
            return "redirect:/clientes";
        } catch (ConflictException e) {
            br.reject("conflito", e.getMessage());
            return formulario(model, "Editar cliente #" + id, "/clientes/" + id);
        } catch (NotFoundException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("msg", "Cliente " + id + " excluído com sucesso.");
        } catch (ConflictException | NotFoundException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/clientes";
    }

    private String formulario(Model model, String titulo, String action) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("action", action);
        return "clientes/form";
    }
}
