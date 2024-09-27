package uk.gov.hmcts.reform.opal.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChequeNumberAmalgamatedEntity {

    String amalgamatedCt;
    String masterCt;
    String oldChequeNo;
    String newChequeNo;
    LocalDateTime insertTimestamp;
    LocalDateTime updateTimestamp;
    LocalDateTime encounteredTimestamp;
}
