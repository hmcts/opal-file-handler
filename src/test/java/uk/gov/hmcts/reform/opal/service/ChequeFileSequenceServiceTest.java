package uk.gov.hmcts.reform.opal.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ChequeFileSequenceServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ChequeFileSequenceService chequeFileSequenceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNextSequenceValue_shouldReturnNextSequenceValue() {
        Query query = org.mockito.Mockito.mock(Query.class);
        when(entityManager.createNativeQuery("SELECT nextval('lif01201_filename_seq')")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(123L);

        String result = chequeFileSequenceService.getNextSequenceValue().toString();

        assertEquals("123", result);
    }
}
