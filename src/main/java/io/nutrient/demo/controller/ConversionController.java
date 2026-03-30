package io.nutrient.demo.controller;

import io.nutrient.demo.service.ConversionService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/conversion")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/docx-to-pdf")
    public ResponseEntity<byte[]> docxToPdf(@RequestParam("file") MultipartFile file) {
        return convertToPdf(file);
    }

    @PostMapping("/xlsx-to-pdf")
    public ResponseEntity<byte[]> xlsxToPdf(@RequestParam("file") MultipartFile file) {
        return convertToPdf(file);
    }

    @PostMapping("/pptx-to-pdf")
    public ResponseEntity<byte[]> pptxToPdf(@RequestParam("file") MultipartFile file) {
        return convertToPdf(file);
    }

    @PostMapping("/html-to-pdf")
    public ResponseEntity<byte[]> htmlToPdf(@RequestParam("file") MultipartFile file) {
        return convertToPdf(file);
    }

    @PostMapping("/pdf-to-html")
    public ResponseEntity<byte[]> pdfToHtml(@RequestParam("file") MultipartFile file) {
        try {
            byte[] result = conversionService.pdfToHtml(file.getBytes());
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/pdf-to-docx")
    public ResponseEntity<byte[]> pdfToDocx(@RequestParam("file") MultipartFile file) {
        try {
            byte[] result = conversionService.pdfToDocx(file.getBytes());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.docx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/pdf-to-xlsx")
    public ResponseEntity<byte[]> pdfToXlsx(@RequestParam("file") MultipartFile file) {
        try {
            byte[] result = conversionService.pdfToXlsx(file.getBytes());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<byte[]> convertToPdf(MultipartFile file) {
        try {
            byte[] result = conversionService.convertToPdf(file.getBytes(), file.getOriginalFilename());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
