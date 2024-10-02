package uk.gov.hmcts.reform.opal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.opal.model.dto.FileContent;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.sftp.SftpInboundService;
import uk.gov.hmcts.reform.opal.transformer.AmalgamatedCTTransformer;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_ERROR;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_PROCESSING;
import static uk.gov.hmcts.reform.opal.sftp.SftpLocation.DWP_BAILIFFS_SUCCESS;

@ExtendWith(MockitoExtension.class)
class DwpBailiffsServiceTest {

    @Mock
    private SftpInboundService sftpInboundService;

    @Mock
    private AmalgamatedCTTransformer amalgamatedCTTransformer;

    private DwpBailiffsService dwpBailiffsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dwpBailiffsService = spy(new DwpBailiffsService(sftpInboundService, amalgamatedCTTransformer));
    }

    @Test
    void processMovesFilesToProcessingAndProcessesThem() {
        String fileName = "test.txt";
        when(sftpInboundService.listFiles(DWP_BAILIFFS.getPath())).thenReturn(List.of(new String[]{fileName}));

        dwpBailiffsService.process();

        verify(sftpInboundService).moveFile(eq(DWP_BAILIFFS.getPath()), eq(fileName),
                                            eq(DWP_BAILIFFS_PROCESSING.getPath()));
        verify(sftpInboundService).listFiles(DWP_BAILIFFS.getPath());
    }

    @Test
    void testProcessSingleFile_Success() throws Exception {
        // Arrange
        String fileName = "testFile.txt";;
        FileContent testFileContent = StandardBankingFile.builder().header(new StringBuilder("header")).build();

        OpalFile testFile = OpalFile.builder().newFileName(fileName).originalFileName(fileName)
            .fileContent(testFileContent).build();

        // Mocking createOpalFile to return a mock object of OpalFile
        when(dwpBailiffsService.createOpalFile(fileName, true, DWP_BAILIFFS_PROCESSING.getPath()))
            .thenReturn(testFile);

        // Mocking applyTransformations to return an object (e.g., transformed file)

        when(dwpBailiffsService.applyTransformations(testFile)).thenReturn(testFile);

        // Act
        dwpBailiffsService.processSingleFile(fileName);

        // Assert
        // Verify that outputFileSuccess was called with the transformed file and success path
        verify(dwpBailiffsService, times(1))
            .outputFileSuccess(testFile, DWP_BAILIFFS_SUCCESS.getPath());

        // Ensure outputFileError was not called
        verify(dwpBailiffsService, never()).outputFileError(anyString(), anyString(), anyString());
    }

    @Test
    void testProcessSingleFile_Failure() throws Exception {
        // Arrange
        String fileName = "testFile.txt";
        String processingPath = DWP_BAILIFFS_PROCESSING.getPath();
        String errorPath = DWP_BAILIFFS_ERROR.getPath();

        // Mock createOpalFile to throw an exception
        when(dwpBailiffsService.createOpalFile(fileName, true, processingPath))
            .thenThrow(new IndexOutOfBoundsException("Test Exception"));

        // Act
        dwpBailiffsService.processSingleFile(fileName);

        // Assert
        // Verify that outputFileError was called with the correct parameters
        verify(dwpBailiffsService, times(1)).outputFileError(fileName, processingPath,
                                                             errorPath);

        // Ensure outputFileSuccess was never called
        verify(dwpBailiffsService, never()).outputFileSuccess(any(), anyString());
    }
}
