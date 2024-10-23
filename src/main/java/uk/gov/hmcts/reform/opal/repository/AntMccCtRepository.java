package uk.gov.hmcts.reform.opal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.opal.model.entity.AntMccCtEntity;

@Repository
public interface AntMccCtRepository extends JpaRepository<AntMccCtEntity, String> {

    AntMccCtEntity findByCt(String ct);

    @Query("SELECT a1 FROM AntMccCtEntity a1 JOIN AntCtBankAccountEntity a2 "
        + "ON a1.ct = a2.ct "
        + "WHERE a2.sortCode = :branchSortCode AND a2.accountNumber = :branchAccountNumber")
    AntMccCtEntity findByBranchSortCodeAndBranchAccountNumber(@Param("branchSortCode") String branchSortCode,
                                                              @Param("branchAccountNumber") String branchAccountNumber);

}
