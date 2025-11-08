package org.monitor.controllers;

import org.monitor.configs.entities.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Rest Controller to retrieve JSON output collected in directory specified.
 */
@RestController
public class HealthFileRestController {

    private final AppConfig appConfig;
    private final ObjectMapper mapper;

    // Expected timestamp format in filename
    private final DateTimeFormatter fileDateFormatter;

    public HealthFileRestController(AppConfig appConfig, ObjectMapper mapper) {
        this.appConfig = appConfig;
        this.mapper = mapper;
        this.fileDateFormatter = DateTimeFormatter.ofPattern(appConfig.getCheck().getFileDateFormatter());
    }

    /**
     * get latest json file in directory
     * @return json output
     */
    @GetMapping(value = "/api/health-file-latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> getHealthFile() {
        try {
            File dir = new File(appConfig.getCheck().getOutputDir());

            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files == null || files.length == 0) return ResponseEntity.notFound().build();

            // Sort by last modified
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            File latestFile = files[0];

            FileSystemResource resource = new FileSystemResource(latestFile);
            return ResponseEntity.ok()
                    .contentLength(latestFile.length())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all json in output folder
     * @return list of all json output
     * @throws IOException when reading file value
     */
    @GetMapping(value = "/api/health-files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object>> getAllHealthFiles() throws IOException {
        File dir = new File(appConfig.getCheck().getOutputDir());
        if (!dir.exists() || !dir.isDirectory()) {
            return ResponseEntity.notFound().build();
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            return ResponseEntity.notFound().build();
        }

        List<Object> merged = new ArrayList<>();

        for (File file : files) {
            List fileContents = mapper.readValue(file, List.class);
            merged.addAll(fileContents);
        }

        return ResponseEntity.ok(merged);
    }

    /**
     * <p>
     *      Method to get the logs specified by datetime,date or time.
     *      If partial match, get latest date/time e.g. 08112025 returns the latest timestamp of the day.
     * </p>
     * <p>
     *      GET /api/health-file-by-date?datetime=081120251430
     * </p>
     * @param partialStr datetime, date or time as param request
     * @return file with datetime stamp closest to description specified by param
     */
    @GetMapping(value = "/api/health-file-by-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFileByPartialDate(
            @RequestParam("datetime") String partialStr) {

        try {
            File dir = new File(appConfig.getCheck().getOutputDir());
            if (!dir.exists() || !dir.isDirectory()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Output directory not found"));
            }

            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files == null || files.length == 0) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "No JSON files found"));
            }

            // Filter files that **contain the partial string** in the timestamp
            List<File> matchingFiles = Arrays.stream(files)
                    .filter(f -> f.getName().contains(partialStr))
                    .collect(Collectors.toList());

            if (matchingFiles.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "No matching files found", "partial", partialStr));
            }

            // Sort by timestamp in filename descending to get the latest
            matchingFiles.sort((f1, f2) -> {
                String t1 = f1.getName().replace("health-", "").replace(".json", "");
                String t2 = f2.getName().replace("health-", "").replace(".json", "");
                return t2.compareTo(t1);
            });

            File latestFile = matchingFiles.get(0);

            return ResponseEntity.ok()
                    .contentLength(latestFile.length())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new FileSystemResource(latestFile));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    "Please format date query as " + this.fileDateFormatter);
        }
    }
}


