package uk.gov.hmcts.reform.opal.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentCardRecord {

    @JsonProperty("business_unit_id")
    String businessUnitId;
    @JsonProperty("account_number")
    String accountNumber;
    @JsonProperty("debtor_title_and_initials")
    String debtorTitleAndInitials;
    @JsonProperty("debtor_name")
    String debtorName;
    @JsonProperty("name_on_card")
    String nameOnCard;
    @JsonProperty("address_line_1")
    String addressLine1;
    @JsonProperty("address_line_2")
    String addressLine2;
    @JsonProperty("address_line_3")
    String addressLine3;
    @JsonProperty("address_line_4")
    String addressLine4;
    @JsonProperty("postcode")
    String postcode;
}
