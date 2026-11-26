package org.example.primerproyectospring.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/primero")
public class PrimerController {

    @RequestMapping("/{id}/{cosa}")
    public String mostrarPagina(Model model, @PathVariable int id, @PathVariable String cosa) {
        model.addAttribute("id", id);
        model.addAttribute("titulo", cosa);
        return "primero";
    }

}
