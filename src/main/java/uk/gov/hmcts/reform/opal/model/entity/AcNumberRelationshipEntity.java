package uk.gov.hmcts.reform.opal.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.reform.opal.model.entity.id.AcNumberRelationshipId;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "AC_NUMBER_REL")
public class AcNumberRelationshipEntity {

    @EmbeddedId
    AcNumberRelationshipId acNumberRelationshipId;
    @Column(name = "RELATIONSHIP")
    String relationship;
    @Column(name = "STATUS")
    String status;
    @Column(name = "INSERT_TIMESTAMP")
    LocalDateTime insertDateTimeStamp;
    @Column(name = "UPDATE_TIMESTAMP")
    LocalDateTime updateDateTimeStamp;
}
