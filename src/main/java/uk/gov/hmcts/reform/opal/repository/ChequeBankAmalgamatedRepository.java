package uk.gov.hmcts.reform.opal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.opal.model.entity.ChequeBankAmalgamatedEntity;

@Repository
public interface ChequeBankAmalgamatedRepository extends JpaRepository<ChequeBankAmalgamatedEntity, String> {

    ChequeBankAmalgamatedEntity findByAmalgamatedCt(String amalgamatedCt);

    ChequeBankAmalgamatedEntity findByAmalgamatedCtAndOldBankAccount(String amalgamatedCt,
                                                                                   String oldBankAccount);
}

