package com.example.demo.utils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.exception.ApiException;

public class FileUpoladUtil {
    
    public static String uploadFile(MultipartFile file){   

        if(file == null || file.isEmpty() || file.getOriginalFilename() == null){
            return "";
        }

        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        if (!List.of(".jpg", ".jpeg", ".png", ".webp").contains(ext.toLowerCase())) {
            throw new ApiException(Map.of("file", "不支援的圖片格式"));
        }


        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File saveFile = new File(uploadDir + fileName);
        try {
            file.transferTo(saveFile);
            return "/uploads/" + fileName;
        } catch (Exception e) {
            throw new ApiException(Map.of("general", "上傳圖片失敗"));
        }

    }
}
