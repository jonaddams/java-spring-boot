package io.nutrient.demo.controller;

import io.nutrient.demo.service.ConversionService;
import io.nutrient.sdk.exceptions.NutrientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConversionController.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversionService conversionService;

    @Test
    void docxToPdf_returnsPdf() throws Exception {
        byte[] fakePdf = "fake-pdf-content".getBytes();
        when(conversionService.convertToPdf(any(byte[].class), anyString())).thenReturn(fakePdf);

        MockMultipartFile file = new MockMultipartFile("file", "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "docx-content".getBytes());

        mockMvc.perform(multipart("/api/conversion/docx-to-pdf").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes(fakePdf));
    }

    @Test
    void pdfToHtml_returnsHtml() throws Exception {
        byte[] fakeHtml = "<html><body>Hello</body></html>".getBytes();
        when(conversionService.pdfToHtml(any(byte[].class))).thenReturn(fakeHtml);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/conversion/pdf-to-html").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html"));
    }

    @Test
    void docxToPdf_returnsErrorOnFailure() throws Exception {
        when(conversionService.convertToPdf(any(byte[].class), anyString()))
                .thenThrow(new NutrientException("SDK error"));

        MockMultipartFile file = new MockMultipartFile("file", "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "docx-content".getBytes());

        mockMvc.perform(multipart("/api/conversion/docx-to-pdf").file(file))
                .andExpect(status().isInternalServerError());
    }
}
