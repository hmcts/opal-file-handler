package uk.gov.hmcts.reform.opal.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.PaymentCardFile;
import uk.gov.hmcts.reform.opal.model.dto.PaymentCardFileName;
import uk.gov.hmcts.reform.opal.model.dto.PaymentCardRecord;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;
import uk.gov.hmcts.reform.opal.model.entity.AllPayClientCodeEntity;
import uk.gov.hmcts.reform.opal.repository.AllPayClientCodeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AllPayTransformerTest {

    @Mock
    private AllPayClientCodeRepository allPayClientCodeRepository;

    @InjectMocks
    private AllPayTransformer allPayTransformer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transform_shouldTransformOpalFileSuccessfully() {
        PaymentCardRecord record = PaymentCardRecord.builder().businessUnitId("BU1").build();
        PaymentCardFile paymentCardFile = PaymentCardFile.builder()
            .paymentCardRecords(List.of(record))
            .build();
        OpalFile opalFile = OpalFile.builder().fileContent(paymentCardFile).originalFileName("PCR_05022024_000.txt")
            .build();

        AllPayClientCodeEntity entity = new AllPayClientCodeEntity();
        entity.setAllPayClientCode("APC1");
        when(allPayClientCodeRepository.findByBusinessUnitId("BU1")).thenReturn(Optional.of(entity));

        OpalFile result = allPayTransformer.transform(opalFile);

        assertEquals("APC1", ((PaymentCardFile) result.getFileContent()).getPaymentCardRecords()
            .get(0).getBusinessUnitId());
        assertEquals(PaymentCardFileName.fromOriginalFileName("PCR_05022024_000.txt"), result.getNewFileName());
    }

    @Test
    void transform_shouldThrowExceptionForUnsupportedFileContent() {
        OpalFile opalFile = OpalFile.builder().fileContent(StandardBankingFile.builder().build()).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            allPayTransformer.transform(opalFile));

        assertEquals(
            "Unsupported file content type: StandardBankingFile."
                + " AllPayTransformer only supports PaymentCardFile.",
                     exception.getMessage());
    }

}
