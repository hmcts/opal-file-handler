package uk.gov.hmcts.reform.opal.model.entity;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

//@Entity
@Data
//@Table(name = "ANT_CT_AMALGAMATED")
@RequiredArgsConstructor
public class AntCtAmalgamatedEntity {

    private String amalgamatedCt;
    private String masterCt;
    private String masterBankAccount;
    private String masterSortCode;
    private LocalDateTime insertTimestamp;
    private LocalDateTime updateTimestamp;
}
