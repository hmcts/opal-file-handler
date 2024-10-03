package uk.gov.hmcts.reform.opal.model.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public  class DocumentSummary {
    @XmlElement(name = "SummaryAmount", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String summaryAmountType;
    @XmlElement(name = "SummaryAmountSign", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String summaryAmountSign;
    @XmlElement(name = "Total02Records", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private int total02Records;
    @XmlElement(name = "Total03Records", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private int total03Records;

}
