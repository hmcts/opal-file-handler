package uk.gov.hmcts.reform.opal.repository;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.entity.ChequeNumberAmalgamatedEntity;

@Component
public class ChequeNumberAmalgamatedTemp implements ChequeNumberAmalgamatedRepo {

    @Override
    public ChequeNumberAmalgamatedEntity findByAmalgamatedCt_AmalgamatedCtAndOldChequeNo(String amalgamatedCt,
                                                                                         String oldChequeNo) {
        return null;
    }
}
