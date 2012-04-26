package org.es4j.serialization.xml.xstream.axon;

import org.dom4j.Document;
import org.es4j.serialization.core.axon.AbstractContentTypeConverter;
import org.es4j.serialization.core.axon.IOUtils;

/**
 * Converter that converts Dom4j Document instances to a byte array. The Document is written as XML string, and
 * converted to bytes using the UTF-8 character set.
 *
 * @author Allard Buijze
 * @since 2.0
 */
public class Dom4JToByteArrayConverter extends AbstractContentTypeConverter<Document, byte[]> {

    @Override
    public Class<Document> expectedSourceType() {
        return Document.class;
    }

    @Override
    public Class<byte[]> targetType() {
        return byte[].class;
    }

    @Override
    public byte[] convert(Document original) {
        return original.asXML().getBytes(IOUtils.UTF8);
    }
}
