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
import uk.gov.hmcts.reform.opal.model.entity.AntCtAmalgamatedEntity;
import uk.gov.hmcts.reform.opal.model.entity.AntMccCtEntity;
import uk.gov.hmcts.reform.opal.repository.AntCtAmalgamatedRepository;
import uk.gov.hmcts.reform.opal.repository.AntMccCtRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AmalgamatedBUTransformerTest {

    @Mock
    private AntCtAmalgamatedRepository antCtAmalgamated;

    @Mock
    private AntMccCtRepository antMccCtRepository;

    @InjectMocks
    private AmalgamatedBUTransformer amalgamatedBUTransformer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transformAmalgamatedCT_withCash_shouldTransformFile() {
        OpalFile file = OpalFile.builder().newFileName(createDummyFileName()).build();

        file.setFileContent(StandardBankingFile.builder()
                                .financialTransactions(Collections.singletonList(createDummyTransaction()))
                                        .build());

        AntCtAmalgamatedEntity cashEntity = new AntCtAmalgamatedEntity();
        cashEntity.setMasterCt("456");
        cashEntity.setMasterBankAccount("newAcc");
        cashEntity.setMasterSortCode("newSort");

        AntMccCtEntity antMccCtEntity = new AntMccCtEntity();
        antMccCtEntity.setMccCt("12345678");

        when(antCtAmalgamated.findByAmalgamatedCt("123456")).thenReturn(cashEntity);
        when(antMccCtRepository.findByBranchSortCodeAndBranchAccountNumber(any(), any())).thenReturn(antMccCtEntity);

        OpalFile result = amalgamatedBUTransformer.transformAmalgamatedCT(file, "DB");

        assertEquals("a121_123456_DB_678.dat", StandardBankingFileName.toString(
            (StandardBankingFileName) result.getNewFileName()));
    }

    @Test
    void transformAmalgamatedCT_withoutCash_shouldNotTransformFile() {
        OpalFile file = OpalFile.builder().newFileName(createDummyFileName()).build();

        file.setFileContent(StandardBankingFile.builder()
                                .financialTransactions(Collections.singletonList(createDummyTransaction()))
                                .build());



        AntMccCtEntity antMccCtEntity = new AntMccCtEntity();
        antMccCtEntity.setMccCt("12345678");

        when(antMccCtRepository.findByBranchSortCodeAndBranchAccountNumber(any(), any())).thenReturn(antMccCtEntity);

        OpalFile result = amalgamatedBUTransformer.transformAmalgamatedCT(file, "DB");

        assertEquals("a121_123456_DB_678.dat", StandardBankingFileName.toString(
            (StandardBankingFileName) result.getNewFileName()));
    }

    @Test
    void transformAmalgamatedCT_withUnsupportedFileContent_shouldThrowException() {
        OpalFile file = OpalFile.builder().build();
        file.setFileContent(new DwpFile());

        assertThrows(IllegalArgumentException.class, () -> amalgamatedBUTransformer
            .transformAmalgamatedCT(file, "DB"));
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

    private StandardBankingFileName createDummyFileName() {
        return StandardBankingFileName.builder()
            .date("123456")
            .source("123456")
            .ct("123456")
            .sequence("123456")
            .extension("123456")
            .build();
    }
}
