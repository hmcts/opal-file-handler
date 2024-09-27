package uk.gov.hmcts.reform.opal.repository;

import uk.gov.hmcts.reform.opal.model.entity.ChequeNumberAmalgamatedEntity;

public interface ChequeNumberAmalgamatedRepo {

    ChequeNumberAmalgamatedEntity findByAmalgamatedCt_AmalgamatedCtAndOldChequeNo(String amalgamatedCt,
                                                                                  String oldChequeNo);
}
