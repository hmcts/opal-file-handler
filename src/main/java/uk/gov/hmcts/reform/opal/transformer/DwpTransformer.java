package uk.gov.hmcts.reform.opal.transformer;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.dto.DocumentDetail;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;
import uk.gov.hmcts.reform.opal.model.dto.FinancialTransaction;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DwpTransformer {


    public OpalFile dwpTransform(OpalFile opalFile) {

        return OpalFile.builder()
            .originalFileName(opalFile.getOriginalFileName())
            .newFileName(convertFileName(opalFile.getNewFileName()))
            .fileContent(populateStandardBankingFile((DwpFile) opalFile.getFileContent()))
            .build();
    }


    private StandardBankingFile populateStandardBankingFile(DwpFile dwpFile) {

        List<FinancialTransaction> financialTransactions = new ArrayList<>();

        for (DocumentDetail documentDetail : dwpFile.getDocumentDetails()) {

            if (documentDetail.getDetailAmountSign().equals("+")) {

                financialTransactions.add(FinancialTransaction.builder()
                                              .branchSortCode("ANT_CT_BANK_ACCOUNT")
                                              .branchAccountNumber("ANT_CT_BANK_ACCOUNT")
                                              .accountType("0")
                                              .transactionType("99")
                                              .originatorsSortCode("0".repeat(6))
                                              .originatorsAccountNumber("0".repeat(8))
                                              .originatorsReference("0".repeat(4))
                                              .amount(documentDetail.getDetailAmountType())
                                              .originatorsNameOrDescription(" ".repeat(18))
                                              .referenceNumber(documentDetail.getCustomerRef())
                                              .date(convertToJulian(documentDetail.getDateFrom()))
                                              .build());
            }
        }
        return StandardBankingFile.builder()
            .header(new StringBuilder())
            .financialTransactions(financialTransactions)
            .footer(new StringBuilder())
            .build();
    }

    private String convertFileName(String dwpFileName) {
        return dwpFileName.replace("", "");
    }

    private String convertToJulian(String date) {
        // Parse the input date in the format YYYY-MM-DD
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Get the last two digits of the year
        String year = String.valueOf(localDate.getYear()).substring(2);

        // Get the day of the year (DDD)
        int dayOfYear = localDate.getDayOfYear();

        // Format the Julian date as YYDDD
        return String.format("%s%03d", year, dayOfYear);
    }
}
