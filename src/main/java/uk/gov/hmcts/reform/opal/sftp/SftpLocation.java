package uk.gov.hmcts.reform.opal.sftp;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.opal.sftp.SftpDirection.INBOUND;
import static uk.gov.hmcts.reform.opal.sftp.SftpDirection.OUTBOUND;

@Getter
public enum SftpLocation {

    AUTO_CHEQUES(INBOUND, "auto-cheque", "Comes from OAGS (pushed)"),
    AUTO_CHEQUES_SUCCESS(INBOUND, "auto-cheque/success", "Successfully processed for auto-check"),
    AUTO_CHEQUES_ERROR(INBOUND, "auto-cheque/error", "Error processing for auto-check"),

    AUTO_CASH(INBOUND, "auto-cash", "Comes from OAGS (pushed)"),
    AUTO_CASH_SUCCESS(INBOUND, "auto-cash/success", "Successfully processed for auto-cash"),
    AUTO_CASH_ERROR(INBOUND, "auto-cash/error", "Error processing for auto-cash"),

    NATWEST(INBOUND, "natwest", "Comes from BAIS (pulled)"),
    NATWEST_SUCCESS(INBOUND, "natwest/success", "Successfully processed for natwest"),
    NATWEST_ERROR(INBOUND, "natwest/error", "Error processing for natwest"),

    ALL_PAY_BT_BARCLAY_CARD(INBOUND, "allpay", "Comes from BAIS (pulled)"),
    ALL_PAY_BT_BARCLAY_CARD_SUCCESS(INBOUND, "allpay/success", "Successfully processed for all pay"),
    ALL_PAY_BT_BARCLAY_CARD_ERROR(INBOUND, "allpay/error", "Error processing for all pay"),

    DWP_BAILIFFS(INBOUND, "dwp-bailiffs", "Comes from BAIS (pulled)"),
    DWP_BAILIFFS_SUCCESS(INBOUND, "dwp-bailiffs/success", "Successfully processed for DWP bailiffs"),
    DWP_BAILIFFS_PROCESSING(INBOUND, "dwp-bailiffs/processing", "In progress processing for DWP bailiffs"),
    DWP_BAILIFFS_ERROR(INBOUND, "dwp-bailiffs/error", "Error processing for DWP bailiffs"),

    ALL_PAY(OUTBOUND, "allpay", "Goes to BAIS (pushed)"),
    ALL_PAY_ARCHIVE(OUTBOUND, "allpay-archive", "Goes to OAGS (pushed)"),

    PRINT_LOCATION(OUTBOUND, "print", "Goes to Print Service (pushed)");

    private final SftpDirection direction;
    private final String path;
    private final String description;

    SftpLocation(SftpDirection direction, String path, String description) {
        this.direction = direction;
        this.path = path;
        this.description = description;
    }

    public static List<SftpLocation> getLocations(SftpDirection direction) {
        return Arrays.stream(SftpLocation.values())
            .filter(location -> location.getDirection() == direction)
            .collect(Collectors.toList());
    }

    public static List<SftpLocation> getInboundLocations() {
        return getLocations(INBOUND);
    }

    public static List<SftpLocation> getOutboundLocations() {
        return getLocations(OUTBOUND);
    }

}
