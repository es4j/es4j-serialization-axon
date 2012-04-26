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

package org.es4j.serializaiton.java.axon;

import java.io.*;
import org.es4j.serialization.api.axon.SerializationException;
import org.es4j.serialization.api.axon.SerializedObject;
import org.es4j.serialization.api.axon.SerializedType;
import org.es4j.serialization.api.axon.Serializer;
import org.es4j.serialization.core.axon.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializer implementation that uses Java serialization to serialize and deserialize object instances. This
 * implementation is very suitable if the life span of the serialized objects allows classes to remain unchanged. If
 * Class definitions need to be changed during the object's life cycle, another implementation, like the
 * {@link org.axonframework.serializer.xml.XStreamSerializer} might be a more suitable alternative.
 *
 * @author Allard Buijze
 * @since 2.0
 */
public class JavaSerializer implements Serializer {

    private static final Logger logger = LoggerFactory.getLogger(JavaSerializer.class);
    private final ConverterFactory converterFactory = new ChainingConverterFactory();

    @Override
    public <T> SerializedObject<T> serialize(Object instance, Class<T> expectedType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                oos.writeObject(instance);
            } finally {
                oos.flush();
            }
        } catch (IOException e) {
            throw new SerializationException("An exception occurred writing serialized data to the output stream", e);
        }
        new SimpleSerializedType(instance.getClass().getName(), revisionOf(instance.getClass()));
        T converted = converterFactory.getConverter(byte[].class, expectedType)
                                      .convert(baos.toByteArray());
        return new SimpleSerializedObject<T>(converted, expectedType, instance.getClass().getName(),
                                             revisionOf(instance.getClass()));
    }

    @Override
    public <T> boolean canSerializeTo(Class<T> expectedRepresentation) {
        return (converterFactory.hasConverter(byte[].class, expectedRepresentation));
    }

    @Override
    public <T> Object deserialize(SerializedObject<T> serializedObject) {
        SerializedObject<InputStream> converted = converterFactory.getConverter(serializedObject.getContentType(),
                                                                                InputStream.class)
                                                                  .convert(serializedObject);
        InputStream stream = converted.getData();
        try {
            ObjectInputStream ois = new ObjectInputStream(stream);
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new SerializationException("An error occurred while deserializing: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new SerializationException("The theoretically impossible has just happened: "
                                                     + "An IOException while reading to a ByteArrayInputStream.", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Override
    public Class classForType(SerializedType type) {
        try {
            return Class.forName(type.getName());
        } catch (ClassNotFoundException e) {
            logger.warn("Could not load class for serialized type [{}] revision {}",
                        type.getName(), type.getRevision());
            return null;
        }
    }

    /**
     * Returns the revision number for the given <code>type</code>. The default implementation checks for an {@link
     * Revision @Revision} annotation, and returns <code>0</code> if none was found. This method can be safely
     * overridden by subclasses.
     * <p/>
     * The revision number is used by upcasters to decide whether they need to process a certain serialized event.
     * Generally, the revision number needs to be increased each time the structure of an event has been changed in an
     * incompatible manner.
     *
     * @param type The type for which to return the revision number
     * @return the revision number for the given <code>type</code>
     */
    protected String revisionOf(Class<?> type) {
        Revision revision = type.getAnnotation(Revision.class);
        return revision == null ? null : revision.value();
    }
}
