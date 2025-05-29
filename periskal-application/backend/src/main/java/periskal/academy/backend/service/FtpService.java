package periskal.academy.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FtpService {

    @Value("${ftp.host}")
    public String ftpHost;

    @Value("${ftp.user}")
    public String ftpUser;

    @Value("${ftp.password}")
    public String ftpPassword;

    @Value("${ftp.upload-folder}")
    public String ftpUploadFolder;

    @Value("${ftp.port:9910}")
    public int ftpPort;

    @Value("${ftp.connection-timeout:10000}")
    public int connectionTimeout;

    @Value("${ftp.data-timeout:30000}")
    public int dataTimeout;

    @Async
    public CompletableFuture<URI> uploadFile(MultipartFile file, String remoteFileName) {
        CompletableFuture<URI> future = new CompletableFuture<>();

        log.info("Starting FTP upload for file: {}, size: {} bytes", remoteFileName, file.getSize());

        File tempFile;
        try {
            tempFile = convertMultiPartToFile(file);
            log.info("Created temporary file: {}", tempFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create temporary file: {}", e.getMessage(), e);
            future.completeExceptionally(e);
            return future;
        }

        Thread ftpThread = new Thread(() -> {
            FTPSClient ftp = null;
            try (FileInputStream fis = new FileInputStream(tempFile)){
                ftp = createAndConfigureClient();
                String remotePath = ftpUploadFolder + (ftpUploadFolder.endsWith("/") ? "" : "/") + remoteFileName; // Ensure
                                                                                                                   // correct
                                                                                                                   // path
                log.info("Uploading file to: {}", remotePath);

                createFtpDirectory(ftp, ftpUploadFolder);

                boolean success = ftp.storeFile(remotePath, fis);
                if (success) {
                    log.info("File uploaded successfully to: {}", remotePath);
                    URI ftpUri = new URI("ftp", ftpUser, ftpHost, ftpPort, remotePath, null, null);
                    future.complete(ftpUri);
                } else {
                    String errorMessage = "Failed to upload file to FTP. Reply code: " + ftp.getReplyCode();
                    log.error(errorMessage);
                    future.completeExceptionally(new IOException(errorMessage));
                }
            } catch (Exception e) {
                log.error("Error in FTP upload: {}", e.getMessage(), e);
                future.completeExceptionally(e);
            } finally {
                disconnectFtpClient(ftp);
                if (tempFile.exists()) {
                    try {
                        Files.delete(tempFile.toPath());
                    } catch (Exception deleteEx) {
                        log.warn("Could not delete temporary file: {}", tempFile.getAbsolutePath(), deleteEx);
                    }
                }
            }
        });

        ftpThread.setName("ftp-upload-" + remoteFileName);
        ftpThread.start();
        return future;
    }

    public Resource downloadFile(String filePath) throws IOException {
        FTPSClient ftp = createAndConfigureClient();
        try {
            // Extract relative path from URI if necessary
            String remotePath = filePath.startsWith("ftp://") ? filePath.substring(filePath.indexOf(ftpUploadFolder))
                    : filePath;


            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                boolean success = ftp.retrieveFile(remotePath, outputStream);
                if (!success) {
                    throw new IOException("Failed to retrieve file: " + remotePath);
                }
                byte[] fileData = outputStream.toByteArray();
                return new ByteArrayResource(fileData);
            }  
        } finally {
            disconnectFtpClient(ftp);
        }
    }

    public FTPSClient createAndConfigureClient() throws IOException {
        FTPSClient ftp = new FTPSClient("SSL", false);
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

        ftp.setConnectTimeout(connectionTimeout);
        ftp.setDefaultTimeout(connectionTimeout);
        ftp.setDataTimeout(Duration.ofMillis(dataTimeout));

        log.info("Connecting to FTP server {}:{}", ftpHost, ftpPort);
        ftp.connect(ftpHost, ftpPort);

        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server. Reply code: " + reply);
        }

        log.info("Logging in with user: {}", ftpUser);
        if (!ftp.login(ftpUser, ftpPassword)) {
            ftp.disconnect();
            throw new IOException("FTP login failed");
        }

        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.execPBSZ(0);
        ftp.execPROT("P");
        ftp.enterLocalPassiveMode();

        log.info("Connected to FTP Server");
        return ftp;
    }

    private void createFtpDirectory(FTPSClient ftp, String dirPath) throws IOException {
        String[] directories = dirPath.split("/");
        StringBuilder currentPath = new StringBuilder();

        for (String dir : directories) {
            if (!dir.isEmpty()) {
                currentPath.append("/").append(dir);
                String pathSoFar = currentPath.toString();
                if (!ftp.changeWorkingDirectory(pathSoFar)) {
                    ftp.makeDirectory(pathSoFar);
                    ftp.changeWorkingDirectory(pathSoFar);
                }
            }
        }
    }

    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("ftp-upload-", "-" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }

    public void disconnectFtpClient(FTPSClient ftp) {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
                log.info("FTP client disconnected");
            } catch (IOException e) {
                log.warn("Error during FTP disconnection: {}", e.getMessage());
            }
        }
    }
}