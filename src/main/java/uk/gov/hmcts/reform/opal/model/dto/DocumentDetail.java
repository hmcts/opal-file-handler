package uk.gov.hmcts.reform.opal.model.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public  class DocumentDetail {
    @XmlElement(name = "CustomerRef", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String customerRef;
    @XmlElement(name = "RecordType", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String recordType;
    @XmlElement(name = "LocationCode", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String locationCode;
    @XmlElement(name = "NationalInsuranceNumber", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String nationalInsuranceNumberType;
    @XmlElement(name = "DateFrom", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String dateFrom;
    @XmlElement(name = "DateTo", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String dateTo;
    @XmlElement(name = "DetailAmount", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String detailAmountType;
    @XmlElement(name = "DetailAmountSign", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String detailAmountSign;
}
