package uk.gov.hmcts.reform.opal.model.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public  class DocumentHeader {
    @XmlElement(name = "CreditorID", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String creditorID;
    @XmlElement(name = "BatchNumber", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String batchNumber;
    @XmlElement(name = "PacsDocumentCreationDate", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String pacsDocumentCreationDate;
    @XmlElement(name = "PacsDocumentCreationTime", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String pacsDocumentCreationTime;
    @XmlElement(name = "NotificationReference", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private String notificationReference;
}
