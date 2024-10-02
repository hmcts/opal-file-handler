package uk.gov.hmcts.reform.opal.repository;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.entity.AntCtAmalgamatedEntity;

@Component
public class AntCtAmalgamatedTemp implements AntCtAmalgamatedRepo {

    @Override
    public AntCtAmalgamatedEntity findByAmalgamatedCt(String amalgamatedCt) {
        return null;
    }
}
