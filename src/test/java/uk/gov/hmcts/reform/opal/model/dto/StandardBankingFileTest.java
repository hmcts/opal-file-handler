package uk.gov.hmcts.reform.opal.model.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class StandardBankingFileTest {

    @Test
    void toStandardBankingFile_withValidContent_shouldParseCorrectly() {

        StandardBankingFile file = StandardBankingFile.toStandardBankingFile(getTestFileContent());

        assertEquals("Header Line", file.getHeader().toString().trim());
        assertEquals("Footer Line", file.getFooter().toString().trim());
        assertEquals(1, file.getFinancialTransactions().size());
        FinancialTransaction transaction = file.getFinancialTransactions().getFirst();
        assertEquals("123456", transaction.getBranchSortCode());
        assertEquals("12345678", transaction.getBranchAccountNumber());
        assertEquals("1", transaction.getAccountType());
        assertEquals("12", transaction.getTransactionType());
        assertEquals("123456", transaction.getOriginatorsSortCode());
        assertEquals("12345678", transaction.getOriginatorsAccountNumber());
        assertEquals("1234", transaction.getOriginatorsReference());
        assertEquals("12345678901", transaction.getAmount());
        assertEquals("                  ", transaction.getOriginatorsNameOrDescription());
        assertEquals("21003440N         ", transaction.getReferenceNumber());
        assertEquals(" 21122", transaction.getDate());
    }

    @Test
    void toStandardBankingFile_withEmptyContent_shouldReturnEmptyFile() {
        String content = "";
        StandardBankingFile file = StandardBankingFile.toStandardBankingFile(content);

        assertEquals("", file.getHeader().toString());
        assertEquals("", file.getFooter().toString());
        assertTrue(file.getFinancialTransactions().isEmpty());
    }

    @Test
    void toString_withValidFile_shouldReturnFormattedString() {
        StandardBankingFile file = StandardBankingFile.builder()
            .header(new StringBuilder("Header Line"))
            .footer(new StringBuilder("Footer Line"))
            .financialTransactions(List.of(
                FinancialTransaction.builder()
                    .branchSortCode("123456")
                    .branchAccountNumber("12345678")
                    .accountType("1")
                    .transactionType("12")
                    .originatorsSortCode("123456")
                    .originatorsAccountNumber("12345678")
                    .originatorsReference("1234")
                    .amount("12345678901")
                    .originatorsNameOrDescription("                  ")
                    .referenceNumber("21003440N         ")
                    .date(" 21122")
                    .build()
            ))
            .build();

        String result = StandardBankingFile.toString(file);
        assertEquals(getTestFileContent(), result);
    }

    @Test
    void toString_withEmptyFile_shouldReturnEmptyString() {
        StandardBankingFile file = StandardBankingFile.builder()
            .header(new StringBuilder())
            .footer(new StringBuilder())
            .financialTransactions(new ArrayList<>())
            .build();

        String result = StandardBankingFile.toString(file);
        String expected = "\n";
        assertEquals(expected, result);
    }

    private String getTestFileContent() {
        return """
            Header Line
            1234561234567811212345612345678123412345678901                  21003440N                            21122
            Footer Line""";
    }
}
