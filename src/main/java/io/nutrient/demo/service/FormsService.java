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
        Path outputFile = Files.createTempFile("output-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                // Fill form fields using the SDK's FormFieldEditor API
                // Exact API to be verified during Task 9
                editor.save();
                editor.close();
                document.exportAsPdf(outputFile.toString());
            }
            return Files.readAllBytes(outputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
        }
    }

    public byte[] createFormFields(byte[] pdfBytes, String fieldDefinitionsJson)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        Path outputFile = Files.createTempFile("output-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                // Create form fields using the SDK's FormFieldEditor API
                // Exact API to be verified during Task 9
                editor.save();
                editor.close();
                document.exportAsPdf(outputFile.toString());
            }
            return Files.readAllBytes(outputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
        }
    }
}
