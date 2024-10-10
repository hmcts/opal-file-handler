package uk.gov.hmcts.reform.opal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.opal.model.entity.AccountNumberTranslation2Entity;

import java.util.Optional;

@Repository
public interface AccountNumberTranslation2Repository extends JpaRepository<AccountNumberTranslation2Entity, Long> {


    @Query("""
        SELECT ant2.reference
        FROM AccountNumberTranslation2Entity ant1
        JOIN AcNumberRelationshipEntity acr ON acr.acNumberRelationshipId.id1 = ant1.id
        JOIN AccountNumberTranslation2Entity ant2 ON ant2.id = acr.acNumberRelationshipId.id2
        WHERE ant1.mccCt = :mccCt AND ant1.reference = :referenceNumber""")
    Optional<String> findUpdatedReference(@Param("mccCt") String mccCt,
                                          @Param("referenceNumber") String referenceNumber);
}
