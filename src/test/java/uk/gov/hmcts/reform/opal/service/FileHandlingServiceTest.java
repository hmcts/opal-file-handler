package uk.gov.hmcts.reform.opal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.sftp.SftpInboundService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
class FileHandlingServiceTest {

    @Mock
    private SftpInboundService sftpInboundService;

    @InjectMocks
    private FileHandlingService fileHandlingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOpalFile_Success() throws Exception {
        // Arrange
        String fileName = "testFile.txt";
        String path = "/path/to/file";
        String fileContent = getTestFileContent();

        doAnswer(invocation -> {
            Consumer<InputStream> consumer = invocation.getArgument(2);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
            consumer.accept(inputStream);
            return null;
        }).when(sftpInboundService).downloadFile(eq(path), eq(fileName), Mockito.<Consumer<InputStream>>any());


        // Act
        OpalFile result = fileHandlingService.createOpalFile(fileName, false, path);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getOriginalFileName());
        assertTrue(result.getFileContent() instanceof StandardBankingFile);
        assertEquals(fileContent, StandardBankingFile.toString((StandardBankingFile) result.getFileContent()));

        verify(sftpInboundService, times(1)).downloadFile(eq(path), eq(fileName),
                                                          any(Consumer.class));
    }

    @Test
    void testOutputFileSuccess() throws Exception {
        // Arrange
        String fileName = "newTestFile.txt";
        OpalFile opalFile = OpalFile.builder().originalFileName("originalFile.txt").newFileName(fileName).build();
        opalFile.setFileContent(StandardBankingFile.builder().build());
        // Act
        fileHandlingService.outputFileSuccess(opalFile);
        // Assert
        String pathSuccess = "dwp-bailiffs/success";
        String processingPath = "dwp-bailiffs/processing";
        String archivePath = "dwp-bailiffs/archive";
        verify(sftpInboundService, times(1))
            .uploadFile(any(byte[].class), eq(pathSuccess), eq(fileName));
        verify(sftpInboundService, times(1))
            .moveFile(eq(processingPath), eq(opalFile.getOriginalFileName()), eq(archivePath));
    }

    @Test
    void testOutputFileError() {
        // Arrange
        String fileName = "errorFile.txt";
        String oldPath = "dwp-bailiffs/processing";
        String newPath = "dwp-bailiffs/error";

        // Act
        fileHandlingService.outputFileError(fileName, oldPath, newPath);

        // Assert
        verify(sftpInboundService, times(1)).moveFile(eq(oldPath), eq(fileName), eq(newPath));
    }

    @Test
    void testCreateOpalFile_Exception() {
        // Arrange
        String fileName = "testFile.txt";
        String path = "/path/to/file";
        String fileContent = "line1\nline2\n";

        doThrow(new IndexOutOfBoundsException("Error processing file")).when(sftpInboundService)
            .downloadFile(eq(path), eq(fileName), any(Consumer.class));
        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            fileHandlingService.createOpalFile(fileName, false, path);
        });
        assertEquals("java.lang.IndexOutOfBoundsException: Error processing file", thrown.getMessage());
    }

    private String getTestFileContent() {
        return """
            VOL1                                 ****830000        3
            HDR1 A560033Z0001                      F     01 21123                   0
            UHL1 21123560033    00000000         000
            0000000000000000000000000000000000000000000000                  21003440N                            21122
            0000000000000000000000000000000000000000000000                  20003448F                            21122
            0000000000000000000000000000000000000000000000                  20007492T                            21122
            0000000000000000000000000000000000000000000000                  21002866U                            21122
            0000000000000000000000000000000000000000000000                  20002042K                            21122
            0000000000000000000000000000000000000000000000                  20004631R                            21122
            0000000000000000000000000000000000000000000000                  21002615Q                            21122
            0000000000000000000000000000000000000000000000                  21003456L                            21122
            0000000000000000000000000000000000000000000000                  16008027S                            21122
            0000000000000000000000000000000000000000000000                  21001301J                            21122
            0000000000000000000000000000000000000000000000                  21000633N                            21122
            0000000000000000000000000000000000000000000000                  18011565T                            21122
            0000000000000000000000000000000000000000000000                  21001292B                            21121
            UTL10000000000000000000048800000000000000013""";
    }
}
