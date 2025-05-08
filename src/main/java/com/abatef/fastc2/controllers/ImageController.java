package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.FileUploadResponse;
import com.abatef.fastc2.enums.UploadStatus;
import com.abatef.fastc2.exceptions.DrugNotFoundException;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.Image;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.DrugRepository;
import com.abatef.fastc2.repositories.ImageRepository;
import com.abatef.fastc2.services.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private final StorageService storageService;
    private final DrugRepository drugRepository;
    private final ImageRepository imageRepository;

    public ImageController(
            StorageService storageService,
            DrugRepository drugRepository,
            ImageRepository imageRepository) {
        this.storageService = storageService;
        this.drugRepository = drugRepository;
        this.imageRepository = imageRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("drug_id") Integer drugId,
            @AuthenticationPrincipal User user)
            throws IOException {
        FileUploadResponse fileUploadResponse = storageService.upload(file);
        if (fileUploadResponse.getStatus() == UploadStatus.SUCCESS) {
            Optional<Drug> drugOptional = drugRepository.findById(drugId);
            if (drugOptional.isEmpty()) {
                throw new DrugNotFoundException(drugId);
            }
            Drug drug = drugOptional.get();
            Image image = new Image();
            image.setCreatedBy(user);
            image.setUrl(fileUploadResponse.getFileUrl());
            image.setDrug(drug);
            imageRepository.save(image);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileUploadResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fileUploadResponse);
    }

    @GetMapping
    @Operation(summary = "get all urls of the images of the drug with drug_id")
    public List<String> getAllImages(@RequestParam("drug_id") Integer drugId) {
        List<Image> images = imageRepository.findAllByDrug_Id(drugId);
        return images.stream().map(Image::getUrl).collect(Collectors.toList());
    }
}
