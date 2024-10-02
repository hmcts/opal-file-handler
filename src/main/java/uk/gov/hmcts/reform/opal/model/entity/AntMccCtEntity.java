package uk.gov.hmcts.reform.opal.model.entity;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

//@Entity
@Data
//@Table(name = "ANT_MCC_CT")
@Builder
public class AntMccCtEntity {

    String area;
    String ct;
    String legacySystem;
    String mccCt;
}
