package periskal.academy.backend.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTPSClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FtpServiceTest {

    @InjectMocks
    private FtpService ftpService;

    @Mock
    private FTPSClient ftpClientMock;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ftpService.ftpHost = "localhost";
        ftpService.ftpUser = "user";
        ftpService.ftpPassword = "password";
        ftpService.ftpUploadFolder = "/uploads/";
        ftpService.ftpPort = 9910; // Aangepast naar 9910 zoals in de nieuwe implementatie
        ftpService.connectionTimeout = 10000;
        ftpService.dataTimeout = 30000;
    }

    @Test
    void GivenMultipartFile_thenConvertMultiPartToFile_whenCalled_shouldCreateTempFile()
            throws IOException {
        // Arrange
        byte[] content = "hello".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", content);

        // Act
        File file = ftpService.convertMultiPartToFile(multipartFile);

        // Assert
        assertThat(file).exists();
        assertThat(file.length()).isEqualTo(content.length);
        assertThat(file.getName()).contains("ftp-upload-").contains("test.txt");
        file.delete();
    }

    @Test
    void GivenConnectedFtpClient_whenDisconnectFtpClient_shouldLogoutAndDisconnect()
            throws IOException {
        // Arrange
        when(ftpClientMock.isConnected()).thenReturn(true);

        // Act
        ftpService.disconnectFtpClient(ftpClientMock);

        // Assert
        verify(ftpClientMock).logout();
        verify(ftpClientMock).disconnect();
    }

    @Test
    void GivenNotConnectedFtpClient_whenDisconnectFtpClient_shouldNotLogoutOrDisconnect()
            throws IOException {
        // Arrange
        when(ftpClientMock.isConnected()).thenReturn(false);

        // Act
        ftpService.disconnectFtpClient(ftpClientMock);

        // Assert
        verify(ftpClientMock, never()).logout();
        verify(ftpClientMock, never()).disconnect();
    }

    @Test
    void GivenValidFileAndRemoteName_whenUploadFile_thenCompleteFutureWithUri() throws Exception {
        // Arrange
        FtpService spyService = Mockito.spy(ftpService);
        MultipartFile multipartFile = new MockMultipartFile("file", "file.txt",
                "text/plain", "test".getBytes());

        File fakeFile = File.createTempFile("ftp-upload-", "-file.txt");

        // Mock dependencies
        doReturn(fakeFile).when(spyService).convertMultiPartToFile(any(MultipartFile.class));
        doReturn(ftpClientMock).when(spyService).createAndConfigureClient();
        when(ftpClientMock.storeFile(anyString(), any(FileInputStream.class))).thenReturn(true);

        // Mock changeWorkingDirectory to return false first (directory doesn't exist),
        // then true
        OngoingStubbing<Boolean> changeWorkingDirectoryStubbing = when(
                ftpClientMock.changeWorkingDirectory(anyString()));
        changeWorkingDirectoryStubbing.thenReturn(false).thenReturn(true);

        // Mock makeDirectory to return true (successful creation)
        when(ftpClientMock.makeDirectory(anyString())).thenReturn(true);

        // Mock isConnected to return true to ensure logout and disconnect are called
        when(ftpClientMock.isConnected()).thenReturn(true);

        // Act
        CompletableFuture<URI> future = spyService.uploadFile(multipartFile, "remoteFile.txt");

        // Wait for the future to complete
        URI uri = future.get(5, TimeUnit.SECONDS); // Increased timeout for reliability

        // Assert
        assertThat(uri).isNotNull();
        assertThat(uri.getScheme()).isEqualTo("ftp");
        assertThat(uri.getHost()).isEqualTo(ftpService.ftpHost);
        assertThat(uri.getPort()).isEqualTo(ftpService.ftpPort);
        assertThat(uri.getPath()).isEqualTo(ftpService.ftpUploadFolder + "remoteFile.txt");

        // Verify interactions
        verify(spyService).convertMultiPartToFile(multipartFile);
        verify(spyService).createAndConfigureClient();
        verify(ftpClientMock).storeFile(contains("remoteFile.txt"), any(FileInputStream.class));
        verify(ftpClientMock, times(2)).changeWorkingDirectory(anyString());
        verify(ftpClientMock).makeDirectory(anyString());
        verify(ftpClientMock).isConnected();
        verify(ftpClientMock).logout(); // Now should be invoked
        verify(ftpClientMock).disconnect(); // Now should be invoked

        fakeFile.delete();
    }

    @Test
    void GivenUploadFails_whenUploadFile_thenCompleteFutureExceptionally() throws Exception {
        // Arrange
        FtpService spyService = Mockito.spy(ftpService);
        MultipartFile multipartFile = new MockMultipartFile("file", "file.txt",
                "text/plain", "test".getBytes());

        File fakeFile = File.createTempFile("ftp-upload-", "-file.txt");

        doReturn(fakeFile).when(spyService).convertMultiPartToFile(any(MultipartFile.class));
        doReturn(ftpClientMock).when(spyService).createAndConfigureClient();
        when(ftpClientMock.storeFile(anyString(), any(FileInputStream.class))).thenReturn(false);

        // Act
        CompletableFuture<URI> future = spyService.uploadFile(multipartFile, "remoteFile.txt");

        // Assert
        assertThatThrownBy(future::get)
                .hasCauseInstanceOf(IOException.class)
                .hasMessageContaining("Failed to upload file to FTP");

        fakeFile.delete();
    }
}