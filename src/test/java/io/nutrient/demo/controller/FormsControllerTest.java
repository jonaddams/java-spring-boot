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
    void fillForm_returnsPdf() throws Exception {
        byte[] fakePdf = "filled-pdf".getBytes();
        when(formsService.fillFormFields(any(byte[].class), anyMap())).thenReturn(fakePdf);

        MockMultipartFile file = new MockMultipartFile("file", "form.pdf",
                "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/forms/fill")
                        .file(file)
                        .param("fieldValues", "{\"name\":\"John\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }
}
