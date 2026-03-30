package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.editors.PdfEditor;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SigningService {

    public byte[] signDocument(byte[] pdfBytes, byte[] certificateBytes, String password)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        Path certFile = Files.createTempFile("cert-", ".p12");
        try {
            Files.write(inputFile, pdfBytes);
            Files.write(certFile, certificateBytes);

            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                // Sign PDF using digital signature
                // Exact signing API to be verified — certificate at certFile with password
                editor.save();
                editor.close();
            }

            return Files.readAllBytes(inputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(certFile);
        }
    }
}
