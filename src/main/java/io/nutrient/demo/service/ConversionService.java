package io.nutrient.demo.service;

import io.nutrient.sdk.Document;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ConversionService {

    public byte[] convertToPdf(byte[] inputBytes, String originalFilename) throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", "-" + originalFilename);
        Path outputFile = Files.createTempFile("output-", ".pdf");
        try {
            Files.write(inputFile, inputBytes);
            try (Document document = Document.open(inputFile.toString())) {
                document.exportAsPdf(outputFile.toString());
            }
            return Files.readAllBytes(outputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
        }
    }

    public byte[] pdfToHtml(byte[] pdfBytes) throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        Path outputFile = Files.createTempFile("output-", ".html");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                document.exportAsHtml(outputFile.toString());
            }
            return Files.readAllBytes(outputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
        }
    }

    public byte[] pdfToDocx(byte[] pdfBytes) throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        Path outputFile = Files.createTempFile("output-", ".docx");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                document.exportAsWord(outputFile.toString());
            }
            return Files.readAllBytes(outputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
        }
    }

    public byte[] pdfToXlsx(byte[] pdfBytes) throws NutrientException, IOException {
        Path inputFile = Files.createTempFile("input-", ".pdf");
        Path outputFile = Files.createTempFile("output-", ".xlsx");
        try {
            Files.write(inputFile, pdfBytes);
            try (Document document = Document.open(inputFile.toString())) {
                document.exportAsSpreadsheet(outputFile.toString());
            }
            return Files.readAllBytes(outputFile);
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(outputFile);
        }
    }
}
