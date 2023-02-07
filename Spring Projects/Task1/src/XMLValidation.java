import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class XMLValidation {
    public static void main(String[] args) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File("schoolResultSchema.xsd"));

            File xmlFile = new File("schoolResult.xml");
            StreamSource source = new StreamSource(xmlFile);

            Validator validator = schema.newValidator();

            validator.validate(source);

            System.out.println(xmlFile.getName() + " is valid");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}