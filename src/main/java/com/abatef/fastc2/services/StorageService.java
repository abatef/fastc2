package com.abatef.fastc2.services;


import com.abatef.fastc2.dtos.FileUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface StorageService {
    FileUploadResponse upload(MultipartFile file) throws IOException;
}
