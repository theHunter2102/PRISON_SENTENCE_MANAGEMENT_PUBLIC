package com.example.psmsystem.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageFileManagement {
    private final String folderPath = "C:\\MyImages"; // Đường dẫn của thư mục chứa ảnh

    public void saveImageToFolder(String imageName) {
        saveImage(folderPath + File.separator + imageName);
    }

    public void saveImage(String filePath) {
        try {
            // Đường dẫn của tệp tin
            File file = new File(filePath);

            // Đảm bảo thư mục tồn tại
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // Tạo thư mục nếu chưa tồn tại
            }

            // Đọc tập tin ảnh từ nguồn (ví dụ: một tập tin ảnh hiện đang được hiển thị trong ứng dụng)
            FileInputStream inputStream = new FileInputStream("sourceImagePath");

            // Sao chép dữ liệu từ nguồn sang đích (tệp tin đã được chỉ định)
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();

            System.out.println("Image saved successfully at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
