package uk.gov.hmcts.reform.opal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.opal.model.entity.AllPayClientCodeEntity;

import java.util.Optional;

@Repository
public interface AllPayClientCodeRepository extends JpaRepository<AllPayClientCodeEntity, String> {


    Optional<AllPayClientCodeEntity> findByBusinessUnitId(String businessUnitId);
}
