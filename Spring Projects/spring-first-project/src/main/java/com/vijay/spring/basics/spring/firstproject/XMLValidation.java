package com.vijay.spring.basics.spring.firstproject;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class XMLValidation {
    public static void main(String[] args) {
        try {
            // Load the XML schema
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File("schoolResultSchema.xsd"));

            // Load the XML file
            File xmlFile = new File("schoolResult.xml");
            StreamSource source = new StreamSource(xmlFile);

            // Create a validator based on the schema
            Validator validator = schema.newValidator();

            // Validate the XML file against the schema
            validator.validate(source);

            System.out.println(xmlFile.getName() + " is valid");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}