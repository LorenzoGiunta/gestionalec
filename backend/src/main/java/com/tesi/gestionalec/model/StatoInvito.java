package com.tesi.gestionalec.model;

public enum StatoInvito {
    PENDING,   // inviato, in attesa di risposta dal collaboratore
    ACCEPTED,  // accettato — l'associazione è attiva
    DECLINED,  // rifiutato dal collaboratore
    EXPIRED    // scaduto automaticamente dallo scheduler notturno
}
