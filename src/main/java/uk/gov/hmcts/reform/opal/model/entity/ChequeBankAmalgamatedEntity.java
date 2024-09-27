package uk.gov.hmcts.reform.opal.model.entity;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

//@Entity
@Data
//@Table(name = "CHEQUE_BANK_AMALGAMATED")
@RequiredArgsConstructor
public class ChequeBankAmalgamatedEntity {

    private String amalgamatedCt;
    private String masterCt;
    private String oldBankAcc;
    private String oldSortCode;
    private String masterBankAcc;
    private String masterSortCode;
    private LocalDateTime insertTimestamp;
    private LocalDateTime updateTimestamp;
}
