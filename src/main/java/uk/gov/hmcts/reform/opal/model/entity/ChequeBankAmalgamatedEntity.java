package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "CHEQUE_BANK_AMALGAMATED")
@RequiredArgsConstructor
public class ChequeBankAmalgamatedEntity {

    @Id
    @Column(name = "AMALGAMATED_CT")
    String amalgamatedCt;
    @Column(name = "MASTER_CT")
    String masterCt;
    @Column(name = "OLD_BANK_ACC")
    String oldBankAccount;
    @Column(name = "OLD_SORT_CODE")
    String oldSortCode;
    @Column(name = "MASTER_BANK_ACC")
    String masterBankAccount;
    @Column(name = "MASTER_SORT_CODE")
    String masterSortCode;
    @Column(name = "INSERT_TIMESTAMP")
    LocalDateTime insertTimestamp;
    @Column(name = "UPDATE_TIMESTAMP")
    LocalDateTime updateTimestamp;
}
