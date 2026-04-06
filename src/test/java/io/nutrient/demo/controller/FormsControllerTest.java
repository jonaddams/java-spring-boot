package io.nutrient.demo.controller;

import io.nutrient.demo.service.FormsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FormsController.class)
class FormsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FormsService formsService;

    @Test
    void listFields_returnsJson() throws Exception {
        when(formsService.listFormFields(any(byte[].class)))
                .thenReturn(java.util.List.of(
                        java.util.Map.of("name", "firstName", "type", "PdfTextFormField")));

        MockMultipartFile file = new MockMultipartFile("file", "form.pdf",
                "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/forms/list-fields").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
