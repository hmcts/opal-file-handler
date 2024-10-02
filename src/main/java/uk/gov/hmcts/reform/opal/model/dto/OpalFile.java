package uk.gov.hmcts.reform.opal.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpalFile {

    private String originalFileName;
    private String newFileName;
    private FileContent fileContent;

}
