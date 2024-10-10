package uk.gov.hmcts.reform.opal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.opal.model.entity.ChequeNumberAmalgamatedEntity;

public interface ChequeNumberAmalgamatedRepository extends JpaRepository<ChequeNumberAmalgamatedEntity, String> {

    ChequeNumberAmalgamatedEntity findByAmalgamatedCtAndOldChequeNumber(String amalgamatedCt,
                                                                                  String oldChequeNumber);
}
