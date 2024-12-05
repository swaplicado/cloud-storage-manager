package com.swaplicado.cloudstoragemanager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.SignUrlOption;
import com.google.cloud.storage.StorageOptions;
import com.swaplicado.config.ConfigLoader;
import com.swaplicado.data.CloudStorageFile;
import com.swaplicado.data.FileManager;
import com.swaplicado.data.StorageManagerException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CloudStorageManager {

    private static final ConfigLoader oConfigLoader = new ConfigLoader();

    /**
     * La función `uploadObject` sube un archivo local a un bucket de Google
     * Cloud Storage y devuelve un objeto `CloudStorageFile` con los detalles
     * del archivo almacenado.
     *
     * @param filePath El parámetro `filePath` representa la ruta del archivo
     * local que se desea subir al bucket de almacenamiento. Especifica la
     * ubicación en el sistema de archivos del archivo que se va a cargar.
     *
     * @param objectName El parámetro `objectName` es el nombre que se asignará
     * al objeto (archivo) en el bucket de almacenamiento. Si es nulo o está
     * vacío, el método generará un ID de archivo basado en el `filePath`.
     *
     * @return Un objeto `CloudStorageFile` que contiene los detalles del
     * archivo subido, incluyendo su ID, nombre, ruta de archivo, nombre del
     * bucket y el ID del proyecto. Devuelve `null` si se produce un error
     * durante la carga del archivo.
     * @throws com.swaplicado.data.StorageManagerException
     */
    public static CloudStorageFile uploadObject(String filePath, String objectName) throws StorageManagerException {
        // Inicializa el cliente de almacenamiento
        Storage storage = StorageOptions.newBuilder().setProjectId(oConfigLoader.getProjectId()).build().getService();

        String fileId = "";
        if (objectName == null || objectName.isEmpty()) {
            fileId = FileManager.generateFileID(filePath);
        } else {
            fileId = objectName;
        }

        if (objectName == null) {
            return null;
        }

        // Identifica el archivo y el bucket donde se almacenará
        BlobId blobId = BlobId.of(oConfigLoader.getBucketName(), fileId);
        BlobInfo blobInfo = null;
        if (objectName.toLowerCase().endsWith(".pdf")) {
            String contentType = "application/pdf";
            blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .setContentDisposition("inline") // Asegura que los PDF se visualicen en lugar de descargarse
                    .build();
        } else {
            blobInfo = BlobInfo.newBuilder(blobId).build();
        }

        try {
            // Lee el archivo en formato de bytes y sube el objeto al bucket
            Path path = Paths.get(filePath);
            byte[] bytes = Files.readAllBytes(path);

            // Subir el archivo
            storage.create(blobInfo, bytes);

            CloudStorageFile oCloudStorageFile = new CloudStorageFile(fileId);
            oCloudStorageFile.setFileName(objectName);
            oCloudStorageFile.setFilePath(filePath);
            oCloudStorageFile.setBucketName(oConfigLoader.getBucketName());
            oCloudStorageFile.setProjectId(oConfigLoader.getProjectId());
            oCloudStorageFile.setUploadDate_n(new Date());

            System.out.println("El archivo fue subido a " + fileId + " en el bucket " + oConfigLoader.getBucketName());

            return oCloudStorageFile;

        } catch (Exception e) {
            System.err.println("Error al subir el archivo: " + e.getMessage());
            throw new StorageManagerException("Error al subir el archivo: " + e.getMessage());
        }
    }

    /**
     * La función `uploadFileData` sube un array de bytes a un bucket de Google
     * Cloud Storage y devuelve un objeto `CloudStorageFile` con los detalles
     * del archivo almacenado.
     *
     * @param fileData El parámetro `fileData` en el método `uploadFileData` es
     * un array de bytes que representa los datos del archivo que se desea subir
     * al bucket de almacenamiento. Es el contenido del archivo que se va a
     * cargar.
     *
     * @param objectName El parámetro `objectName` es el nombre que se asignará
     * al objeto (archivo) en el bucket de almacenamiento. Si es nulo o está
     * vacío, el método generará un ID de archivo basado en el `filePath`.
     *
     * @return Un objeto `CloudStorageFile` que contiene los detalles del
     * archivo subido, incluyendo su ID, nombre, ruta de archivo, nombre del
     * bucket y el ID del proyecto. Devuelve `null` si se produce un error
     * durante la carga del archivo.
     * @throws java.lang.Exception
     */
    public static CloudStorageFile uploadFileData(byte[] fileData, String objectName) throws Exception {
        try {
            // Inicializa el cliente de almacenamiento
            Storage storage = StorageOptions.newBuilder().setProjectId(oConfigLoader.getProjectId()).build().getService();

            if (objectName == null || objectName.isEmpty()) {
                throw new Exception("El nombre del archivo es obligatorio al subir array de datos.");
            }

            String fileId = objectName;

            // Identifica el archivo y el bucket donde se almacenará
            BlobId blobId = BlobId.of(oConfigLoader.getBucketName(), fileId);
            BlobInfo blobInfo;
            if (objectName.toLowerCase().endsWith(".pdf")) {
                String contentType = "application/pdf";
                blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType(contentType)
                        .setContentDisposition("inline") // Asegura que los PDF se visualicen en lugar de descargarse
                        .build();
            } else {
                blobInfo = BlobInfo.newBuilder(blobId).build();
            }

            // Subir el archivo
            storage.create(blobInfo, fileData);

            CloudStorageFile cloudStorageFile = new CloudStorageFile(fileId);
            cloudStorageFile.setFileName(objectName);
            cloudStorageFile.setBucketName(oConfigLoader.getBucketName());
            cloudStorageFile.setProjectId(oConfigLoader.getProjectId());
            cloudStorageFile.setUploadDate_n(new Date());

            System.out.println("Archivo subido exitosamente a Google Cloud Storage: " + fileId);

            return cloudStorageFile;

        } catch (Exception e) {
            System.err.println("Error al subir el archivo a Google Cloud Storage: " + e.getMessage());
            throw new StorageManagerException("Error al subir el archivo a Google Cloud Storage: " + e.getMessage());
        }
    }

    /**
     * La función `downloadObject` descarga un archivo de un bucket de Google
     * Cloud Storage a una ruta de destino especificada en el sistema de
     * archivos local.
     *
     * @param fileName El parámetro `fileName` en el método `downloadObject`
     * representa el nombre del objeto (archivo) que deseas descargar del bucket
     * de almacenamiento en la nube. Es el nombre del archivo que deseas
     * recuperar del bucket especificado.
     * @param destFilePath El parámetro `destFilePath` en el método
     * `downloadObject` es la ruta donde se guardará el archivo descargado en el
     * sistema de archivos local. Especifica la ruta de destino donde se
     * descargará el objeto.
     * @return Un objeto `CloudStorageFile` que contiene los detalles del
     * archivo subido, incluyendo su ID, nombre, ruta de archivo, nombre del
     * bucket y el ID del proyecto. Devuelve `null` si se produce un error
     * durante la carga del archivo.
     */
    public static CloudStorageFile downloadObject(String fileName, String destFilePath) {
        // Inicializa el cliente de almacenamiento
        try {
            Storage storage = StorageOptions.newBuilder().setProjectId(oConfigLoader.getProjectId()).build()
                    .getService();

            Blob blob = storage.get(oConfigLoader.getBucketName(), fileName);
            if (blob != null) {
                // Define la ruta de destino
                Path destPath = Paths.get(destFilePath);

                // Descarga el archivo en la ruta especificada
                blob.downloadTo(destPath);
                CloudStorageFile oCloudStorageFile = new CloudStorageFile(fileName);
                oCloudStorageFile.setFileName(fileName);
                oCloudStorageFile.setFilePath(destFilePath);
                oCloudStorageFile.setBucketName(oConfigLoader.getBucketName());
                oCloudStorageFile.setProjectId(oConfigLoader.getProjectId());
                oCloudStorageFile.setDownloadedDate_n(new Date());

                System.out.println("Archivo descargado a " + destFilePath);
                return oCloudStorageFile;
            } else {
                System.out.println(
                        "El objeto " + fileName + " no se encontró en el bucket " + oConfigLoader.getBucketName());
            }
        } catch (Exception e) {
            System.err.println("Error al subir el archivo: " + e.getMessage());
        }

        return null;
    }

    /**
     * La función `generatePresignedUrl` genera una URL temporal firmada para un
     * objeto en un bucket de almacenamiento de Google Cloud Storage.
     *
     * @param objectName El parámetro `objectName` en el método
     * `generatePresignedUrl` representa el nombre del objeto (archivo) para el
     * que se generará una URL temporal firmada. Es el nombre del archivo para
     * el que se desea generar la URL temporal.
     * @return La función `generatePresignedUrl` devuelve una cadena que
     * representa la URL temporal firmada para el objeto especificado.
     * @throws com.swaplicado.data.StorageManagerException
     */
    public static String generatePresignedUrl(String objectName) throws StorageManagerException {
        if (objectName == null || objectName.isEmpty()) {
            throw new StorageManagerException("El nombre de archivo es nulo o vacío.");
        }
//            System.out.println("project ID: " + oConfigLoader.getProjectId());
//            System.out.println("Bucket name: " + oConfigLoader.getBucketName());
//            if (StorageOptions.newBuilder() == null) {
//                throw new StorageManagerException("Builder nulo!");
//            }
//            if (BlobInfo.newBuilder(oConfigLoader.getBucketName(), objectName) == null) {
//                throw new StorageManagerException("BlobInfo nulo!");
//            }
        Storage storage = StorageOptions.newBuilder().setProjectId(oConfigLoader.getProjectId()).build().getService();
        BlobInfo blobInfo = BlobInfo.newBuilder(oConfigLoader.getBucketName(), objectName).build();

        if (blobInfo == null) {
            System.out.println("El objeto " + objectName + " no se encontró en el bucket " + oConfigLoader.getBucketName());
            return "";
        }

        long expirationTimeInMinutes = oConfigLoader.getUrlExpirationTimeMinutes();
        URL signedUrl = storage.signUrl(blobInfo, expirationTimeInMinutes, TimeUnit.MINUTES, SignUrlOption.withV4Signature());
        // Imprime la URL temporal
        //        System.out.println("Generated signed URL: " + signedUrl.toString());

        return signedUrl.toString();
    }

    /**
     * El método `downloadAndZipFiles` descarga una lista de archivos desde un
     * bucket de Google Cloud Storage y los comprime en un solo archivo `.zip`
     * en la ubicación especificada.
     *
     * @param fileNames Una lista de nombres de archivos que se desean descargar
     * desde el bucket de almacenamiento. Cada nombre corresponde a un archivo
     * que será recuperado y añadido al archivo `.zip`.
     *
     * @param zipFilePath La ruta de destino donde se guardará el archivo `.zip`
     * generado. Especifica el nombre y ubicación del archivo comprimido que
     * contendrá todos los archivos descargados.
     * @return Un objeto `CloudStorageFile` que contiene los detalles del
     * archivo subido, incluyendo su ID, nombre, ruta de archivo, nombre del
     * bucket y el ID del proyecto. Devuelve `null` si se produce un error
     * durante la carga del archivo.
     *
     * @throws IOException Si ocurre un error al leer o escribir archivos
     * durante el proceso de descarga o compresión.
     *
     * El método crea archivos temporales para cada archivo descargado y luego
     * los añade al archivo `.zip`. Después de añadir cada archivo, el archivo
     * temporal se elimina para liberar espacio.
     *
     * Si un archivo en la lista `fileNames` no se encuentra en el bucket, el
     * método muestra un mensaje indicando que el archivo no fue encontrado.
     */
    public static CloudStorageFile downloadAndZipFiles(List<String> fileNames, String zipFilePath) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            for (String fileName : fileNames) {
                Blob blob = storage.get(BlobId.of(oConfigLoader.getBucketName(), fileName));
                if (blob != null) {
                    Path tempFile = Files.createTempFile(null, null);
                    blob.downloadTo(tempFile);

                    // Agregar el archivo al zip
                    zos.putNextEntry(new ZipEntry(fileName));
                    Files.copy(tempFile, zos);
                    zos.closeEntry();

                    Files.delete(tempFile); // Eliminar el archivo temporal
                    System.out.println("Archivo " + fileName + " añadido al zip.");

                } else {
                    System.out.println("El archivo " + fileName + " no se encontró en el bucket.");
                }
            }

            CloudStorageFile oCloudStorageFile = new CloudStorageFile(FileManager.getFileName(zipFilePath));
            oCloudStorageFile.setFileName(FileManager.getFileName(zipFilePath));
            oCloudStorageFile.setFilePath(zipFilePath);
            oCloudStorageFile.setBucketName(oConfigLoader.getBucketName());
            oCloudStorageFile.setProjectId(oConfigLoader.getProjectId());
            oCloudStorageFile.setDownloadedDate_n(new Date());

            System.out.println("Archivos descargados y comprimidos en " + zipFilePath);

            return oCloudStorageFile;
        }
    }

    /**
     * La función `deleteFiles` elimina una lista de archivos del bucket de
     * almacenamiento de Google Cloud Storage.
     *
     * @param fileNames Una lista de nombres de archivos que se desean eliminar
     * del bucket de almacenamiento. Cada nombre corresponde a un archivo que
     * será eliminado del bucket.
     * @return Una cadena que contiene un mensaje que indica el resultado de la
     * operación de eliminación. Cada archivo se elimina y se agrega un mensaje
     * correspondiente. Si un archivo no se encuentra en el bucket, se agrega un
     * mensaje indicando que el archivo no fue encontrado.
     */
    public static String deleteFiles(List<String> fileNames) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String result = "";

        for (String fileName : fileNames) {
            Blob blob = storage.get(BlobId.of(oConfigLoader.getBucketName(), fileName));
            if (blob != null) {
                blob.delete();
                result += fileName + " eliminado del bucket.\n";
                System.out.println("Archivo " + fileName + " eliminado del bucket.");
            } else {
                result += "El archivo " + fileName + " no se encontró en el bucket.\n";
                System.out.println("El archivo " + fileName + " no se encontró en el bucket.");
            }
        }

        return result;
    }

    /**
     * Verifica si un archivo con el nombre dado ya existe en el bucket.
     *
     * @param objectName El nombre del archivo a verificar.
     * @return true si el archivo existe en el bucket, false en caso contrario.
     * @throws StorageManagerException Si ocurre algún problema al conectarse al
     * bucket o si el nombre es nulo/vacío.
     */
    public static boolean storagedFileExists(String objectName) throws StorageManagerException {
        if (objectName == null || objectName.isEmpty()) {
            throw new StorageManagerException("El nombre de archivo es nulo o vacío.");
        }

        try {
            // Crear el cliente de Google Cloud Storage
            Storage storage = StorageOptions.newBuilder()
                    .setProjectId(oConfigLoader.getProjectId())
                    .build()
                    .getService();

            // Intentar obtener el archivo del bucket
            Blob blob = storage.get(oConfigLoader.getBucketName(), objectName);

            // Si el blob no es null, significa que el archivo existe
            return blob != null;
        }
        catch (Exception e) {
            // Manejar excepciones y lanzar StorageManagerException si ocurre algún problema
            throw new StorageManagerException("Error al verificar si el archivo existe en el bucket: " + e.getMessage());
        }
    }

}
