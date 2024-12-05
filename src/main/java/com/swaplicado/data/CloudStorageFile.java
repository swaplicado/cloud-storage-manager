/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swaplicado.data;

import java.util.Date;

/**
 *
 * @author Edwin Carmona
 */
public class CloudStorageFile {
    private final String id;
    private String fileName;
    private String bucketName;
    private String projectId;
    private String filePath;
    private Date uploadDate_n;
    private Date downloadedDate_n;

    public CloudStorageFile(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getUploadDate_n() {
        return uploadDate_n;
    }

    public void setUploadDate_n(Date uploadDate_n) {
        this.uploadDate_n = uploadDate_n;
    }

    public Date getDownloadedDate_n() {
        return downloadedDate_n;
    }

    public void setDownloadedDate_n(Date downloadedDate_n) {
        this.downloadedDate_n = downloadedDate_n;
    }
}
