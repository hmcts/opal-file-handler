package uk.gov.hmcts.reform.opal.model.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class AcNumberRelationshipId implements Serializable {

    @Column(name = "ID1", nullable = false)
    Long id1;
    @Column(name = "ID2", nullable = false)
    Long id2;
}
