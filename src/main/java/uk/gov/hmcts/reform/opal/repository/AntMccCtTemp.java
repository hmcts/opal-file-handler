package uk.gov.hmcts.reform.opal.repository;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.opal.model.entity.AntMccCtEntity;

@Component
public class AntMccCtTemp implements AntMccCtRepo {

    @Override
    public AntMccCtEntity findByCt(String ct) {
        return null;
    }
}
