package com.spring.files.upload.service.helpers;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.spring.files.upload.service.FilesStorageServiceImpl;
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
import java.util.Arrays;
import java.util.List;

public class FileHelper {

    private FilesStorageServiceImpl filesStorageService = new FilesStorageServiceImpl();

    private CipherHelper cipherHelper = new CipherHelper();

    public void handleTextFiles(MultipartFile file, boolean encrypt, String secretKey) {
        try {
            String value = encrypt ? "encrypted-" : "decrypted-";
            new File("uploads/encrypted-" + file.getOriginalFilename());
            FileWriter writer = new FileWriter("uploads/" + value + file.getOriginalFilename());
            if (encrypt) {
                writer.write(cipherHelper.encrypt(new String(file.getBytes(), StandardCharsets.UTF_8), secretKey));
            } else {
                writer.write(cipherHelper.decrypt(new String(file.getBytes(), StandardCharsets.UTF_8), secretKey));
            }
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

    public void handleDocxFiles(String fileName, boolean encrypt, String secretKey) {
        List<XWPFParagraph> paragraphs = readDocxFile(fileName);
        if (encrypt) {
            createEncryptedDocx(fileName, paragraphs, secretKey);
        } else {
            createDecryptedDocx(fileName, paragraphs, secretKey);
        }
    }

    private void createEncryptedDocx(String fileName, List<XWPFParagraph> paragraphs, String secretKey) {
        XWPFDocument document = new XWPFDocument();

        try (FileOutputStream fileOutputStream = new FileOutputStream(
                new File("C:\\Users\\david.ghiurau\\Downloads\\Facultate\\Anul 4\\TSD\\file-encryption-project\\uploads\\encrypted-" + fileName))) {

            paragraphs.forEach(paragraphToAdd -> document.createParagraph().createRun().setText(cipherHelper.encrypt(paragraphToAdd.getText(), secretKey)));

            document.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDecryptedDocx(String fileName, List<XWPFParagraph> paragraphs, String secretKey) {
        XWPFDocument document = new XWPFDocument();

        try (FileOutputStream fileOutputStream = new FileOutputStream(
                new File(filesStorageService.getRoot().toAbsolutePath() + "\\decrypted-" + fileName))) {

            paragraphs.forEach(paragraphToAdd -> document.createParagraph().createRun().setText(cipherHelper.decrypt(paragraphToAdd.getText(), secretKey)));

            document.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlePdfFiles(String fileName, boolean encrypt, String secretKey) throws DocumentException {
        PdfReader reader;

        try {

            reader = new PdfReader("C:\\Users\\david.ghiurau\\Downloads\\Facultate\\Anul 4\\" + fileName);

            String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);
            if (encrypt) {
                createEncryptedPdf(fileName, textFromPage, secretKey);
            } else {
                createDecryptedPdf(fileName, textFromPage, secretKey);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDecryptedPdf(String fileName, String content, String secretKey) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filesStorageService.getRoot().toAbsolutePath() + "\\decrypted-" + fileName));

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);

        Arrays.asList(content.split("\n")).forEach(contentPart -> {
            try {
                document.add(new Paragraph(cipherHelper.decrypt(contentPart, secretKey), font));
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            document.close();
        });
    }

    private void createEncryptedPdf(String fileName, String content, String secretKey) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filesStorageService.getRoot().toAbsolutePath() + "\\encrypted-" + fileName));

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);

        Arrays.asList(content.split("\n")).forEach(contentPart -> {
            try {
                document.add(new Paragraph(cipherHelper.encrypt(contentPart, secretKey), font));
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        });

        document.close();
    }
}
