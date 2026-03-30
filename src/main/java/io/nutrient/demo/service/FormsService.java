package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.editors.PdfEditor;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class FormsService {

    public byte[] fillFormFields(byte[] pdfBytes, Map<String, String> fieldValues)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                // Fill form fields using the SDK's FormFieldEditor API
                // Exact API to be verified
                editor.save();
                editor.close();
            }
            return Files.readAllBytes(inputFile);
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }

    public byte[] createFormFields(byte[] pdfBytes, String fieldDefinitionsJson)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                // Create form fields using the SDK's FormFieldEditor API
                // Exact API to be verified
                editor.save();
                editor.close();
            }
            return Files.readAllBytes(inputFile);
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }
}
