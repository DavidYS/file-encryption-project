package com.spring.files.upload.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
  void init();

  void save(MultipartFile file);

  Resource load(String filename);

  void deleteAll();

  void deleteFile(String filename) throws IOException;

  Stream<Path> loadAll();
}
