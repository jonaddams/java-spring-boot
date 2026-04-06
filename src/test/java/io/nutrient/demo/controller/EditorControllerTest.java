package io.nutrient.demo.controller;

import io.nutrient.demo.service.EditorService;
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

@WebMvcTest(EditorController.class)
class EditorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EditorService editorService;

    @Test
    void getMetadata_returnsXml() throws Exception {
        String fakeXml = "<xmp>metadata</xmp>";
        when(editorService.getMetadata(any(byte[].class))).thenReturn(fakeXml);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/editor/metadata").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/xml"));
    }
}
