package org.es4j.serialization.core.axon;

import org.es4j.serialization.api.axon.SerializedObject;


/**
 * Abstract implementation of the ContentTypeConverter for convenience purposes. It implements the {@link
 * #convert(org.axonframework.serializer.SerializedObject)} method, based on information available through the other
 * methods.
 *
 * @param <S> The source data type representing the serialized object
 * @param <T> The target data type representing the serialized object
 * @author Allard Buijze
 * @since 2.0
 */
public abstract class AbstractContentTypeConverter<S, T> implements ContentTypeConverter<S, T> {

    @Override
    public SerializedObject<T> convert(SerializedObject<S> original) {
        return new SimpleSerializedObject<T>(convert(original.getData()), targetType(), original.getType());
    }
}
