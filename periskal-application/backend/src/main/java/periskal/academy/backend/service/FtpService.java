package periskal.academy.backend.service;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class FtpService {

    private static final Logger logger = LoggerFactory.getLogger(FtpService.class);

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

        logger.info("Starting FTP upload for file: {}, size: {} bytes", remoteFileName, file.getSize());

        File tempFile;
        try {
            tempFile = convertMultiPartToFile(file);
            logger.info("Created temporary file: {}", tempFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to create temporary file: {}", e.getMessage(), e);
            future.completeExceptionally(e);
            return future;
        }

        Thread ftpThread = new Thread(() -> {
            FTPSClient ftp = null;
            try {
                ftp = createAndConfigureClient();
                String remotePath = ftpUploadFolder + (ftpUploadFolder.endsWith("/") ? "" : "/") + remoteFileName; // Ensure
                                                                                                                   // correct
                                                                                                                   // path
                logger.info("Uploading file to: {}", remotePath);

                createFtpDirectory(ftp, ftpUploadFolder);

                boolean success = ftp.storeFile(remotePath, new FileInputStream(tempFile));
                if (success) {
                    logger.info("File uploaded successfully to: {}", remotePath);
                    URI ftpUri = new URI("ftp", ftpUser, ftpHost, ftpPort, remotePath, null, null);
                    future.complete(ftpUri);
                } else {
                    String errorMessage = "Failed to upload file to FTP. Reply code: " + ftp.getReplyCode();
                    logger.error(errorMessage);
                    future.completeExceptionally(new IOException(errorMessage));
                }
            } catch (Exception e) {
                logger.error("Error in FTP upload: {}", e.getMessage(), e);
                future.completeExceptionally(e);
            } finally {
                disconnectFtpClient(ftp);
                if (tempFile.exists() && !tempFile.delete()) {
                    logger.warn("Could not delete temporary file: {}", tempFile.getAbsolutePath());
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

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            boolean success = ftp.retrieveFile(remotePath, outputStream);
            if (!success) {
                throw new IOException("Failed to retrieve file: " + remotePath);
            }

            return new ByteArrayResource(outputStream.toByteArray());
        } finally {
            disconnectFtpClient(ftp);
        }
    }

    public FTPSClient createAndConfigureClient() throws SocketException, IOException {
        FTPSClient ftp = new FTPSClient("SSL", false);
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

        ftp.setConnectTimeout(connectionTimeout);
        ftp.setDefaultTimeout(connectionTimeout);
        ftp.setDataTimeout(Duration.ofMillis(dataTimeout));

        logger.info("Connecting to FTP server {}:{}", ftpHost, ftpPort);
        ftp.connect(ftpHost, ftpPort);

        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server. Reply code: " + reply);
        }

        logger.info("Logging in with user: {}", ftpUser);
        if (!ftp.login(ftpUser, ftpPassword)) {
            ftp.disconnect();
            throw new IOException("FTP login failed");
        }

        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.execPBSZ(0);
        ftp.execPROT("P");
        ftp.enterLocalPassiveMode();

        logger.info("Connected to FTP Server");
        return ftp;
    }

    private void createFtpDirectory(FTPSClient ftp, String dirPath) throws IOException {
        String[] directories = dirPath.split("/");
        String currentPath = "";
        for (String dir : directories) {
            if (!dir.isEmpty()) {
                currentPath += "/" + dir;
                if (!ftp.changeWorkingDirectory(currentPath)) {
                    ftp.makeDirectory(currentPath);
                    ftp.changeWorkingDirectory(currentPath);
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
                logger.info("FTP client disconnected");
            } catch (IOException e) {
                logger.warn("Error during FTP disconnection: {}", e.getMessage());
            }
        }
    }
}