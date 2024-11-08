package uk.gov.hmcts.reform.opal.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ChequeFileSequenceService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Long getNextSequenceValue() {
        Query query = entityManager.createNativeQuery("SELECT nextval('lif01201_filename_seq')");
        return ((Number) query.getSingleResult()).longValue();
    }
}
