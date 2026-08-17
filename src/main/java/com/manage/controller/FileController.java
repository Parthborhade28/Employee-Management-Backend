package com.manage.controller;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.core.io.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.service.FileService;
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {

        Resource resource = fileService.downloadFile(fileName);

        String contentType = Files.probeContentType(resource.getFile().toPath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}