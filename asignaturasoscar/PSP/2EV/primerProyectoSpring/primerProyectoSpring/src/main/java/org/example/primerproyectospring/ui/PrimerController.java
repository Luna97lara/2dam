package org.example.primerproyectospring.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/primero")
public class PrimerController {

    @RequestMapping("/mostrar")
    public String mostrarPagina(Model model) {
        model.addAttribute("titulo", "Primer Proyecto");
        return "primero";
    }

}
