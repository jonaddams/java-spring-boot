package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.editors.PdfEditor;
import io.nutrient.sdk.exceptions.NutrientException;
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
                // Add annotations on first page — exact annotation API to be verified
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
                // Add stamp annotation as watermark — exact API to be verified
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
                editor.addPage((float) width, (float) height, insertAtIndex);
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
