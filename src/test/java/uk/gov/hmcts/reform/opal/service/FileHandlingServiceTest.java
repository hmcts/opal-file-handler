package uk.gov.hmcts.reform.opal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.sftp.SftpInboundService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_PROCESSING;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_SUCCESS;

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
    void testGetListOfFilesToProcess() {
        when(sftpInboundService.listFiles(DWP_BAILIFFS.getPath())).thenReturn(Arrays.asList("file1.txt", "file2.txt"));

        List<String> result = fileHandlingService.getListOfFilesToProcess();

        verify(sftpInboundService, times(2))
            .moveFile(eq(DWP_BAILIFFS.getPath()), anyString(), eq(DWP_BAILIFFS_PROCESSING.getPath()));
        assertEquals(result.size(), 2);
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
    void testCreateOpalFileDwp_Success() throws Exception {
        // Arrange
        String fileName = "testFile.txt";
        String path = "/path/to/file";
        String fileContent = getDwpTestFileContent();

        doAnswer(invocation -> {
            Consumer<InputStream> consumer = invocation.getArgument(2);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
            consumer.accept(inputStream);
            return null;
        }).when(sftpInboundService).downloadFile(eq(path), eq(fileName), Mockito.<Consumer<InputStream>>any());


        // Act
        OpalFile result = fileHandlingService.createOpalFile(fileName, true, path);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getOriginalFileName());
        assertTrue(result.getFileContent() instanceof DwpFile);

        verify(sftpInboundService, times(1)).downloadFile(eq(path), eq(fileName),
                                                          any(Consumer.class));
    }

    @Test
    void testOutputFileSuccess() throws Exception {
        // Arrange
        OpalFile opalFile = OpalFile.builder().originalFileName("originalFile.txt")
            .newFileName(StandardBankingFileName.builder().build()).build();
        opalFile.setFileContent(StandardBankingFile.builder().build());
        // Act
        fileHandlingService.uploadStandardBankingFile(opalFile, DWP_BAILIFFS_SUCCESS.getPath());
        // Assert
        String pathSuccess = "dwp-bailiffs/success";
        verify(sftpInboundService, times(1))
            .uploadFile(any(byte[].class), eq(pathSuccess), anyString());
    }

    @Test
    void testOutputFileError() {
        // Arrange
        String fileName = "errorFile.txt";
        String oldPath = "dwp-bailiffs/processing";
        String newPath = "dwp-bailiffs/error";

        // Act
        fileHandlingService.moveFile(fileName, oldPath, newPath);

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

    private String getDwpTestFileContent() {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <PacsTppSchedule xmlns="http://www.dwp.gsi.gov.uk/pacs" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.dwp.gsi.gov.uk/pacs">
             <DocumentHeader>
              <CreditorID>0000031714</CreditorID>
              <BatchNumber>00003</BatchNumber>
              <PacsDocumentCreationDate>2023-09-10</PacsDocumentCreationDate>
              <PacsDocumentCreationTime>10:59:59</PacsDocumentCreationTime>
              <NotificationReference>0000000002</NotificationReference>
             </DocumentHeader>
             <DocumentDetail>
              <CustomerRef>23000106E</CustomerRef>
              <RecordType>02</RecordType>
              <LocationCode>100202</LocationCode>
              <NationalInsuranceNumberType></NationalInsuranceNumberType>
              <DateFrom>2023-09-03</DateFrom>
              <DateTo>2023-09-03</DateTo>
              <DetailAmountType>0000000199</DetailAmountType>
              <DetailAmountSign>+</DetailAmountSign>
             </DocumentDetail>
             <DocumentSummary>
              <SummaryAmountType>0000000035</SummaryAmountType>
              <SummaryAmountSign>+</SummaryAmountSign>
              <Total02Records>5</Total02Records>
              <Total03Records>4</Total03Records>
             </DocumentSummary>
            </PacsTppSchedule>""";
    }
}
