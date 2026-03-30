package io.nutrient.demo.controller;

import io.nutrient.demo.service.ExtractionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExtractionController.class)
class ExtractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExtractionService extractionService;

    @Test
    void ocrExtraction_returnsJson() throws Exception {
        Map<String, Object> fakeResult = Map.of(
                "engine", "OCR",
                "content", "Extracted text from document",
                "filename", "scan.pdf"
        );
        when(extractionService.extractTextOcr(any(byte[].class), anyString())).thenReturn(fakeResult);

        MockMultipartFile file = new MockMultipartFile("file", "scan.pdf",
                "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/extraction/ocr").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.engine").value("OCR"))
                .andExpect(jsonPath("$.content").value("Extracted text from document"));
    }

    @Test
    void icrExtraction_returnsJson() throws Exception {
        Map<String, Object> fakeResult = Map.of(
                "engine", "ICR",
                "content", "Handwritten text extracted",
                "filename", "form.png"
        );
        when(extractionService.extractTextIcr(any(byte[].class), anyString())).thenReturn(fakeResult);

        MockMultipartFile file = new MockMultipartFile("file", "form.png",
                "image/png", "image-bytes".getBytes());

        mockMvc.perform(multipart("/api/extraction/icr").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.engine").value("ICR"))
                .andExpect(jsonPath("$.content").value("Handwritten text extracted"));
    }
}
