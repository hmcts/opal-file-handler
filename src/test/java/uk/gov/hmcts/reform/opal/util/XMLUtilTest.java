package uk.gov.hmcts.reform.opal.util;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.opal.model.dto.DocumentHeader;
import uk.gov.hmcts.reform.opal.model.dto.DwpFile;

public class XMLUtilTest {

    @Test
    void marshalValidObjectToString() throws JAXBException {
        DwpFile sample = new DwpFile();
        DocumentHeader header = new DocumentHeader();
        header.setBatchNumber("123");
        header.setCreditorID("456");
        sample.setDocumentHeader(header);
        String xml = XMLUtil.marshal(sample);
        Assertions.assertNotNull(xml);
        Assertions.assertTrue(xml.contains("123"));
        Assertions.assertTrue(xml.contains("456"));
    }

    @Test
    void marshalNullObjectThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> XMLUtil.marshal(null));
    }

    @Test
    void unmarshalValidXmlStringToObject() throws JAXBException {
        DwpFile sample = XMLUtil.unmarshal(getSampleXml(), DwpFile.class);
        Assertions.assertNotNull(sample);
        Assertions.assertEquals("456", sample.getDocumentHeader().getBatchNumber());
        Assertions.assertEquals("123", sample.getDocumentHeader().getCreditorID());
    }

    @Test
    void unmarshalNullXmlStringThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> XMLUtil.unmarshal(null, DwpFile.class));
    }

    @Test
    void unmarshalEmptyXmlStringThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> XMLUtil.unmarshal("", DwpFile.class));
    }

    public static String getSampleXml() {
        return """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <PacsTppSchedule xmlns="http://www.dwp.gsi.gov.uk/pacs">
                <DocumentHeader>
                    <CreditorID>123</CreditorID>
                    <BatchNumber>456</BatchNumber>
                    <PacsDocumentCreationDate>2023-09-10</PacsDocumentCreationDate>
                    <PacsDocumentCreationTime>10:59:59</PacsDocumentCreationTime>
                    <NotificationReference>0000000002</NotificationReference>
                </DocumentHeader>
                <DocumentDetail>
                    <CustomerRef>230001111</CustomerRef>
                    <RecordType>02</RecordType>
                    <LocationCode>100202</LocationCode>
                    <DateFrom>2023-09-03</DateFrom>
                    <DateTo>2023-09-03</DateTo>
                    <DetailAmountType>0000000199</DetailAmountType>
                    <DetailAmountSign>+</DetailAmountSign>
                </DocumentDetail>
                <DocumentSummary>
                    <SummaryAmountType>0000000035</SummaryAmountType>
                    <SummaryAmountSign>+</SummaryAmountSign>
                    <Total02Records>5</Total02Records>
                    <Total03Records>4</Total03Records>
                </DocumentSummary>
            </PacsTppSchedule>
            """;
    }
}
