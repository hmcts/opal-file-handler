package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Data;

@Data
public  class DocumentDetail {
    private String customerRef;
    private String recordType;
    private String locationCode;
    private String nationalInsuranceNumberType;
    private String dateFrom;
    private String dateTo;
    private String detailAmountType;
    private String detailAmountSign;
}
