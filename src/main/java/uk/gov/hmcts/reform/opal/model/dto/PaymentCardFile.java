package uk.gov.hmcts.reform.opal.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PaymentCardFile implements FileContent {

    @JsonProperty("records")
    List<PaymentCardRecord> paymentCardRecords;
}
