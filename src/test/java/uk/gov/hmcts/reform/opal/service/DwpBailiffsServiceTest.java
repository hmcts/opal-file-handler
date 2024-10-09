package uk.gov.hmcts.reform.opal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.sftp.SftpLocation;
import uk.gov.hmcts.reform.opal.transformer.AmalgamatedCTTransformer;
import uk.gov.hmcts.reform.opal.transformer.DwpTransformer;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DwpBailiffsServiceTest {

    @InjectMocks
    private DwpBailiffsService dwpBailiffsService;

    @Mock
    private FileHandlingService fileHandlingService;

    @Mock
    private AmalgamatedCTTransformer amalgamatedCTTransformer;

    @Mock
    private DwpTransformer dwpTransformer;

    private final String processingPath = SftpLocation.DWP_BAILIFFS_PROCESSING.getPath();
    private final String errorPath = SftpLocation.DWP_BAILIFFS_ERROR.getPath();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void process_shouldProcessAllFiles() {
        // Arrange
        String fileName1 = "file1.txt";
        String fileName2 = "file2.txt";

        when(fileHandlingService.getListOfFilesToProcess()).thenReturn(Arrays.asList(fileName1, fileName2));
        OpalFile opalFile = OpalFile.builder().build();
        when(fileHandlingService.createOpalFile(eq(fileName1), eq(true), eq(processingPath))).thenReturn(opalFile);
        when(fileHandlingService.createOpalFile(eq(fileName2), eq(true), eq(processingPath))).thenReturn(opalFile);
        when(dwpTransformer.dwpTransform(opalFile)).thenReturn(opalFile);
        when(amalgamatedCTTransformer.transformAmalgamatedCT(opalFile, false)).thenReturn(opalFile);

        // Act
        dwpBailiffsService.process();

        // Assert
        verify(fileHandlingService, times(2)).outputFileSuccess(any(OpalFile.class));
    }

    @Test
    void processSingleFile_shouldHandleException() {
        // Arrange
        String fileName = "file1.txt";
        when(fileHandlingService.createOpalFile(eq(fileName), eq(true), eq(processingPath)))
            .thenThrow(new RuntimeException("File error"));

        // Act
        dwpBailiffsService.processSingleFile(fileName);

        // Assert
        verify(fileHandlingService).outputFileError(eq(fileName), eq(processingPath), eq(errorPath));
    }

    @Test
    void applyTransformations_shouldReturnTransformedFile() {
        // Arrange
        OpalFile opalFile = OpalFile.builder().build();
        when(dwpTransformer.dwpTransform(opalFile)).thenReturn(opalFile);
        when(amalgamatedCTTransformer.transformAmalgamatedCT(opalFile, false)).thenReturn(opalFile);

        // Act
        OpalFile transformedFile = dwpBailiffsService.applyTransformations(opalFile);

        // Assert
        assertEquals(opalFile, transformedFile);
        verify(dwpTransformer).dwpTransform(opalFile);
        verify(amalgamatedCTTransformer).transformAmalgamatedCT(opalFile, false);
    }
}
