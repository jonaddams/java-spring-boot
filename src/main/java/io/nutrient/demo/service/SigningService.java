package io.nutrient.demo.service;

import io.nutrient.sdk.exceptions.NutrientException;
import io.nutrient.sdk.signing.DigitalSignatureOptions;
import io.nutrient.sdk.signing.PdfSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SigningService {

    private static final Logger log = LoggerFactory.getLogger(SigningService.class);
    private static final String DEMO_CERT_PATH = "demo-certificate.p12";
    private static final String DEMO_CERT_PASSWORD = "nutrient-demo";

    /**
     * Sign a PDF using the server-side demo certificate.
     */
    public byte[] signWithDemoCert(byte[] pdfBytes) throws NutrientException, IOException {
        ClassPathResource certResource = new ClassPathResource(DEMO_CERT_PATH);
        if (!certResource.exists()) {
            throw new IOException("Demo certificate not found on classpath: " + DEMO_CERT_PATH);
        }
        byte[] certBytes;
        try (InputStream is = certResource.getInputStream()) {
            certBytes = is.readAllBytes();
        }
        log.info("Signing with server-side demo certificate ({} bytes)", certBytes.length);
        return signDocument(pdfBytes, certBytes, DEMO_CERT_PASSWORD);
    }

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
