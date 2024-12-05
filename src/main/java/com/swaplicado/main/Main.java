/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swaplicado.main;

import com.swaplicado.cloudstoragemanager.CloudStorageManager;
import com.swaplicado.data.StorageManagerException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edwin Carmona
 */
public class Main {

    public static void main(String[] args) {
        try {
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\app-compras.pdf", "archivo1.pdf");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\app-compras1.pdf", "archivo2.pdf");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\tema.jpg", "archivo3.jpg");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\pghmovilversion1.apk", "archivo4.apk");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\favicon.png", "archivo5.png");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\node-v20.18.0-x64.msi", "archivo6.msi");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\cloud_sql_proxy.exe", "archivo7.exe");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\oc.csv", "archivo8.csv");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\chromedriver-win64.zip", "archivo9.zip");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\helm-v3.15.3-windows-amd64.zip", "archivo10.zip");
            CloudStorageManager.uploadObject("C:\\Users\\Edwin Carmona\\Downloads\\Best Backend Programming Languages in 2021 - table 1.png", "archivo10.png");

//            System.out.println(CloudStorageManager.generatePresignedUrl("archivo1.pdf"));
//            System.out.println(CloudStorageManager.generatePresignedUrl("archivo2.pdf"));
//            System.out.println(CloudStorageManager.generatePresignedUrl("archivo3.jpg"));
//            System.out.println(CloudStorageManager.generatePresignedUrl("archivo4.apk"));
//            System.out.println(CloudStorageManager.generatePresignedUrl("archivo5.png"));
        } catch (StorageManagerException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
