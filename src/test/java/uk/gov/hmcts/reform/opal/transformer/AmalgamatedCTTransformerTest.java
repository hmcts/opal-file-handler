package uk.gov.hmcts.reform.opal.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;
import uk.gov.hmcts.reform.opal.model.dto.FinancialTransaction;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.entity.AntCtAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.model.entity.ChequeBankAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.model.entity.ChequeNumberAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.repository.AntCtAmalgamatedTemp;
import uk.gov.hmcts.reform.opal.repository.ChequeBankAmalgamatedTemp;
import uk.gov.hmcts.reform.opal.repository.ChequeNumberAmalgamatedTemp;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AmalgamatedCTTransformerTest {

    @Mock
    private ChequeBankAmalgamatedTemp chequeBankAmalgamatedTemp;

    @Mock
    private AntCtAmalgamatedTemp antCtAmalgamatedTemp;

    @Mock
    private ChequeNumberAmalgamatedTemp chequeNumberAmalgamatedTemp;

    @InjectMocks
    private AmalgamatedCTTransformer amalgamatedCTTransformer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transformAmalgamatedCT_withCheque_shouldTransformFile() {
        OpalFile file = OpalFile.builder().newFileName("file123").build();

        file.setFileContent(StandardBankingFile.builder()
                                .financialTransactions(Collections.singletonList(createDummyTransaction()))
                                        .build());

        ChequeBankAmalgamatedEntity chequeEntity = new ChequeBankAmalgamatedEntity();
        chequeEntity.setMasterCt("456");
        chequeEntity.setMasterBankAcc("newAcc");
        chequeEntity.setMasterSortCode("newSort");

        when(chequeBankAmalgamatedTemp.findByAmalgamatedCt_AmalgamatedCt("123")).thenReturn(chequeEntity);

        OpalFile result = amalgamatedCTTransformer.transformAmalgamatedCT(file, true);

        assertEquals("file456", result.getNewFileName());
    }

    @Test
    void transformAmalgamatedCT_withCash_shouldTransformFile() {
        OpalFile file = OpalFile.builder().newFileName("file123").build();

        file.setFileContent(StandardBankingFile.builder()
                                .financialTransactions(Collections.singletonList(createDummyTransaction())).build());

        AntCtAmalgamatedEntity cashEntity = new AntCtAmalgamatedEntity();
        cashEntity.setMasterCt("456");
        cashEntity.setMasterBankAccount("newAcc");
        cashEntity.setMasterSortCode("newSort");

        when(antCtAmalgamatedTemp.findByAmalgamatedCt("123")).thenReturn(cashEntity);

        OpalFile result = amalgamatedCTTransformer.transformAmalgamatedCT(file, false);

        assertEquals("file456", result.getNewFileName());
    }

    @Test
    void transformAmalgamatedCT_withUnsupportedFileContent_shouldThrowException() {
        OpalFile file = OpalFile.builder().build();
        file.setFileContent(new DwpFile());

        assertThrows(IllegalArgumentException.class, () -> amalgamatedCTTransformer
            .transformAmalgamatedCT(file, true));
    }

    @Test
    void transformChequeTransaction_withValidChequeNumber_shouldUpdateReferenceNumber() {

        ChequeBankAmalgamatedEntity chequeEntity = new ChequeBankAmalgamatedEntity();
        chequeEntity.setMasterBankAcc("newAcc");
        chequeEntity.setMasterSortCode("newSort");

        ChequeNumberAmalgamatedEntity chequeNumberEntity = new ChequeNumberAmalgamatedEntity();
        chequeNumberEntity.setNewChequeNo("654321");

        when(chequeNumberAmalgamatedTemp.findByAmalgamatedCt_AmalgamatedCtAndOldChequeNo("123",
                                                                                         "123456"))
            .thenReturn(chequeNumberEntity);
        FinancialTransaction transaction = FinancialTransaction.builder().referenceNumber("1234567890").build();

        FinancialTransaction result = amalgamatedCTTransformer.transformChequeTransaction(transaction,
                                                                                          "file123",
                                                                                          chequeEntity);

        assertEquals("6543217890", result.getReferenceNumber());
    }

    @Test
    void transformChequeTransaction_withNullChequeNumber_shouldNotUpdateReferenceNumber() {

        ChequeBankAmalgamatedEntity chequeEntity = new ChequeBankAmalgamatedEntity();
        chequeEntity.setMasterBankAcc("newAcc");
        chequeEntity.setMasterSortCode("newSort");

        when(chequeNumberAmalgamatedTemp.findByAmalgamatedCt_AmalgamatedCtAndOldChequeNo("123",
                                                                                         "123456"))
            .thenReturn(null);

        FinancialTransaction transaction = FinancialTransaction.builder().referenceNumber("1234567890").build();

        FinancialTransaction result = amalgamatedCTTransformer.transformChequeTransaction(transaction,
                                                                                          "file123",
                                                                                          chequeEntity);

        assertEquals("1234567890", result.getReferenceNumber());
    }

    private FinancialTransaction createDummyTransaction() {
        return FinancialTransaction.builder()
            .branchSortCode("123456")
            .branchAccountNumber("1234567890")
            .transactionCode("123")
            .originatorsSortCode("123456")
            .originatorsAccountNumber("1234567890")
            .originatorsReference("1234")
            .amount("1234567890")
            .originatorsNameOrDescription("12345678901234567890")
            .referenceNumber("12345678901234567890")
            .date("123456")
            .build();
    }
}