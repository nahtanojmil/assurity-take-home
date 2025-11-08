package org.monitor.controllers;

import org.junit.jupiter.api.io.TempDir;
import org.monitor.configs.SecurityConfig;
import org.monitor.configs.entities.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthFileRestController.class)
@Import({HealthFileRestControllerTestConfiguration.class, SecurityConfig.class})
class HealthFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppConfig appConfig;

    @MockBean
    private ObjectMapper objectMapper;

    private File testFile;

    private static final String USERNAME = "USER";
    private static final String PASSWORD = "s3creTpW!";

    @TempDir
    File tempDir;

    @BeforeEach
    void setup() throws Exception {
        // Override AppConfig outputDir to temp directory
        appConfig.getCheck().setOutputDir(tempDir.getAbsolutePath());

        // Create a dummy JSON file in tempDir
        File tempFile = new File(tempDir, "081120251200.json");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("[{\"name\":\"user-service\",\"status\":\"OK\"}]");
        }
    }

    @Test
    void testGetLatestFileWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/health-file-latest")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testGetLatestFile() throws Exception {
        mockMvc.perform(get("/api/health-file-latest")
                        .with(httpBasic(USERNAME,PASSWORD))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetFileByDateNotFound() throws Exception {
        mockMvc.perform(get("/api/health-file-by-date")
                        .with(httpBasic(USERNAME,PASSWORD))
                        .param("datetime", "010120001200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No matching files found"));
    }
}
