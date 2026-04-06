package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.editors.PdfEditor;
import io.nutrient.sdk.editors.pdf.formfields.PdfFormField;
import io.nutrient.sdk.editors.pdf.formfields.PdfFormFieldCollection;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormsService {

    public List<Map<String, String>> listFormFields(byte[] pdfBytes)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                PdfFormFieldCollection formFields = editor.getFormFieldCollection();
                List<Map<String, String>> result = new ArrayList<>();

                for (int i = 0; i < formFields.size(); i++) {
                    PdfFormField field = formFields.get(i);
                    Map<String, String> info = new LinkedHashMap<>();
                    info.put("name", field.getFullName());
                    info.put("type", field.getClass().getSimpleName());
                    result.add(info);
                }

                editor.close();
                return result;
            }
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }
}
