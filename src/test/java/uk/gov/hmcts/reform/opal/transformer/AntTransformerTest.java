package uk.gov.hmcts.reform.opal.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.FinancialTransaction;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.repository.AccountNumberTranslation2Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AntTransformerTest {

    @Mock
    private AccountNumberTranslation2Repository accountNumberTranslation2Repository;

    @InjectMocks
    private AntTransformer antTransformer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void antTransform_updatesReferenceNumberAndTransactionType() {
        FinancialTransaction transaction1 = FinancialTransaction.builder()
            .referenceNumber("123")
            .transactionType("93")
            .branchSortCode("001")
            .build();
        FinancialTransaction transaction2 = FinancialTransaction.builder()
            .referenceNumber("456")
            .transactionType("15")
            .branchSortCode("002")
            .build();
        StandardBankingFile fileContent = StandardBankingFile.builder()
            .financialTransactions(Arrays.asList(transaction1, transaction2))
            .build();
        OpalFile opalFile = OpalFile.builder().originalFileName("original.txt")
            .fileContent(fileContent)
            .build();

        when(accountNumberTranslation2Repository.findUpdatedReference("001", "123"))
            .thenReturn(Optional.of("updated123"));
        when(accountNumberTranslation2Repository.findUpdatedReference("002", "456"))
            .thenReturn(Optional.of("updated456"));

        OpalFile result = antTransformer.antTransform(opalFile);

        assertEquals("updated123", ((StandardBankingFile)result.getFileContent())
            .getFinancialTransactions().getFirst().getReferenceNumber());
        assertEquals("99", ((StandardBankingFile)result.getFileContent())
            .getFinancialTransactions().getFirst().getTransactionType());
        assertEquals("updated456", ((StandardBankingFile)result.getFileContent())
            .getFinancialTransactions().get(1).getReferenceNumber());
        assertEquals("99", ((StandardBankingFile)result.getFileContent())
            .getFinancialTransactions().get(1).getTransactionType());
    }

    @Test
    void antTransform_doesNotUpdateTransactionTypeIfNot93Or15() {
        FinancialTransaction transaction = FinancialTransaction.builder()
            .referenceNumber("123")
            .transactionType("10")
            .branchSortCode("001")
            .build();
        StandardBankingFile fileContent = StandardBankingFile.builder()
            .financialTransactions(Collections.singletonList(transaction))
            .build();
        OpalFile opalFile = OpalFile.builder().originalFileName("original.txt")
            .fileContent(fileContent)
            .build();

        when(accountNumberTranslation2Repository.findUpdatedReference("001", "123"))
            .thenReturn(Optional.of("updated123"));

        OpalFile result = antTransformer.antTransform(opalFile);

        assertEquals("updated123", ((StandardBankingFile)result.getFileContent())
            .getFinancialTransactions().getFirst().getReferenceNumber());
        assertEquals("10", ((StandardBankingFile)result.getFileContent())
            .getFinancialTransactions().getFirst().getTransactionType());
    }

    @Test
    void antTransform_keepsOriginalReferenceIfNoUpdatedReferenceFound() {
        FinancialTransaction transaction = FinancialTransaction.builder()
            .referenceNumber("123")
            .transactionType("93")
            .branchSortCode("001")
            .build();
        StandardBankingFile fileContent = StandardBankingFile.builder()
            .financialTransactions(Collections.singletonList(transaction))
            .build();
        OpalFile opalFile = OpalFile.builder().originalFileName("original.txt")
            .fileContent(fileContent)
            .build();

        when(accountNumberTranslation2Repository.findUpdatedReference("001", "123"))
            .thenReturn(Optional.empty());

        OpalFile result = antTransformer.antTransform(opalFile);

        assertEquals("123", ((StandardBankingFile)result.getFileContent())
            .getFinancialTransactions().getFirst().getReferenceNumber());
        assertEquals("99", ((StandardBankingFile)result.getFileContent())
            .getFinancialTransactions().getFirst().getTransactionType());
    }
}
