package com.spring.files.upload.service.helpers;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    private CipherHelper cipherHelper = new CipherHelper();

    public void encryptTextFile(MultipartFile file) {
        try {
            new File("uploads/encrypted-" + file.getOriginalFilename());
            FileWriter writer = new FileWriter("uploads/encrypted-" + file.getOriginalFilename());
            writer.write(cipherHelper.encrypt(new String(file.getBytes(), StandardCharsets.UTF_8)));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<XWPFParagraph> readDocxFile(String fileName) {
        List<XWPFParagraph> paragraphs = new ArrayList<>();
        try {
            File file = new File("C:\\Users\\david.ghiurau\\Downloads\\Facultate\\Anul 4\\TSD\\file-encryption-project\\uploads\\" + fileName);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());

            paragraphs = new XWPFDocument(fis).getParagraphs();

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paragraphs;
    }

    public void encryptDocxFile(String fileName) {
        List<XWPFParagraph> paragraphs = readDocxFile(fileName);
        createEncryptedDocx(fileName, paragraphs);
    }

    private void createEncryptedDocx(String fileName, List<XWPFParagraph> paragraphs) {
        XWPFDocument document = new XWPFDocument();

        try (FileOutputStream fileOutputStream = new FileOutputStream(
                new File("C:\\Users\\david.ghiurau\\Downloads\\Facultate\\Anul 4\\TSD\\file-encryption-project\\uploads\\encrypted-" + fileName))) {

            paragraphs.forEach(paragraphToAdd -> document.createParagraph().createRun().setText(cipherHelper.encrypt(paragraphToAdd.toString())));

            document.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
