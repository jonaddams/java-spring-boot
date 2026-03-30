package io.nutrient.demo.controller;

import io.nutrient.demo.service.SigningService;
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

@WebMvcTest(SigningController.class)
class SigningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SigningService signingService;

    @Test
    void sign_returnsSignedPdf() throws Exception {
        byte[] fakePdf = "signed-pdf".getBytes();
        when(signingService.signDocument(any(byte[].class), any(byte[].class), anyString()))
                .thenReturn(fakePdf);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                "application/pdf", "pdf-content".getBytes());
        MockMultipartFile cert = new MockMultipartFile("certificate", "cert.p12",
                "application/x-pkcs12", "cert-data".getBytes());

        mockMvc.perform(multipart("/api/signing/sign")
                        .file(file)
                        .file(cert)
                        .param("password", "secret"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }
}
