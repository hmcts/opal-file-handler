package uk.gov.hmcts.reform.opal.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.dto.FinancialTransaction;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.repository.AccountNumberTranslation2Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AntTransformer {

    private final AccountNumberTranslation2Repository accountNumberTranslation2Repository;

    public OpalFile antTransform(OpalFile opalFile) {

        List<FinancialTransaction> transformedFinancialTransactions = new ArrayList<>();
        //update reference number and transaction type for each financial transaction
        ((StandardBankingFile)opalFile.getFileContent()).getFinancialTransactions().forEach(financialTransaction -> {
            financialTransaction.setReferenceNumber(
                getUpdatedReference(financialTransaction.getBranchSortCode(), financialTransaction.getReferenceNumber())
            );
            // if transaction type is 93 or 15, update the transaction type to 99
            if (financialTransaction.getTransactionType().equals("93")
                || financialTransaction.getTransactionType().equals("15")) {
                financialTransaction.setTransactionType("99");
            }

            transformedFinancialTransactions.add(financialTransaction);
        });

        return OpalFile.builder()
            .originalFileName(opalFile.getOriginalFileName())
            .newFileName(opalFile.getNewFileName())
            .fileContent(
                StandardBankingFile.builder()
                    .header(((StandardBankingFile)opalFile.getFileContent()).getHeader())
                    .financialTransactions(transformedFinancialTransactions)
                    .footer(((StandardBankingFile)opalFile.getFileContent()).getFooter())
                    .build()
            )
            .build();
    }

    private String getUpdatedReference(String mccCt, String referenceNumber) {
        // Use the custom query to find the updated reference in a single call
        Optional<String> updatedReference =
            accountNumberTranslation2Repository.findUpdatedReference(mccCt, referenceNumber);

        // If no result, return the original referenceNumber
        return updatedReference.orElse(referenceNumber);
    }
}
