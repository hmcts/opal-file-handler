package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@Table(name = "ANT_MCC_CT")
@RequiredArgsConstructor
public class AntMccCtEntity {

    @Id
    @Column(name = "AREA")
    String area;
    @Column(name = "CT")
    String ct;
    @Column(name = "LEGACY_SYSTEM")
    String legacySystem;
    @Column(name = "MCC_CT")
    String mccCt;
}
