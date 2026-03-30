package io.nutrient.demo.service;

import io.nutrient.sdk.exceptions.NutrientException;
import io.nutrient.sdk.signing.DigitalSignatureOptions;
import io.nutrient.sdk.signing.PdfSigner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SigningService {

    public byte[] signDocument(byte[] pdfBytes, byte[] certificateBytes, String password)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        Path outputFile = Files.createTempFile("signed-", ".pdf");
        Path certFile = Files.createTempFile("cert-", ".p12");
        try {
            Files.write(inputFile, pdfBytes);
            Files.write(certFile, certificateBytes);

            DigitalSignatureOptions options = new DigitalSignatureOptions();
            options.setCertificatePath(certFile.toString());
            options.setCertificatePassword(password);
            options.setSignerName("Nutrient SDK Demo");
            options.setReason("Document signing demo");
            options.setLocation("Nutrient Java SDK");

            try (PdfSigner signer = new PdfSigner()) {
                signer.sign(inputFile.toString(), outputFile.toString(), options);
            }

            return Files.readAllBytes(outputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
            Files.deleteIfExists(certFile);
        }
    }
}
