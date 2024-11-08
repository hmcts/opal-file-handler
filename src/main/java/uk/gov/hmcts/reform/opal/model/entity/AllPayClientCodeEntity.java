package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ALL_PAY_CLIENT_CODE")
@Data
public class AllPayClientCodeEntity {

    @Id
    private String businessUnitId;
    private String allPayClientCode;
}
