package org.example.educonnectjavaproject.files;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class FileUploadProcess {


    public String saveFileToDisk(MultipartFile file) {
        try {
            String uploadDir = "src/main/resources/static/uploads/";
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Ֆայլը դատարկ է");
            }
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isEmpty()) {
                throw new IllegalArgumentException("Ֆայլի անունը դատարկ է");
            }
            String fileExtension = "";
            int lastDotIndex = originalFileName.lastIndexOf('.');
            if (lastDotIndex != -1 && lastDotIndex < originalFileName.length() - 1) {
                fileExtension = originalFileName.substring(lastDotIndex).toLowerCase();
            } else {
                throw new IllegalArgumentException("Ֆայլը չունի վավեր ընդլայնում");
            }

            LocalDate localDate = LocalDate.now();
            String fileName = System.currentTimeMillis() + "_file_" +
                    localDate.getYear() + "_" +
                    localDate.getMonthValue() + "_" +
                    localDate.getDayOfMonth() + fileExtension;

            fileName = fileName.replaceAll("\\s+", "_");
            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            System.out.println("Saved file: " + fileName);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Ֆայլի պահպանումը ձախողվեց: " + e.getMessage());
        }
    }



    public String deleteFileFromDisk(String fileName) {
        try {
            String uploadDir = "src/main/resources/static/uploads/";
            Path path = Paths.get(uploadDir + fileName);
            Files.delete(path);
            return "file has been deleted";
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String deleteProfileImage(String fileName) {
        try {

            String uploadDir = "src/main/resources/static/images/";
            Path path = Paths.get(uploadDir + fileName);
            Files.delete(path);
            return "file has been deleted";
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
