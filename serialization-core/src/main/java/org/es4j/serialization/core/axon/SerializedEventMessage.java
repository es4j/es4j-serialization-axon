package org.es4j.serialization.core.axon;

import java.util.Map;
import org.es4j.messaging.api.axon.EventMessage;
import org.es4j.messaging.api.axon.GenericEventMessage;
import org.es4j.messaging.api.axon.MetaData;
import org.es4j.serialization.api.axon.SerializedObject;
import org.es4j.serialization.api.axon.Serializer;
import org.joda.time.DateTime;

/**
 * EventMessage implementation that is optimized to cope with serialized Payload and MetaData. The Payload and
 * MetaData will only be deserialized when requested. This means that loaded event for which there is no handler will
 * never be deserialized.
 * <p/>
 * This implementation is Serializable as per Java specification. Both MetaData and Payload are deserialized prior to
 * being written to the OutputStream.
 *
 * @param <T> The type of payload contained in this message
 * @author Allard Buijze
 * @since 2.0
 */
public class SerializedEventMessage<T> implements EventMessage<T> {

    private static final long serialVersionUID = -4704515337335869770L;
    private final DateTime timestamp;
    private final SerializedMessage<T> message;

    /**
     * Constructor to reconstruct an EventMessage using serialized data
     *
     * @param eventIdentifier    The identifier of the message
     * @param timestamp          The timestamp of the event message
     * @param serializedPayload  The serialized payload of the message
     * @param serializedMetaData The serialized meta data of the message
     * @param serializer         The serializer to deserialize the payload and meta data with
     */
    public SerializedEventMessage(String eventIdentifier, DateTime timestamp, SerializedObject<?> serializedPayload,
                                  SerializedObject<?> serializedMetaData, Serializer serializer) {
        message = new SerializedMessage<T>(eventIdentifier, serializedPayload, serializedMetaData, serializer);
        this.timestamp = timestamp;
    }

    private SerializedEventMessage(SerializedEventMessage<T> original, Map<String, Object> metaData) {
        message = original.message.withMetaData(metaData);
        this.timestamp = original.getTimestamp();
    }

    @Override
    public String getIdentifier() {
        return message.getIdentifier();
    }

    @Override
    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public MetaData getMetaData() {
        return message.getMetaData();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public T getPayload() {
        return message.getPayload();
    }

    @Override
    public Class getPayloadType() {
        return message.getPayloadType();
    }

    @Override
    public SerializedEventMessage<T> withMetaData(Map<String, Object> newMetaData) {
        if (getMetaData().equals(newMetaData)) {
            return this;
        } else {
            return new SerializedEventMessage<T>(this, newMetaData);
        }
    }

    @Override
    public EventMessage<T> andMetaData(Map<String, Object> additionalMetaData) {
        MetaData newMetaData = getMetaData().mergedWith(additionalMetaData);
        return withMetaData(newMetaData);
    }

    /**
     * Indicates whether the payload of this message has already been deserialized.
     *
     * @return <code>true</code> if the payload is deserialized, otherwise <code>false</code>
     */
    public boolean isPayloadDeserialized() {
        return message.isPayloadDeserialized();
    }

    /**
     * Java Serialization API Method that provides a replacement to serialize, as the fields contained in this instance
     * are not serializable themselves.
     *
     * @return the GenericEventMessage to use as a replacement when serializing
     */
    protected Object writeReplace() {
        return new GenericEventMessage<T>(getIdentifier(), getTimestamp(), getPayload(), getMetaData());
    }
}
