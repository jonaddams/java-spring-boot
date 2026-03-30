package io.nutrient.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutrient.sdk.Document;
import io.nutrient.sdk.editors.PdfEditor;
import io.nutrient.sdk.editors.pdf.formfields.PdfFormField;
import io.nutrient.sdk.editors.pdf.formfields.PdfFormFieldCollection;
import io.nutrient.sdk.editors.pdf.formfields.PdfTextField;
import io.nutrient.sdk.editors.pdf.pages.PdfPage;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class FormsService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] fillFormFields(byte[] pdfBytes, Map<String, String> fieldValues)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                PdfFormFieldCollection formFields = editor.getFormFieldCollection();

                for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                    PdfFormField field = formFields.findByFullName(entry.getKey());
                    if (field != null) {
                        field.setValue(entry.getValue());
                        if (field instanceof PdfTextField textField) {
                            textField.setText(entry.getValue());
                        }
                    }
                }

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
                PdfFormFieldCollection formFields = editor.getFormFieldCollection();
                PdfPage firstPage = editor.getPageCollection().getFirst();

                List<Map<String, Object>> definitions = objectMapper.readValue(
                        fieldDefinitionsJson, new TypeReference<>() {});

                for (Map<String, Object> def : definitions) {
                    String name = (String) def.get("name");
                    float x = ((Number) def.get("x")).floatValue();
                    float y = ((Number) def.get("y")).floatValue();
                    float width = ((Number) def.get("width")).floatValue();
                    float height = ((Number) def.get("height")).floatValue();

                    formFields.addTextField(name, firstPage, x, y, width, height);
                }

                editor.save();
                editor.close();
            }
            return Files.readAllBytes(inputFile);
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }
}
