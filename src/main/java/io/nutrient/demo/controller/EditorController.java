package io.nutrient.demo.controller;

import io.nutrient.demo.service.EditorService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/editor")
public class EditorController {

    private final EditorService editorService;

    public EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    @PostMapping("/add-annotations")
    public ResponseEntity<byte[]> addAnnotations(@RequestParam("file") MultipartFile file) {
        try {
            byte[] result = editorService.addAnnotations(file.getBytes());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=annotated.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/add-watermark")
    public ResponseEntity<byte[]> addWatermark(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "text", defaultValue = "CONFIDENTIAL") String text,
            @RequestParam(value = "fontSize", defaultValue = "48") int fontSize,
            @RequestParam(value = "rotation", defaultValue = "-45") double rotation) {
        try {
            byte[] result = editorService.addWatermark(file.getBytes(), text, fontSize, rotation);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=watermarked.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/merge")
    public ResponseEntity<byte[]> merge(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<byte[]> documents = new ArrayList<>();
            List<String> filenames = new ArrayList<>();
            for (MultipartFile file : files) {
                documents.add(file.getBytes());
                filenames.add(file.getOriginalFilename());
            }
            byte[] result = editorService.mergeDocuments(documents, filenames);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=merged.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/add-page")
    public ResponseEntity<byte[]> addPage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "width", defaultValue = "595") double width,
            @RequestParam(value = "height", defaultValue = "842") double height,
            @RequestParam(value = "index", defaultValue = "0") int index) {
        try {
            byte[] result = editorService.addPage(file.getBytes(), width, height, index);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=modified.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/metadata")
    public ResponseEntity<String> getMetadata(@RequestParam("file") MultipartFile file) {
        try {
            String metadata = editorService.getMetadata(file.getBytes());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(metadata);
        } catch (NutrientException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
