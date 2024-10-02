package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public  class DocumentHeader {
    private String creditorID;
    private String batchNumber;
    private LocalDateTime pacsDocumentCreationDate;
    private String pacsDocumentCreationTime;
    private String notificationReference;

}
