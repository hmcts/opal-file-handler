package uk.gov.hmcts.reform.opal.repository;

//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import uk.gov.hmcts.reform.opal.model.entity.AntCtAmalgamatedEntity;

public interface AntCtAmalgamatedRepo {

    AntCtAmalgamatedEntity findByAmalgamatedCt(String amalgamatedCt);
}
