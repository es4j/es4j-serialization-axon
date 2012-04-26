package org.es4j.serialization.core.axon;

import java.util.Map;
import org.es4j.messaging.api.axon.GenericMessage;
import org.es4j.messaging.api.axon.Message;
import org.es4j.messaging.api.axon.MetaData;
import org.es4j.serialization.api.axon.SerializedObject;
import org.es4j.serialization.api.axon.Serializer;

/**
 * Message implementation that is optimized to cope with serialized Payload and MetaData. The Payload and MetaData will
 * only be deserialized when requested.
 * <p/>
 * This implementation is Serializable as per Java specification. Both MetaData and Payload are deserialized prior to
 * being written to the OutputStream.
 *
 * @param <T> The type of payload contained in this message
 * @author Allard Buijze
 * @since 2.0
 */
public class SerializedMessage<T> implements Message<T> {

    private static final long serialVersionUID = 6332429891815042291L;

    private final String identifier;
    private final transient LazyDeserializingObject<MetaData> serializedMetaData; // NOSONAR
    private final transient LazyDeserializingObject<T> serializedPayload; // NOSONAR

    /**
     * Reconstructs a Message using the given <code>identifier</code>, <code>serializedPayload</code>,
     * <code>serializedMetaData</code> and <code>serializer</code>.
     *
     * @param identifier         The identifier of the message
     * @param serializedPayload  The serialized payload of the message
     * @param serializedMetaData The serialized meta data of the message
     * @param serializer         The serializer to deserialize the payload and meta data with
     */
    public SerializedMessage(String identifier, SerializedObject<?> serializedPayload,
                             SerializedObject<?> serializedMetaData, Serializer serializer) {
        this.identifier = identifier;
        this.serializedMetaData = new LazyDeserializingObject<MetaData>(serializedMetaData, serializer);
        this.serializedPayload = new LazyDeserializingObject<T>(serializedPayload, serializer);
    }

    private SerializedMessage(SerializedMessage<T> message, Map<String, Object> metaData) {
        this.identifier = message.getIdentifier();
        this.serializedMetaData = new LazyDeserializingObject<MetaData>(MetaData.from(metaData));
        this.serializedPayload = message.serializedPayload;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public MetaData getMetaData() {
        MetaData metaData = serializedMetaData.getObject();
        return metaData == null ? MetaData.emptyInstance() : metaData;
    }

    @Override
    public T getPayload() {
        return serializedPayload.getObject();
    }

    @Override
    public Class getPayloadType() {
        return serializedPayload.getType();
    }

    @Override
    public SerializedMessage<T> withMetaData(Map<String, Object> metaData) {
        if (this.serializedMetaData.getObject().equals(metaData)) {
            return this;
        }
        return new SerializedMessage<T>(this, metaData);
    }

    @Override
    public SerializedMessage<T> andMetaData(Map<String, Object> metaData) {
        if (metaData.isEmpty()) {
            return this;
        }
        return new SerializedMessage<T>(this, getMetaData().mergedWith(metaData));
    }

    /**
     * Indicates whether the payload of this message has already been deserialized.
     *
     * @return <code>true</code> if the payload is deserialized, otherwise <code>false</code>
     */
    public boolean isPayloadDeserialized() {
        return serializedPayload.isDeserialized();
    }

    /**
     * Java Serialization API Method that provides a replacement to serialize, as the fields contained in this instance
     * are not serializable themselves.
     *
     * @return the GenericMessage to use as a replacement when serializing
     */
    protected Object writeReplace() {
        return new GenericMessage<T>(identifier, getPayload(), getMetaData());
    }
}
