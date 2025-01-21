package uk.gov.hmcts.reform.opal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.sftp.SftpLocation;
import uk.gov.hmcts.reform.opal.transformer.AmalgamatedBUTransformer;
import uk.gov.hmcts.reform.opal.transformer.AntTransformer;
import uk.gov.hmcts.reform.opal.transformer.DwpTransformer;

import java.util.Arrays;
import java.util.List;

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
    private AmalgamatedBUTransformer amalgamatedCTTransformer;

    @Mock
    private DwpTransformer dwpTransformer;

    @Mock
    private AntTransformer antTransformer;

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
        OpalFile opalFile = OpalFile.builder()
            .newFileName(StandardBankingFileName.builder().ct("073").date("DATE").source("db").extension("dat").build())
            .build();
        when(fileHandlingService.createOpalFile(eq(fileName1), eq(true), eq(processingPath))).thenReturn(opalFile);
        when(fileHandlingService.createOpalFile(eq(fileName2), eq(true), eq(processingPath))).thenReturn(opalFile);
        when(dwpTransformer.dwpTransform(opalFile)).thenReturn(opalFile);
        when(amalgamatedCTTransformer.transformAmalgamatedCT(opalFile, "DB")).thenReturn(opalFile);
        when(antTransformer.antTransform(opalFile)).thenReturn(opalFile);

        // Act
        dwpBailiffsService.process();

        // Assert
        verify(fileHandlingService, times(2))
            .uploadStandardBankingFile(any(OpalFile.class), eq(SftpLocation.DWP_BAILIFFS_SUCCESS.getPath()));
    }

    @Test
    void applyTransformations_shouldReturnTransformedFile() {
        // Arrange
        OpalFile opalFile = OpalFile.builder()
            .newFileName(StandardBankingFileName.builder().ct("073").date("DATE").source("db").extension("dat").build())
            .build();
        when(dwpTransformer.dwpTransform(opalFile)).thenReturn(opalFile);
        when(amalgamatedCTTransformer.transformAmalgamatedCT(opalFile, "DB")).thenReturn(opalFile);
        when(antTransformer.antTransform(opalFile)).thenReturn(opalFile);

        // Act
        OpalFile transformedFile = dwpBailiffsService.applyTransformations(opalFile);

        // Assert
        assertEquals(opalFile, transformedFile);
        verify(dwpTransformer).dwpTransform(opalFile);
        verify(amalgamatedCTTransformer).transformAmalgamatedCT(opalFile, "DB");
        verify(antTransformer).antTransform(opalFile);
    }

    @Test
    void process_ExceptionInTransformer() {
        // Arrange
        String fileName1 = "file1.txt";



        when(fileHandlingService.getListOfFilesToProcess()).thenReturn(List.of(fileName1));
        OpalFile opalFile = OpalFile.builder()
            .newFileName(StandardBankingFileName.builder().ct("073").date("DATE").source("db").extension("dat").build())
            .build();
        when(fileHandlingService.createOpalFile(eq(fileName1), eq(true), eq(processingPath))).thenReturn(opalFile);
        when(dwpTransformer.dwpTransform(opalFile)).thenThrow(RuntimeException.class);

        // Act
        dwpBailiffsService.process();

        // Assert
        verify(fileHandlingService, times(1))
            .moveFile(eq("file1.txt"), eq(SftpLocation.DWP_BAILIFFS_PROCESSING.getPath()),
                      eq(SftpLocation.DWP_BAILIFFS_ERROR.getPath()));
    }
}
