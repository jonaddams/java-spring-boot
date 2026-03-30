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
    void addAnnotations_returnsPdf() throws Exception {
        byte[] fakePdf = "annotated-pdf".getBytes();
        when(editorService.addAnnotations(any(byte[].class))).thenReturn(fakePdf);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/editor/add-annotations").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
    void merge_returnsMergedPdf() throws Exception {
        byte[] fakePdf = "merged-pdf".getBytes();
        when(editorService.mergeDocuments(anyList(), anyList())).thenReturn(fakePdf);

        MockMultipartFile file1 = new MockMultipartFile("files", "doc1.pdf",
                "application/pdf", "pdf1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "doc2.pdf",
                "application/pdf", "pdf2".getBytes());

        mockMvc.perform(multipart("/api/editor/merge").file(file1).file(file2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }

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
