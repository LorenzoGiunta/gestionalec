package com.tesi.gestionalec.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDirPath) throws IOException {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    /**
     * Salva il file su disco e restituisce il percorso relativo salvato nel DB.
     * Il nome fisico è UUID + nome originale per evitare collisioni.
     */
    public String salva(MultipartFile file) throws IOException {
        String nomeOriginale = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        );
        // Previeni path traversal attacks
        if (nomeOriginale.contains("..")) {
            throw new IllegalArgumentException("Nome file non valido: " + nomeOriginale);
        }
        String nomeFisico = UUID.randomUUID() + "_" + nomeOriginale;
        Path destinazione = this.uploadDir.resolve(nomeFisico);
        Files.copy(file.getInputStream(), destinazione, StandardCopyOption.REPLACE_EXISTING);
        // Restituisce il percorso relativo (es. "uploads/documenti/uuid_file.pdf")
        return uploadDir.relativize(destinazione.toAbsolutePath()).toString();
    }

    /**
     * Carica un file dal disco come Resource per il download.
     */
    public Resource carica(String percorsoRelativo) throws IOException {
        Path filePath = this.uploadDir.resolve(percorsoRelativo).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        throw new RuntimeException("File non trovato o non leggibile: " + percorsoRelativo);
    }

    /**
     * Elimina un file dal disco (usato se si sovrascrive una versione).
     */
    public void elimina(String percorsoRelativo) {
        try {
            Path filePath = this.uploadDir.resolve(percorsoRelativo).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // Log in produzione
        }
    }
}
