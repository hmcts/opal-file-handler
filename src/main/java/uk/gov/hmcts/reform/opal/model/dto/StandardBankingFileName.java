package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StandardBankingFileName {
    //a121_{date}_{source}_{ct}_{sequence}.{extension}

    private final String prefix = "a121";
    private String date;
    private String source;
    private String ct;
    private String sequence;
    private String extension;

    private static String toString(StandardBankingFileName fileName) {
        return String.format("%s_%s_%s_%s_%s.%s",
                             fileName.prefix,
                             fileName.date,
                             fileName.source,
                             fileName.ct,
                             fileName.sequence,
                             fileName.extension);
    }
}
