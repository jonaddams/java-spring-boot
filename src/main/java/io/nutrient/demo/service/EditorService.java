package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.editors.PdfEditor;
import io.nutrient.sdk.editors.pdf.annotations.PdfAnnotationCollection;
import io.nutrient.sdk.exceptions.NutrientException;
import io.nutrient.sdk.types.Color;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class EditorService {

    public byte[] addAnnotations(byte[] pdfBytes) throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);

                PdfAnnotationCollection annotations = editor.getPageCollection()
                        .getFirst().getAnnotationCollection();

                // FreeText annotation near the top of the page
                annotations.addFreeText(50, 750, 300, 30,
                        "This is a freetext annotation", "SDK Demo",
                        "Helvetica", 14f, Color.fromArgb(255, 0, 0, 0));

                // Highlight annotation in the middle area
                annotations.addHighlight(50, 700, 200, 20,
                        "Highlighted region", "SDK Demo");

                // Stamp annotation
                annotations.addStamp(350, 700, 150, 50,
                        "APPROVED", "SDK Demo");

                editor.save();
                editor.close();
            }
            return Files.readAllBytes(inputFile);
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }

    public byte[] addWatermark(byte[] pdfBytes, String text, int fontSize, double rotation)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);

                // Add watermark stamp to the first page
                // Multi-page watermarking requires investigation with the SDK team
                // (native iterator crash on page collection)
                PdfAnnotationCollection annotations = editor.getPageCollection()
                        .getFirst().getAnnotationCollection();
                var stamp = annotations.addStamp(100, 400, 400, 100,
                        text, "Watermark");
                stamp.setOpacity(0.3f);
                stamp.setColor(Color.fromArgb(128, 200, 200, 200));

                editor.save();
                editor.close();
            }
            return Files.readAllBytes(inputFile);
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }

    public byte[] mergeDocuments(List<byte[]> documents, List<String> filenames)
            throws NutrientException, IOException {
        if (documents.isEmpty()) {
            throw new IllegalArgumentException("At least one document is required");
        }

        Path baseFile = Files.createTempFile("base-", "-" + filenames.get(0));
        List<Path> tempFiles = new java.util.ArrayList<>();
        tempFiles.add(baseFile);

        try {
            Files.write(baseFile, documents.get(0));
            try (Document baseDocument = Document.open(baseFile.toString())) {
                PdfEditor editor = PdfEditor.edit(baseDocument);

                for (int i = 1; i < documents.size(); i++) {
                    Path additionalFile = Files.createTempFile("merge-", "-" + filenames.get(i));
                    tempFiles.add(additionalFile);
                    Files.write(additionalFile, documents.get(i));
                    try (Document additionalDoc = Document.open(additionalFile.toString())) {
                        editor.appendDocument(additionalDoc);
                    }
                }

                editor.save();
                editor.close();
            }
            return Files.readAllBytes(baseFile);
        } finally {
            for (Path tempFile : tempFiles) {
                Files.deleteIfExists(tempFile);
            }
        }
    }

    public byte[] addPage(byte[] pdfBytes, double width, double height, int insertAtIndex)
            throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                PdfEditor editor = PdfEditor.edit(document);
                editor.getPageCollection().insert(insertAtIndex, (float) width, (float) height);
                editor.save();
                editor.close();
            }
            return Files.readAllBytes(inputFile);
        } finally {
            Files.deleteIfExists(inputFile);
        }
    }

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
