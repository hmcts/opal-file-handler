package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "ANT_CT_BANK_ACCOUNT")
public class AntCtBankAccountEntity {

    @Id
    @Column(name = "CT")
    String ct;
    @Column(name = "SORTCODE")
    String sortCode;
    @Column(name = "ACCOUNT_NO")
    String accountNumber;
    @Column(name = "DWP_COURT_CODE")
    String dwpCourtCode;
}
