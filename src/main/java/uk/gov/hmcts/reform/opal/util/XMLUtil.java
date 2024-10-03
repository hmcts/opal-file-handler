package uk.gov.hmcts.reform.opal.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.io.StringWriter;


public class XMLUtil {

    /**
     * Marshals (converts) a Java object to an XML string.
     *
     * @param object the object to marshal
     * @return XML representation of the object as a String
     * @throws JAXBException if any error occurs during the marshalling process
     */
    public static String marshal(Object object) throws JAXBException {
        if (object == null) {
            throw new IllegalArgumentException("Object to marshal cannot be null.");
        }

        // Create a StringWriter to hold the XML content
        StringWriter writer = new StringWriter();
        // Create a JAXBContext for the object's class
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        // Create a marshaller
        Marshaller marshaller = context.createMarshaller();
        // Optional: Set formatted output (pretty print)
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // Marshal the object into the StringWriter
        marshaller.marshal(object, writer);
        return writer.toString();
    }

    /**
     * Unmarshals (converts) an XML string to a Java object.
     *
     * @param <T> the type of the object to unmarshal to
     * @param xml the XML string to unmarshal
     * @param clazz the class of the object to unmarshal
     * @return the unmarshalled object
     * @throws JAXBException if any error occurs during the unmarshalling process
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(String xml, Class<T> clazz) throws JAXBException {
        if (xml == null || xml.isEmpty()) {
            throw new IllegalArgumentException("XML string to unmarshal cannot be null or empty.");
        }

        // Create a StringReader to read the XML string
        StringReader reader = new StringReader(xml);
        // Create a JAXBContext for the class
        JAXBContext context = JAXBContext.newInstance(clazz);
        // Create an unmarshaller
        Unmarshaller unmarshaller = context.createUnmarshaller();
        // Unmarshal the XML to a Java object
        return (T) unmarshaller.unmarshal(reader);
    }
}
