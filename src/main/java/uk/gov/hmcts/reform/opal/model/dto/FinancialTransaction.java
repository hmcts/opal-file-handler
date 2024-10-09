package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinancialTransaction {

    private String branchSortCode;
    private String branchAccountNumber;
    private String accountType;
    private String transactionType;
    private String originatorsSortCode;
    private String originatorsAccountNumber;
    private String originatorsReference;
    private String amount;
    private String originatorsNameOrDescription;
    private String referenceNumber;
    private String date;
}
