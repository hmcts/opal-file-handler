package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;
import java.util.stream.Stream;

@Data
@Builder
public class StandardBankingFileName {
    //{prefix}_{date}_{source}_{ct}_{sequence}.{extension}

    private String prefix;
    private String date;
    private String source;
    private String ct;
    private String sequence;
    private String extension;


    public static String toString(StandardBankingFileName fileName) {
        return Stream.of(fileName.prefix, fileName.date, fileName.source, fileName.ct, fileName.sequence)
            .filter(Objects::nonNull) // Exclude null values
            .reduce((a, b) -> a + "_" + b) // Join with underscore
            .orElse("") + "." + fileName.extension; // Append the extension
    }


    public static StandardBankingFileName toStandardBankingFileName(String fileName) {
        String[] parts = fileName.split("_");
        return StandardBankingFileName.builder()
            .prefix(parts[0])
            .date(parts[1])
            .source(parts[2])
            .ct(parts[3])
            .sequence(parts[4])
            .extension(parts[5])
            .build();
    }
}
