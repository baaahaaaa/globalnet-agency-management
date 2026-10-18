package com.example.gestiongnet.services;

import com.example.gestiongnet.model.Agence;
import com.example.gestiongnet.repository.AgenceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AgenceService {
    private final AgenceRepository repository;
    public AgenceService(AgenceRepository repository) {this.repository=repository;}

    public List<Agence> listAll() {return repository.findAll();}
    public Agence save(Agence agence) {return repository.save(agence);}
    public Agence get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agence introuvable"));
    }
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Agence introuvable");
        repository.deleteById(id);
    }
    public Agence updateAgence(Long id, Agence newAgence) {
        Agence agence = get(id);
        agence.setNom(newAgence.getNom());
        agence.setAdresse(newAgence.getAdresse());
        agence.setHorraire(newAgence.getHorraire());
        agence.setTelephone(newAgence.getTelephone());
        agence.setEtat(newAgence.getEtat());
        return repository.save(agence);
    }
    public List<Agence> findByRegion(String region) {
        if (region == null || region.isBlank()) return listAll();
        return repository.findByRegion(region.trim());
    }
}
