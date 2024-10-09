package uk.gov.hmcts.reform.opal.model.dto;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import uk.gov.hmcts.reform.opal.util.XMLUtil;

import java.util.List;

@XmlRootElement(name = "PacsTppSchedule", namespace = "http://www.dwp.gsi.gov.uk/pacs")
@XmlType(propOrder = { "documentHeader", "documentDetails", "documentSummary" })
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DwpFile implements FileContent {

    @XmlElement(name = "DocumentHeader", required = true, namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private DocumentHeader documentHeader;
    @XmlElement(name = "DocumentDetail", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private List<DocumentDetail> documentDetails;
    @XmlElement(name = "DocumentSummary", namespace = "http://www.dwp.gsi.gov.uk/pacs")
    private DocumentSummary documentSummary;

    public static DwpFile toDwpFile(String fileContents) throws JAXBException {
        return XMLUtil.unmarshal(fileContents, DwpFile.class);
    }
}
