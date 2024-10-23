package uk.gov.hmcts.reform.opal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.opal.model.entity.AntCtBankAccountEntity;

public interface AntCtBankAccountRepository extends JpaRepository<AntCtBankAccountEntity, String> {

    AntCtBankAccountEntity findByDwpCourtCode(String dwpCourtCode);
}
