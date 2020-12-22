package com.spring.files.upload.service.helpers;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
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

    public void handleTextFiles(MultipartFile file, boolean encrypt) {
        try {
            String value = encrypt ? "encrypted-" : "decrypted-";
            new File("uploads/encrypted-" + file.getOriginalFilename());
            FileWriter writer = new FileWriter("uploads/" + value + file.getOriginalFilename());
            if (encrypt) {
                writer.write(cipherHelper.encrypt(new String(file.getBytes(), StandardCharsets.UTF_8)));
            } else {
                writer.write(cipherHelper.decrypt(new String(file.getBytes(), StandardCharsets.UTF_8)));
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

    public void handleDocxFiles(String fileName, boolean encrypt) {
        List<XWPFParagraph> paragraphs = readDocxFile(fileName);
        if (encrypt) {
            createEncryptedDocx(fileName, paragraphs);
        } else {
            createDecryptedDocx(fileName, paragraphs);
        }
    }

    private void createEncryptedDocx(String fileName, List<XWPFParagraph> paragraphs) {
        XWPFDocument document = new XWPFDocument();

        try (FileOutputStream fileOutputStream = new FileOutputStream(
                new File("C:\\Users\\david.ghiurau\\Downloads\\Facultate\\Anul 4\\TSD\\file-encryption-project\\uploads\\encrypted-" + fileName))) {

            paragraphs.forEach(paragraphToAdd -> document.createParagraph().createRun().setText(cipherHelper.encrypt(paragraphToAdd.getText())));

            document.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDecryptedDocx(String fileName, List<XWPFParagraph> paragraphs) {
        XWPFDocument document = new XWPFDocument();

        try (FileOutputStream fileOutputStream = new FileOutputStream(
                new File("C:\\Users\\david.ghiurau\\Downloads\\Facultate\\Anul 4\\TSD\\file-encryption-project\\uploads\\decrypted-" + fileName))) {

            paragraphs.forEach(paragraphToAdd -> document.createParagraph().createRun().setText(cipherHelper.decrypt(paragraphToAdd.getText())));

            document.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlePdfFiles(String fileName, boolean encrypt) throws IOException, DocumentException {
        PdfReader reader;

        try {

            reader = new PdfReader("C:\\Users\\david.ghiurau\\Downloads\\Facultate\\Anul 4\\" + fileName);

            String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);
            if (encrypt) {
                createEncryptedPdf(fileName, textFromPage);
            } else {
                createDecryptedPdf(fileName, textFromPage);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDecryptedPdf(String fileName, String content) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filesStorageService.getRoot().toAbsolutePath() + "\\decrypted-" + fileName));

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);

        Arrays.asList(content.split("\n")).forEach(contentPart -> {
            try {
                document.add(new Paragraph(cipherHelper.decrypt(contentPart), font));
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            document.close();
        });
    }

    private void createEncryptedPdf(String fileName, String content) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filesStorageService.getRoot().toAbsolutePath() + "\\encrypted-" + fileName));

        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);

        Arrays.asList(content.split("\n")).forEach(contentPart -> {
            try {
                document.add(new Paragraph(cipherHelper.encrypt(contentPart), font));
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        });

        document.close();
//        PdfReader pdfReader = new PdfReader(filesStorageService.getRoot().toAbsolutePath() + "\\encrypted-" + fileName);
//        PdfStamper pdfStamper
//                = new PdfStamper(pdfReader, new FileOutputStream(filesStorageService.getRoot().toAbsolutePath() + "\\encrypted2-" + fileName));
//
//        pdfStamper.setEncryption(
//                "userpass".getBytes(),
//                "test".getBytes(),
//                0,
//                PdfWriter.ENCRYPTION_AES_256
//        );
//
//        pdfStamper.close();
    }
}
