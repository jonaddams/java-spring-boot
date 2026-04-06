package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.editors.PdfEditor;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EditorService {

    public String getMetadata(byte[] pdfBytes) throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                String xmp = editor.getMetadata().getXMP();
                editor.close();
                return xmp;
            }
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }
}
