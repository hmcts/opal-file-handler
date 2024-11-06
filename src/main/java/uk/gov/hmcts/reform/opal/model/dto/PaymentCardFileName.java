package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.opal.util.DateUtil;

@Data
@Builder
public class PaymentCardFileName implements FileName {
    //NC_HMCS{YYYYMMdd}.{sequence_without_leading_zeros}.txt

    private String prefix;
    private String date;
    private String sequence;
    private String extension;


    public static String toString(PaymentCardFileName fileName) {
        return fileName.getPrefix()
            + fileName.getDate()
            + "."
            + fileName.getSequence()
            + "."
            + fileName.getExtension();

    }

    public static PaymentCardFileName fromOriginalFileName(String fileName) {

        String[] parts = fileName.split("_");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid file name format");
        }

        String sequence = parts[2].replaceFirst("^0+(?!$)", "");
        if (sequence.isEmpty()) {
            sequence = "0";
        }

        return PaymentCardFileName.builder()
            .prefix("NC_HMCS")
            .date(DateUtil.flipDate(parts[1]))
            .sequence(sequence)
            .extension("txt")
            .build();
    }
}
