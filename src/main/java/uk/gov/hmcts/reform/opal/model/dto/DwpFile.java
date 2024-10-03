package uk.gov.hmcts.reform.opal.model.dto;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import uk.gov.hmcts.reform.opal.util.XMLUtil;

import java.util.List;

@XmlRootElement(name = "PacsTppSchedule")
@Data
public class DwpFile implements FileContent {

    private DocumentHeader documentHeader;
    private List<DocumentDetail> documentDetails;
    private DocumentSummary documentSummary;

    public DwpFile toDwpFile(String fileContents) throws JAXBException {
        return XMLUtil.unmarshal(fileContents, DwpFile.class);
    }
}
