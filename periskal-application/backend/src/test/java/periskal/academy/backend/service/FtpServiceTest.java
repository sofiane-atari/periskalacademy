// package periskal.academy.backend.service;

// import org.apache.commons.net.ftp.FTPSClient;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.*;
// import java.net.URI;
// import java.util.concurrent.CompletableFuture;

// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// class FtpServiceTest {

// @InjectMocks
// private FtpService ftpService;

// @Mock
// private FTPSClient ftpClientMock;

// @BeforeEach
// void setup() {
// MockitoAnnotations.openMocks(this);
// ftpService.ftpHost = "localhost";
// ftpService.ftpUser = "user";
// ftpService.ftpPassword = "password";
// ftpService.ftpUploadFolder = "/uploads/";
// ftpService.ftpPort = 21;
// ftpService.connectionTimeout = 10000;
// ftpService.dataTimeout = 30000;
// }

// @Test
// void
// GivenMultipartFile_thenConvertMultiPartToFile_whenCalled_shouldCreateTempFile()
// throws IOException {
// // Assert
// byte[] content = "hello".getBytes();
// MultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
// "text/plain", content);

// // Act
// File file = ftpService.convertMultiPartToFile(multipartFile);

// // Arrange
// assertThat(file).exists();
// assertThat(file.length()).isEqualTo(content.length);
// file.delete();
// }

// @Test
// void
// GivenConnectedFtpClient_whenDisconnectFtpClient_shouldLogoutAndDisconnect()
// throws IOException {
// // Assert
// FTPSClient ftp = mock(FTPSClient.class);
// when(ftp.isConnected()).thenReturn(true);

// // Act
// ftpService.disconnectFtpClient(ftp);

// // Arrange
// verify(ftp).logout();
// verify(ftp).disconnect();
// }

// @Test
// void
// GivenNotConnectedFtpClient_whenDisconnectFtpClient_shouldNotLogoutOrDisconnect()
// throws IOException {
// // Assert
// FTPSClient ftp = mock(FTPSClient.class);
// when(ftp.isConnected()).thenReturn(false);

// // Act
// ftpService.disconnectFtpClient(ftp);

// // Arrange
// verify(ftp, never()).logout();
// verify(ftp, never()).disconnect();
// }

// @Test
// void GivenValidFileAndRemoteName_whenUploadFile_thenCompleteFutureWithUri()
// throws Exception {
// // This test is tricky because uploadFile spawns a new thread and uses real
// FTP
// // client inside
// // We can mock convertMultiPartToFile and uploadFileToFtp by partial mocking

// FtpService spyService = Mockito.spy(ftpService);
// MultipartFile multipartFile = new MockMultipartFile("file", "file.txt",
// "text/plain", "test".getBytes());

// File fakeFile = File.createTempFile("ftp-upload-", "-file.txt");
// doReturn(fakeFile).when(spyService).convertMultiPartToFile(any(MultipartFile.class));
// doReturn(true).when(spyService).uploadFileToFtp(any(), eq(fakeFile),
// anyString());
// doReturn(mock(FTPSClient.class)).when(spyService).createAndConfigureClient();

// CompletableFuture<URI> future = spyService.uploadFile(multipartFile,
// "remoteFile.txt");

// URI uri = future.get();

// assertThat(uri).isNotNull();
// assertThat(uri.getScheme()).isEqualTo("ftp");
// assertThat(uri.getHost()).isEqualTo(ftpService.ftpHost);

// fakeFile.delete();
// }
// }
