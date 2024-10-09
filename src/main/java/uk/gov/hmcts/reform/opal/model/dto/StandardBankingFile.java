package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class StandardBankingFile implements FileContent {

    private StringBuilder header;
    private StringBuilder footer;
    private List<FinancialTransaction> financialTransactions;

    public static StandardBankingFile toStandardBankingFile(String contents) {

        StringBuilder header = new StringBuilder();
        StringBuilder footer = new StringBuilder();
        List<FinancialTransaction> financialTransactions;

        //get individual accounts
        // Split the string into lines
        String[] lines = contents.split("\n");
        List<StringBuilder> bodyLines = new ArrayList<>();
        boolean bodyStarted = false;
        boolean headerStarted = false;
        boolean footerStarted = false;

        for (String line : lines) {
            if (line.matches("^\\d.*")) {
                bodyLines.add(new StringBuilder(line));
                bodyStarted = true;
            } else if (bodyStarted) {
                if (footerStarted) {
                    footer.append(System.lineSeparator());
                }
                footer.append(line);
                footerStarted = true;
            } else {
                if (headerStarted) {
                    header.append(System.lineSeparator());
                }
                header.append(line);
                headerStarted = true;
            }
        }

        financialTransactions = new ArrayList<>();

        for (StringBuilder bodyLine : bodyLines) {
            String line = bodyLine.toString();

            FinancialTransaction financialTransaction = FinancialTransaction.builder()
                .branchSortCode(line.substring(0, 6))  // Position 0-5 (inclusive)
                .branchAccountNumber(line.substring(6, 14))  // Position 6-13 (inclusive)
                .accountType(line.substring(14, 15))  // Position 14 (inclusive)
                .transactionType(line.substring(15, 17))  // Position 15-16 (inclusive)
                .originatorsSortCode(line.substring(17, 23))  // Position 17-22 (inclusive)
                .originatorsAccountNumber(line.substring(23, 31))  // Position 23-30 (inclusive)
                .originatorsReference(line.substring(31, 35))  // Position 31-34 (inclusive)
                .amount(line.substring(35, 46))  // Position 35-45 (inclusive)
                .originatorsNameOrDescription(line.substring(46, 64))  // Position 46-63 (inclusive)
                .referenceNumber(line.substring(64, 82))  // Position 64-81 (inclusive)
                .date(line.substring(100, 106))  // Position 100-105 (inclusive)
                .build();

            financialTransactions.add(financialTransaction);
        }

        return StandardBankingFile.builder()
            .header(header)
            .footer(footer)
            .financialTransactions(financialTransactions)
            .build();
    }

    public static String toString(StandardBankingFile file) {
        String spaces = " ".repeat(18);
        StringBuilder sb = new StringBuilder();
        sb.append(file.getHeader());

        if (file.getFinancialTransactions() != null) {
            // Iterate over each financial transaction
            for (FinancialTransaction financialTransaction : file.getFinancialTransactions()) {
                sb.append(System.lineSeparator())
                    .append(fixedLengthString(substituteNull(financialTransaction.getBranchSortCode()), 6))
                    .append(fixedLengthString(substituteNull(financialTransaction.getBranchAccountNumber()), 8))
                    .append(fixedLengthString(substituteNull(financialTransaction.getAccountType()), 1))
                    .append(fixedLengthString(substituteNull(financialTransaction.getTransactionType()), 2))
                    .append(fixedLengthString(substituteNull(financialTransaction.getOriginatorsSortCode()), 6))
                    .append(fixedLengthString(substituteNull(financialTransaction.getOriginatorsAccountNumber()), 8))
                    .append(fixedLengthString(substituteNull(financialTransaction.getOriginatorsReference()), 4))
                    .append(fixedLengthString(substituteNull(financialTransaction.getAmount()), 11))
                    .append(fixedLengthString(substituteNull(financialTransaction
                                                                 .getOriginatorsNameOrDescription()), 18))
                    .append(fixedLengthString(substituteNull(financialTransaction.getReferenceNumber()), 18))
                    .append(spaces) // Append the 18 spaces as per spec
                    .append(fixedLengthString(substituteNull(financialTransaction.getDate()), 6));
            }
        }
        sb.append(System.lineSeparator())
            .append(file.getFooter());
        return sb.toString();

    }

    private static String substituteNull(String value) {
        return value == null ? "" : value;
    }

    private static String fixedLengthString(String value, int length) {
        if (value.length() > length) {
            return value.substring(0, length); // Truncate if longer
        } else {
            return String.format("%-" + length + "s", value); // Pad with spaces if shorter
        }
    }

}
