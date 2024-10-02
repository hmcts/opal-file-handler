package uk.gov.hmcts.reform.opal.repository;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.entity.ChequeBankAmalgamatedEntity;

@Component
public class ChequeBankAmalgamatedTemp implements ChequeBankAmalgamatedRepo {

    @Override
    public ChequeBankAmalgamatedEntity findByAmalgamatedCt_AmalgamatedCt(String amalgamatedCt) {
        return null;
    }

    @Override
    public ChequeBankAmalgamatedEntity findByAmalgamatedCt_AmalgamatedCtAndOldBankAccount(String amalgamatedCt,
                                                                                          String oldBankAccount) {
        return null;
    }

    @Override
    public ChequeBankAmalgamatedEntity findByAmalgamatedCt_AmalgamatedCtAndOldChequeNumber(String amalgamatedCt,
                                                                                           String oldChequeNumber) {
        return null;
    }
}
