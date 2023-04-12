package com.ukolpakova.soap.parser.utils;

import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class NodeToInputStreamConverter {

    public static final Charset UTF_8_CHARSET = StandardCharsets.UTF_8;

    private NodeToInputStreamConverter() {
    }

    public static InputStream convertNodeToInputStream(Node node) throws TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        String xmlContent = writer.toString();
        return new ByteArrayInputStream(xmlContent.getBytes(UTF_8_CHARSET));
    }
}
