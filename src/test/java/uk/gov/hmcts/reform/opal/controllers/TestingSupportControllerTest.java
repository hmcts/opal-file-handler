package uk.gov.hmcts.reform.opal.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.opal.service.DwpBailiffsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

class TestingSupportControllerTest {

    @Mock
    private DwpBailiffsService dwpBailiffsService;

    @InjectMocks
    private TestingSupportController testingSupportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void runDwpBailiffs_shouldReturnOkResponse_whenServiceSucceeds() {
        doNothing().when(dwpBailiffsService).process();

        ResponseEntity<String> response = testingSupportController.runDwpBailiffs();

        assertEquals(ResponseEntity.ok("DWP_BAILIFFS JOB INVOKED"), response);
    }

    @Test
    void runDwpBailiffs_shouldReturnErrorResponse_whenServiceThrowsException() {
        doThrow(new RuntimeException("Service failure")).when(dwpBailiffsService).process();

        ResponseEntity<String> response = testingSupportController.runDwpBailiffs();

        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("DWP_BAILIFFS JOB FAILED: Service failure"), response);
    }
}
