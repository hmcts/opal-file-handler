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
                .transactionCode(line.substring(14, 17))  // Position 14-16 (inclusive)
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
        for (FinancialTransaction financialTransaction : file.getFinancialTransactions()) {
            sb.append(System.lineSeparator())
                .append(financialTransaction.getBranchSortCode())
                .append(financialTransaction.getBranchAccountNumber())
                .append(financialTransaction.getTransactionCode())
                .append(financialTransaction.getOriginatorsSortCode())
                .append(financialTransaction.getOriginatorsAccountNumber())
                .append(financialTransaction.getOriginatorsReference())
                .append(financialTransaction.getAmount())
                .append(financialTransaction.getOriginatorsNameOrDescription())
                .append(financialTransaction.getReferenceNumber())
                .append(spaces)
                .append(financialTransaction.getDate());
        }
        sb.append(System.lineSeparator())
            .append(file.getFooter());
        return sb.toString();

    }

}
