/*
 * Copyright (c) 2010-2011. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.es4j.serialization.xml.xstream.axon;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.Dom4JReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.dom4j.Document;
import org.es4j.serialization.api.axon.SerializedObject;
import org.es4j.serialization.core.axon.ChainingConverterFactory;
import org.es4j.serialization.core.axon.ConverterFactory;

/**
 * Serializer that uses XStream to serialize and deserialize arbitrary objects. The XStream instance is configured to
 * deal with the Classes used in Axon Framework in the most compact fashion.
 * <p/>
 * When running on a Sun JVM, XStream does not pose any restrictions on classes to serialize. On other JVM's, however,
 * you need to either implement Serializable, or provide a default constructor (accessible under the JVM's security
 * policy). That means that for portability, you should do either of these two.
 *
 * @author Allard Buijze
 * @see com.thoughtworks.xstream.XStream
 * @since 1.2
 */
public class XStreamSerializer extends AbstractXStreamSerializer {

    /**
     * Initialize a generic serializer using the UTF-8 character set. A default XStream instance (with {@link
     * com.thoughtworks.xstream.io.xml.XppDriver}) is used to perform the serialization.
     */
    public XStreamSerializer() {
    }

    /**
     * Initialize a generic serializer using the UTF-8 character set. The provided XStream instance  is used to perform
     * the serialization.
     *
     * @param xStream XStream instance to use
     */
    public XStreamSerializer(XStream xStream) {
        super(xStream);
    }

    /**
     * Initialize the serializer using the given <code>charset</code>. A default XStream instance (with {@link
     * com.thoughtworks.xstream.io.xml.XppDriver}) is used to perform the serialization.
     *
     * @param charset The character set to use
     */
    public XStreamSerializer(Charset charset) {
        this(charset, new XStream());
    }

    /**
     * Initialize the serializer using the given <code>charset</code> and <code>xStream</code> instance. The
     * <code>xStream</code> instance is configured with several converters for the most common types in Axon.
     *
     * @param charset The character set to use
     * @param xStream The XStream instance to use
     */
    public XStreamSerializer(Charset charset, XStream xStream) {
        super(charset, xStream);
    }

    /**
     * Initialize the serializer using the given <code>charset</code> and <code>xStream</code> instance. The given
     * <code>converterFactory</code> is used to convert serialized objects for use by Upcasters. The
     * <code>xStream</code> instance is configured with several converters for the most common types in Axon.
     *
     * @param charset          The character set to use
     * @param xStream          The XStream instance to use
     * @param converterFactory The factory providing the converter instances for upcasters
     */
    public XStreamSerializer(Charset charset, XStream xStream, ConverterFactory converterFactory) {
        super(charset, xStream, converterFactory);
    }

    @Override
    protected <T> T doSerialize(Object object, Class<T> expectedFormat, XStream xStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        xStream.marshal(object, new CompactWriter(new OutputStreamWriter(baos, getCharset())));
        return convert(byte[].class, expectedFormat, baos.toByteArray());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public Object doDeserialize(SerializedObject serializedObject, XStream xStream) {
        if ("org.dom4j.Document".equals(serializedObject.getContentType().getName())) {
            return xStream.unmarshal(new Dom4JReader((Document) serializedObject.getData()));
        }
        InputStream serializedData = (InputStream) convert(serializedObject.getContentType(), InputStream.class,
                                                           serializedObject.getData());
        return xStream.fromXML(new InputStreamReader(serializedData, getCharset()));
    }

    @Override
    protected void registerConverters(ChainingConverterFactory converterFactory) {
        converterFactory.registerConverter(Dom4JToByteArrayConverter.class);
        converterFactory.registerConverter(InputStreamToDom4jConverter.class);
    }
}
