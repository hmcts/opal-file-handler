package uk.gov.hmcts.reform.opal.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.opal.model.dto.DocumentDetail;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.StandardBankingFile;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DwpTransformerTest {

    @InjectMocks
    private DwpTransformer dwpTransformer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void dwpTransform_withValidDwpFile_shouldTransformFile() {
        DwpFile dwpFile = new DwpFile();
        dwpFile.setDocumentDetails(Collections.singletonList(createDummyDocumentDetail("+", "PAYMENT")));

        OpalFile opalFile = OpalFile.builder().newFileName("dwpFile123").fileContent(dwpFile).build();

        OpalFile result = dwpTransformer.dwpTransform(opalFile);

        assertEquals("dwpFile123", result.getNewFileName());
        assertEquals(1, ((StandardBankingFile) result.getFileContent()).getFinancialTransactions().size());
    }

    @Test
    void dwpTransform_withInvalidFileContent_shouldThrowException() {
        OpalFile opalFile = OpalFile.builder().newFileName("dwpFile123").fileContent(
            StandardBankingFile.builder().build()).build();

        assertThrows(ClassCastException.class, () -> dwpTransformer.dwpTransform(opalFile));
    }

    private DocumentDetail createDummyDocumentDetail(String amountSign, String amountType) {
        DocumentDetail documentDetail = new DocumentDetail();
        documentDetail.setDetailAmountSign(amountSign);
        documentDetail.setDetailAmountType(amountType);
        documentDetail.setCustomerRef("1234567890");
        documentDetail.setDateFrom("2023-10-01");
        return documentDetail;
    }
}
