package uk.gov.hmcts.reform.opal.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.opal.model.dto.OpalFile;
import uk.gov.hmcts.reform.opal.model.dto.PaymentCardFile;
import uk.gov.hmcts.reform.opal.model.dto.PaymentCardFileName;
import uk.gov.hmcts.reform.opal.model.dto.PaymentCardRecord;
import uk.gov.hmcts.reform.opal.model.entity.AllPayClientCodeEntity;
import uk.gov.hmcts.reform.opal.repository.AllPayClientCodeRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AllPayTransformer {

    private final AllPayClientCodeRepository allPayClientCodeRepository;

    @Transactional
    public OpalFile transform(OpalFile opalFile) {
        if (!(opalFile.getFileContent() instanceof PaymentCardFile paymentCardFile)) {
            throw new IllegalArgumentException(
                "Unsupported file content type: " + opalFile.getFileContent().getClass().getSimpleName()
                    + ". AllPayTransformer only supports PaymentCardFile.");
        }

        List<PaymentCardRecord> transformedPaymentCardRecords = paymentCardFile.getPaymentCardRecords()
            .stream()
            .map(this::transformRecord)
            .toList();

        opalFile.setNewFileName(PaymentCardFileName.fromOriginalFileName(opalFile.getOriginalFileName()));
        opalFile.setFileContent(PaymentCardFile.builder().paymentCardRecords(transformedPaymentCardRecords).build());

        return opalFile;
    }

    private PaymentCardRecord transformRecord(PaymentCardRecord paymentCardRecord) {
        Optional<AllPayClientCodeEntity> optionalEntity = allPayClientCodeRepository
            .findByBusinessUnitId(paymentCardRecord.getBusinessUnitId());
        optionalEntity.ifPresent(allPayClientCodeEntity ->
                                     paymentCardRecord.setBusinessUnitId(allPayClientCodeEntity.getAllPayClientCode()));
        return paymentCardRecord;
    }
}
