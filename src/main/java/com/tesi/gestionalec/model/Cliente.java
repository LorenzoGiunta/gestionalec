package com.tesi.gestionalec.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@ToString(exclude = {"pratiche" , "documenti"})
@AllArgsConstructor
@NoArgsConstructor
public class Cliente extends Utente {

    @Column(unique = true)
    private String codFiscale;

    private String pIVA;

    @Enumerated(EnumType.STRING)
    private RegimeFiscale regime;

    private Double redditoAnnuo;


    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pratica> pratiche;

    @OneToMany(mappedBy = "caricatoDa", cascade = CascadeType.ALL)
    private List<Documento> documenti;

    @Override
    public Ruolo getRuolo() {
        return Ruolo.CLIENTE;
    }
}
