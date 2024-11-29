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
@Table(name = "ANT_CT_AMALGAMATED")
@RequiredArgsConstructor
public class AntCtAmalgamatedEntity {

    @Id
    @Column(name = "amalgamated_ct", length = 3, nullable = false)
    String amalgamatedCt;
    @Column(name = "master_ct", length = 3, nullable = false)
    String masterCt;
    @Column(name = "MASTER_BANK_ACCOUNT")
    String masterBankAccount;
    @Column(name = "MASTER_SORT_CODE")
    String masterSortCode;
    @Column(name = "INSERT_TIMESTAMP")
    LocalDateTime insertTimestamp;
    @Column(name = "UPDATE_TIMESTAMP")
    LocalDateTime updateTimestamp;
}
