package com.example.gestiongnet.controller;

import com.example.gestiongnet.model.Agence;
import com.example.gestiongnet.services.AgenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agences")
@Tag(name = "Agences", description = "Gestion des agences et points de vente")
public class AgenceController {
    private final AgenceService agences;
    public AgenceController(AgenceService agences) {this.agences=agences;}

    @GetMapping
    @Operation(summary = "Lister toutes les agences")
    public List<Agence> getAll() {return agences.listAll();}

    @GetMapping("/search")
    public List<Agence> searchByRegion(@RequestParam String region) {return agences.findByRegion(region);}

    @GetMapping("/{id}")
    public Agence getOne(@PathVariable Long id) {return agences.get(id);}

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Agence> create(@Valid @RequestBody Agence agence) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agences.save(agence));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Agence update(@PathVariable Long id, @Valid @RequestBody Agence agence) {
        return agences.updateAgence(id, agence);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agences.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggleEtat")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Agence toggle(@PathVariable Long id) {
        Agence agence=agences.get(id);
        agence.setEtat(!agence.getEtat());
        return agences.save(agence);
    }
}
