package uk.gov.hmcts.reform.opal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.opal.model.entity.AntCtAmalgamatedEntity;

@Repository
public interface AntCtAmalgamatedRepository extends JpaRepository<AntCtAmalgamatedEntity, String> {

    AntCtAmalgamatedEntity findByAmalgamatedCt(String amalgamatedCt);
}
