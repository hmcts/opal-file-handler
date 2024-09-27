package uk.gov.hmcts.reform.opal.repository;

//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.opal.model.entity.ChequeBankAmalgamatedEntity;

//@Repository
public interface ChequeBankAmalgamatedRepo {

    ChequeBankAmalgamatedEntity findByAmalgamatedCt_AmalgamatedCt(String amalgamatedCt);

    ChequeBankAmalgamatedEntity findByAmalgamatedCt_AmalgamatedCtAndOldBankAccount(String amalgamatedCt,
                                                                                   String oldBankAccount);

    ChequeBankAmalgamatedEntity findByAmalgamatedCt_AmalgamatedCtAndOldChequeNumber(String amalgamatedCt,
                                                                                    String oldChequeNumber);
}

