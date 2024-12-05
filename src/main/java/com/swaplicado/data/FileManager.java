package com.swaplicado.data;

public class FileManager {
    
    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String generateFileID(String path) {
        try {
            String extension = FileManager.getFileExtension(path);
            String id = java.util.UUID.randomUUID().toString() + "." + extension;
            
            return  id;
        } catch (Exception e) {
            System.err.println("Error al generar el id: " + e.getMessage());
        }
        
        return null;
    }
}
