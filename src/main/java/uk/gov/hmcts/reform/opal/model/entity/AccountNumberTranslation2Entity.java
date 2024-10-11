package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "ACCOUNT_NUMBER_TRANSLATION2")
public class AccountNumberTranslation2Entity {

    @Id
    @Column(name = "ID")
    Long id;
    @Column(name = "MCC_CT")
    String mccCt;
    @Column(name = "REFERENCE")
    String reference;
    @Column(name = "CATEGORY")
    String category;
    @Column(name = "ACC_TYPE")
    String accountType;
    @Column(name = "PAYMENT_TYPE")
    String paymentType;
    @Column(name = "CLOSE_STATUS")
    String closeStatus;
    @Column(name = "REASON_CLOSE_STATUS")
    String reasonCloseStatus;
    @Column(name = "DATE_CLOSE_STATUS")
    LocalDate dateCloseStatus;
    @Column(name = "INSERT_DATE_TIME_STAMP")
    LocalDateTime insertDateTimeStamp;
    @Column(name = "UPDATE_DATE_TIME_STAMP")
    LocalDateTime updateDateTimeStamp;
}
