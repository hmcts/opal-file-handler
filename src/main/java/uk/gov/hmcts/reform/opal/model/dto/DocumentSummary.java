package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Data;

@Data
public  class DocumentSummary {
    private String summaryAmountType;
    private String summaryAmountSign;
    private int total02Records;
    private int total03Records;

    }
