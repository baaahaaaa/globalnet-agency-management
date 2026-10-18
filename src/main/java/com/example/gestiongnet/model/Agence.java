package com.example.gestiongnet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max=120)
    private String nom;
    @NotBlank
    @Size(max=255)
    private String adresse;
    private String telephone;
    private String horraire; // minuscule
    private boolean etat;

    public Agence() {}

    public Agence(String nom, String adresse, String telephone, String horraire, boolean etat) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.horraire = horraire;
        this.etat = etat;
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getHorraire() { return horraire; }
    public void setHorraire(String horraire) { this.horraire = horraire; }

    public boolean getEtat() { return etat; }
    public void setEtat(boolean etat) { this.etat = etat; }
}
