package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@RequiredArgsConstructor
@Table(name = "CHEQUE_NO_AMALGAMATED")
public class ChequeNumberAmalgamatedEntity {

    @Id
    @Column(name = "AMALGAMATED_CT")
    String amalgamatedCt;
    @Column(name = "MASTER_CT")
    String masterCt;
    @Column(name = "OLD_CHEQUE_NO")
    String oldChequeNumber;
    @Column(name = "NEW_CHEQUE_NO")
    String newChequeNumber;
    @Column(name = "INSERT_TIMESTAMP")
    LocalDateTime insertTimestamp;
    @Column(name = "UPDATE_TIMESTAMP")
    LocalDateTime updateTimestamp;
    @Column(name = "ENCOUNTERED_TIMESTAMP")
    LocalDateTime encounteredTimestamp;
}
