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
        Path outputFile = Files.createTempFile("output-", ".pdf");
        Path certFile = Files.createTempFile("cert-", ".p12");
        try {
            Files.write(inputFile, pdfBytes);
            Files.write(certFile, certificateBytes);

            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                // Sign PDF using digital signature
                // NOTE: PdfSigner / DigitalSignatureOptions not available in SDK 2.0.1.
                // Exact signing API to be verified during Task 9.
                // The certificate file is at certFile.toString() with the given password.
                editor.save();
                editor.close();
                document.exportAsPdf(outputFile.toString());
            }

            return Files.readAllBytes(outputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
            Files.deleteIfExists(certFile);
        }
    }
}
