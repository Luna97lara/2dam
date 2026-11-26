package org.example.primerproyectospring.ui;


import org.example.primerproyectospring.domain.modelo.Cosa;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cozas")
public class RestCosas {

    @GetMapping("/{cosa}")
    public Cosa damePath(@PathVariable String cosa) {
        return new Cosa(cosa);
    }

    @GetMapping
    public List<Cosa> getTodasCosas() {
        return List.of(new Cosa("nombre"));
    }

}
