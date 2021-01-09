package com.spring.files.upload.controller;

import com.spring.files.upload.message.ResponseMessage;
import com.spring.files.upload.model.FileInfo;
import com.spring.files.upload.model.UserCredentials;
import com.spring.files.upload.service.FilesStorageService;
import com.spring.files.upload.service.UserService;
import com.spring.files.upload.service.helpers.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class FilesController {

    @Autowired
    FilesStorageService storageService;

    @Autowired
    private UserService userService;

    private FileHelper fileHelper = new FileHelper();

    @GetMapping("/users")
    public ResponseEntity<List<UserCredentials>> getUsers() {
        return ResponseEntity.ok().body(this.userService.getUsers());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserCredentials userCredentials) {
        if (this.userService.signUp(userCredentials)) {
            return ResponseEntity.ok().body("User successfully created!");
        }
        return ResponseEntity.badRequest().body("E-mail already used!");
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody UserCredentials userCredentials) {
        return ResponseEntity.ok().body(this.userService.login(userCredentials));
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, boolean encrypt, String email) {

        String message = "";
        try {
            if (Objects.requireNonNull(file.getOriginalFilename()).contains(".txt")) {
                fileHelper.handleTextFiles(file, encrypt, userService.getPrivateSecretKey(email));
            } else if (Objects.requireNonNull(file.getOriginalFilename()).contains(".doc")) {
                this.storageService.save(file);
                fileHelper.handleDocxFiles(file.getOriginalFilename(), encrypt, userService.getPrivateSecretKey(email));
                this.storageService.deleteFile(file.getOriginalFilename());
            } else {
                fileHelper.handlePdfFiles(file.getOriginalFilename(), encrypt, userService.getPrivateSecretKey(email));
            }
            message = "Processed the file " + file.getOriginalFilename() + " successfully!";
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not process the file " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @DeleteMapping("/files/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) throws IOException {
        storageService.deleteFile(filename);
        return ResponseEntity.ok().body("File was deleted successfully!");
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/files")
    public ResponseEntity<?> deleteFiles() {
        storageService.deleteAll();
        storageService.init();
        return ResponseEntity.ok().body("Files were deleted successfully!");
    }
}
