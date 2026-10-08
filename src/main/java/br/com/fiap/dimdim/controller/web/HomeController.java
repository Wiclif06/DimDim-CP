package br.com.fiap.dimdim.controller.web;

import br.com.fiap.dimdim.service.ClienteService;
import br.com.fiap.dimdim.service.PagamentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ClienteService clientes;
    private final PagamentoService pagamentos;

    public HomeController(ClienteService clientes, PagamentoService pagamentos) {
        this.clientes = clientes;
        this.pagamentos = pagamentos;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Início");
        model.addAttribute("totalClientes", clientes.total());
        model.addAttribute("totalPagamentos", pagamentos.total());
        return "index";
    }
}
