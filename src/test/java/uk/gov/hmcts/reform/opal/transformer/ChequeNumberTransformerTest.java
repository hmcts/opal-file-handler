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
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFileName;
import uk.gov.hmcts.reform.opal.model.entity.AntMccCtEntity;
import uk.gov.hmcts.reform.opal.model.entity.ChequeBankAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.model.entity.ChequeNumberAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.repository.AntMccCtRepository;
import uk.gov.hmcts.reform.opal.repository.ChequeBankAmalgamatedRepository;
import uk.gov.hmcts.reform.opal.repository.ChequeNumberAmalgamatedRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ChequeNumberTransformerTest {

    @Mock
    private AntMccCtRepository antMccCtRepository;

    @Mock
    private ChequeNumberAmalgamatedRepository chequeNumberAmalgamated;

    @Mock
    private  ChequeBankAmalgamatedRepository chequeBankAmalgamated;

    @InjectMocks
    private ChequeNumberTransformer chequeNumberTransformer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transformAmalgamatedCT_withCheque_shouldTransformFile() {
        OpalFile file = OpalFile.builder().originalFileName("filect1").build();

        file.setFileContent(StandardBankingFile.builder()
                                .financialTransactions(Collections.singletonList(createDummyTransaction()))
                                        .build());

        ChequeBankAmalgamatedEntity chequeEntity = new ChequeBankAmalgamatedEntity();
        chequeEntity.setMasterCt("456");
        chequeEntity.setMasterBankAccount("newAcc");
        chequeEntity.setMasterSortCode("newSort");

        AntMccCtEntity antMccCtEntity = new AntMccCtEntity();
        antMccCtEntity.setMccCt("12345678");

        when(antMccCtRepository.findByBranchSortCodeAndBranchAccountNumber(any(), any())).thenReturn(antMccCtEntity);

        when(chequeBankAmalgamated.findByAmalgamatedCt("ct1")).thenReturn(chequeEntity);

        OpalFile result = chequeNumberTransformer.transformAmalgamatedCT(file);

        assertEquals("bacs_678_1.dat",
                     StandardBankingFileName.toString((StandardBankingFileName) result.getNewFileName()));
    }

    @Test
    void transformAmalgamatedCT_wrongFileType() {
        OpalFile file = OpalFile.builder().originalFileName("filect1").build();

        file.setFileContent(new DwpFile());

        assertThrows(IllegalArgumentException.class, () -> chequeNumberTransformer.transformAmalgamatedCT(file));
    }


    @Test
    void transformAmalgamatedCT_withCheque_notAmalgamated() {
        OpalFile file = OpalFile.builder().originalFileName("filect1").build();

        file.setFileContent(StandardBankingFile.builder()
                                .financialTransactions(Collections.singletonList(createDummyTransaction()))
                                .build());


        when(chequeBankAmalgamated.findByAmalgamatedCt("ct1")).thenReturn(null);

        OpalFile result = chequeNumberTransformer.transformAmalgamatedCT(file);

        assertEquals("bacs_ct1_1.dat", StandardBankingFileName.toString(
            (StandardBankingFileName) result.getNewFileName()));
    }


    @Test
    void transformChequeTransaction_withValidChequeNumber_shouldUpdateReferenceNumber() {

        ChequeBankAmalgamatedEntity chequeEntity = new ChequeBankAmalgamatedEntity();
        chequeEntity.setMasterBankAccount("newAcc");
        chequeEntity.setMasterSortCode("newSort");
        chequeEntity.setMasterCt("123");

        ChequeNumberAmalgamatedEntity chequeNumberEntity = new ChequeNumberAmalgamatedEntity();
        chequeNumberEntity.setNewChequeNumber("654321");

        AntMccCtEntity antMccCtEntity = new AntMccCtEntity();
        antMccCtEntity.setMccCt("12345678");

        when(chequeNumberAmalgamated.findByAmalgamatedCtAndOldChequeNumber("123",
                                                                           "123456"))
            .thenReturn(chequeNumberEntity);

        when(antMccCtRepository.findByBranchSortCodeAndBranchAccountNumber(any(), any())).thenReturn(antMccCtEntity);

        FinancialTransaction transaction = FinancialTransaction.builder().referenceNumber("1234567890").build();

        FinancialTransaction result = chequeNumberTransformer.transformChequeTransaction(transaction,
                                                                                          chequeEntity);

        assertEquals("6543217890", result.getReferenceNumber());
    }

    @Test
    void transformChequeTransaction_nullBankDetails_newReferenceNumber() {

        ChequeBankAmalgamatedEntity chequeEntity = new ChequeBankAmalgamatedEntity();
        chequeEntity.setMasterCt("123");

        ChequeNumberAmalgamatedEntity chequeNumberEntity = new ChequeNumberAmalgamatedEntity();
        chequeNumberEntity.setNewChequeNumber("654321");

        AntMccCtEntity antMccCtEntity = new AntMccCtEntity();
        antMccCtEntity.setMccCt("12345678");

        when(chequeNumberAmalgamated.findByAmalgamatedCtAndOldChequeNumber("123",
                                                                           "123456"))
            .thenReturn(chequeNumberEntity);

        when(antMccCtRepository.findByBranchSortCodeAndBranchAccountNumber(any(), any())).thenReturn(antMccCtEntity);

        FinancialTransaction transaction = FinancialTransaction.builder()
            .branchSortCode("112233").branchAccountNumber("12345678").referenceNumber("1234567890").build();

        FinancialTransaction result = chequeNumberTransformer.transformChequeTransaction(transaction,
                                                                                         chequeEntity);

        assertEquals("6543217890", result.getReferenceNumber());
        assertEquals("12345678", result.getBranchAccountNumber());
        assertEquals("112233", result.getBranchSortCode());
    }

    @Test
    void transformChequeTransaction_withNullChequeNumber_newBankDetails() {

        ChequeBankAmalgamatedEntity chequeEntity = new ChequeBankAmalgamatedEntity();
        chequeEntity.setMasterBankAccount("newAcc");
        chequeEntity.setMasterSortCode("newSort");
        chequeEntity.setOldBankAccount("oldAcc");
        chequeEntity.setOldSortCode("oldSort");

        AntMccCtEntity antMccCtEntity = new AntMccCtEntity();
        antMccCtEntity.setMccCt("12345678");

        when(chequeNumberAmalgamated.findByAmalgamatedCtAndOldChequeNumber("123",
                                                                           "123456"))
            .thenReturn(null);

        when(antMccCtRepository.findByBranchSortCodeAndBranchAccountNumber(any(), any())).thenReturn(antMccCtEntity);
        FinancialTransaction transaction = FinancialTransaction.builder()
            .branchAccountNumber("12345678").branchSortCode("112233").referenceNumber("1234567890").build();

        FinancialTransaction result = chequeNumberTransformer.transformChequeTransaction(transaction,
                                                                                          chequeEntity);

        assertEquals("1234567890", result.getReferenceNumber());
        assertEquals("newAcc", result.getBranchAccountNumber());
        assertEquals("newSort", result.getBranchSortCode());
    }

    private FinancialTransaction createDummyTransaction() {
        return FinancialTransaction.builder()
            .branchSortCode("123456")
            .branchAccountNumber("1234567890")
            .accountType("1")
            .transactionType("23")
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
