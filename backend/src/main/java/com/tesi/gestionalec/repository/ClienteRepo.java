package com.tesi.gestionalec.repository;

import com.tesi.gestionalec.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepo extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCodFiscale(String codiceFiscale);
}
