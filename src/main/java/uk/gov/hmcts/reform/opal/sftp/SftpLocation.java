package uk.gov.hmcts.reform.opal.sftp;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.opal.sftp.SftpDirection.INBOUND;
import static uk.gov.hmcts.reform.opal.sftp.SftpDirection.OUTBOUND;

@Getter
public enum SftpLocation {

    AUTO_CHEQUES(INBOUND, "auto-cheque", "Comes from OAGS (pushed)", "VB"),
    AUTO_CHEQUES_SUCCESS(INBOUND, "auto-cheque/success", "Successfully processed for auto-check", null),
    AUTO_CHEQUES_ERROR(INBOUND, "auto-cheque/error", "Error processing for auto-check", null),

    AUTO_CASH(INBOUND, "auto-cash", "Comes from OAGS (pushed)", "VB"),
    AUTO_CASH_SUCCESS(INBOUND, "auto-cash/success", "Successfully processed for auto-cash", null),
    AUTO_CASH_ERROR(INBOUND, "auto-cash/error", "Error processing for auto-cash", null),

    NATWEST(INBOUND, "natwest", "Comes from BAIS (pulled)", "NW"),
    NATWEST_SUCCESS(INBOUND, "natwest/success", "Successfully processed for natwest", null),
    NATWEST_ERROR(INBOUND, "natwest/error", "Error processing for natwest", null),

    ALL_PAY_BT_BARCLAY_CARD(INBOUND, "allpay", "Comes from BAIS (pulled)", "AP"),
    ALL_PAY_BT_BARCLAY_CARD_SUCCESS(INBOUND, "allpay/success", "Successfully processed for all pay", null),
    ALL_PAY_BT_BARCLAY_CARD_ERROR(INBOUND, "allpay/error", "Error processing for all pay", null),

    DWP_BAILIFFS(INBOUND, "dwp-bailiffs", "Comes from BAIS (pulled)", "DB"),
    DWP_BAILIFFS_SUCCESS(INBOUND, "dwp-bailiffs/success", "Successfully processed for DWP bailiffs", null),
    DWP_BAILIFFS_PROCESSING(
        INBOUND, "dwp-bailiffs/processing", "In progress processing for DWP bailiffs", null),
    DWP_BAILIFFS_ERROR(INBOUND, "dwp-bailiffs/error", "Error processing for DWP bailiffs", null),
    DWP_BAILIFFS_ARCHIVE(INBOUND,
                         "dwp-bailiffs/archive",
                         "Archive original file for successful transformations", null),

    ALL_PAY(OUTBOUND, "allpay", "Goes to BAIS (pushed)", "AP"),
    ALL_PAY_ARCHIVE(OUTBOUND, "allpay-archive", "Goes to OAGS (pushed)", null),

    PRINT_LOCATION(OUTBOUND, "print", "Goes to Print Service (pushed)", null);

    private final SftpDirection direction;
    private final String path;
    private final String description;
    private final String source;

    SftpLocation(SftpDirection direction, String path, String description, String source) {
        this.direction = direction;
        this.path = path;
        this.description = description;
        this.source = source;
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
