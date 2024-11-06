package uk.gov.hmcts.reform.opal.model.dto;

public interface FileName {

    String getPrefix();

    String getDate();

    String getSequence();

    String getExtension();

    void setSequence(String sequence);

    void setExtension(String extension);

    void setPrefix(String prefix);

    void setDate(String date);
}
