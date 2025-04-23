package periskal.academy.backend.service.FtpService;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FTPService {
    @Value("${ftp.server}") private String server;
    @Value("${ftp.port}") private int port;
    @Value("${ftp.username}") private String user;
    @Value("${ftp.password}") private String password;

    public String uploadVideo(MultipartFile file) throws IOException {
        FTPSClient ftp = new FTPSClient("SSL", false);
        try {
            // Connect to FTP
            ftp.connect(server, port);
            ftp.login(user, password);
            ftp.enterLocalPassiveMode();
            
            // Navigate to group directory
            String baseDir = "Groep14/Videos";
            if (!ftp.changeWorkingDirectory(baseDir)) {
                ftp.makeDirectory("Groep14");
                ftp.changeWorkingDirectory("Groep14");
                ftp.makeDirectory("Videos");
            }
            
            // Generate unique filename
            String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = UUID.randomUUID() + extension;
            
            // Upload file
            boolean success = ftp.storeFile(filename, file.getInputStream());
            if (!success) {
                throw new IOException("FTP upload failed");
            }
            
            return filename;
        } finally {
            if (ftp.isConnected()) {
                ftp.disconnect();
            }
        }
    }
}