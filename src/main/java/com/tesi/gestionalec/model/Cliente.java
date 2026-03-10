package com.tesi.gestionalec.model;


import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Utente {

    @Column(unique = true)
    private String codFiscale;

    private String pIVA;

    @Enumerated(EnumType.STRING)
    private RegimeFiscale regime;

    private Double redditoAnno;

    /* Relazioni
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pratica> pratiche;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Documento> documenti;

    */



    @Override
    public Ruolo getRuolo() {
        return Ruolo.CLIENTE;
    }
}
