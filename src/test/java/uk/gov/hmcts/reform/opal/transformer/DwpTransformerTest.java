package uk.gov.hmcts.reform.opal.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.DocumentDetail;
import uk.gov.hmcts.reform.opal.model.dto.DocumentHeader;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.model.entity.AntCtBankAccountEntity;
import uk.gov.hmcts.reform.opal.model.entity.AntMccCtEntity;
import uk.gov.hmcts.reform.opal.repository.AntCtBankAccountRepository;
import uk.gov.hmcts.reform.opal.repository.AntMccCtRepository;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DwpTransformerTest {

    @InjectMocks
    private DwpTransformer dwpTransformer;

    @Mock
    private AntCtBankAccountRepository antCtBankAccountRepository;

    @Mock
    private AntMccCtRepository antMccCtRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void dwpTransform_withValidDwpFile_shouldTransformFile() {
        DwpFile dwpFile = new DwpFile();
        DocumentHeader documentHeader = new DocumentHeader();
        documentHeader.setPacsDocumentCreationDate("2023-12-02");
        dwpFile.setDocumentHeader(documentHeader);
        dwpFile.setDocumentDetails(Collections.singletonList(createDummyDocumentDetail("+",
                                                                                       "PAYMENT")));

        OpalFile opalFile = OpalFile.builder()
            .originalFileName("0000031714_dat_0000000005_20231202_105959.txt")
            .newFileName(StandardBankingFileName.builder().prefix("a121").ct("073").date("DATE").source("db")
                             .extension("dat").build())
            .fileContent(dwpFile).build();

        AntCtBankAccountEntity antCtBankAccountEntity = new AntCtBankAccountEntity();
        AntMccCtEntity antMccCtEntity = new AntMccCtEntity();
        antMccCtEntity.setMccCt("12345678");

        when(antCtBankAccountRepository
            .findByDwpCourtCode(opalFile.getOriginalFileName().substring(0, 10))).thenReturn(antCtBankAccountEntity);
        when(antMccCtRepository.findByCt(any())).thenReturn(antMccCtEntity);

        OpalFile result = dwpTransformer.dwpTransform(opalFile);

        assertEquals("a121_2023-12-02_DB_678.dat", StandardBankingFileName.toString(
            (StandardBankingFileName) result.getNewFileName()));
        assertEquals(1, ((StandardBankingFile) result.getFileContent()).getFinancialTransactions().size());
    }

    @Test
    void dwpTransform_withValidDwpFile_shouldTransformFile_ExcludeNonPlus() {
        DwpFile dwpFile = new DwpFile();
        DocumentHeader documentHeader = new DocumentHeader();
        documentHeader.setPacsDocumentCreationDate("2023-12-02");
        dwpFile.setDocumentHeader(documentHeader);
        dwpFile.setDocumentDetails(Arrays.asList(createDummyDocumentDetail("+",
                                                                           "PAYMENT"),
                                                 createDummyDocumentDetail("-",
                                                                           "PAYMENT")));
        OpalFile opalFile = OpalFile.builder()
            .originalFileName("0000031714_dat_0000000005_20231202_105959.txt")
            .newFileName(StandardBankingFileName.builder().prefix("a121").ct("073").date("DATE").source("db")
                             .extension("dat").build())
            .fileContent(dwpFile).build();

        AntCtBankAccountEntity antCtBankAccountEntity = new AntCtBankAccountEntity();
        AntMccCtEntity antMccCtEntity = new AntMccCtEntity();
        antMccCtEntity.setMccCt("12345678");

        when(antCtBankAccountRepository
                 .findByDwpCourtCode(opalFile.getOriginalFileName().substring(0, 10)))
            .thenReturn(antCtBankAccountEntity);
        when(antMccCtRepository.findByCt(any())).thenReturn(antMccCtEntity);

        OpalFile result = dwpTransformer.dwpTransform(opalFile);

        assertEquals("a121_2023-12-02_DB_678.dat", StandardBankingFileName.toString(
            (StandardBankingFileName) result.getNewFileName()));
        assertEquals(1, ((StandardBankingFile) result.getFileContent()).getFinancialTransactions().size());
    }

    @Test
    void dwpTransform_withInvalidFileContent_shouldThrowException() {
        OpalFile opalFile = OpalFile.builder()
            .originalFileName("0000031714_dat_0000000005_20231202_105959.txt")
            .newFileName(StandardBankingFileName.builder().ct("073").date("DATE").source("db").extension("dat").build())
            .fileContent(StandardBankingFile.builder().build()).build();

        assertThrows(ClassCastException.class, () -> dwpTransformer.dwpTransform(opalFile));
    }

    private DocumentDetail createDummyDocumentDetail(String amountSign, String amountType) {
        DocumentDetail documentDetail = new DocumentDetail();
        documentDetail.setDetailAmountSign(amountSign);
        documentDetail.setDetailAmountType(amountType);
        documentDetail.setCustomerRef("1234567890");
        documentDetail.setDateFrom("2023-10-01");
        return documentDetail;
    }
}
