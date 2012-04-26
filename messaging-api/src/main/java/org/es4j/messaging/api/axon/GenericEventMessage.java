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

package org.es4j.messaging.api.axon;

import java.util.Map;
import org.joda.time.DateTime;

/**
 * Generic implementation of the EventMessage interface. It simply keeps a reference to the payload and MetaData.
 *
 * @param <T> The type of payload contained in this Message
 * @author Allard Buijze
 * @since 2.0
 */
public class GenericEventMessage<T> extends GenericMessage<T> implements EventMessage<T> {

    private static final long serialVersionUID = -8370948891267874107L;

    private final DateTime timestamp;

    /**
     * Returns the given event as an EventMessage. If <code>event</code> already implements EventMessage, it is
     * returned as-is. Otherwise, the given <code>event</code> is wrapped into a GenericEventMessage as its payload.
     *
     * @param event the event to wrap as EventMessage
     * @return an EventMessage containing given <code>event</code> as payload, or <code>event</code> if it already
     *         implements EventMessage.
     */
    public static EventMessage asEventMessage(Object event) {
        if (EventMessage.class.isInstance(event)) {
            return (EventMessage) event;
        }
        return new GenericEventMessage<Object>(event);
    }

    /**
     * Creates a GenericEventMessage with given <code>payload</code>, and an empty MetaData.
     *
     * @param payload The payload for the message
     * @see #asEventMessage(Object)
     */
    public GenericEventMessage(T payload) {
        this(payload, MetaData.emptyInstance());
    }

    /**
     * Creates a GenericEventMessage with given <code>payload</code> and given <code>metaData</code>.
     *
     * @param payload  The payload of the EventMessage
     * @param metaData The MetaData for the EventMessage
     * @see #asEventMessage(Object)
     */
    public GenericEventMessage(T payload, Map<String, Object> metaData) {
        super(IdentifierFactory.getInstance().generateIdentifier(), payload, metaData);
        this.timestamp = new DateTime();
    }

    /**
     * Constructor to reconstruct an EventMessage using existing data.
     *
     * @param identifier The identifier of the Message
     * @param timestamp  The timestamp of the Message creation
     * @param payload    The payload of the message
     * @param metaData   The meta data of the message
     */
    public GenericEventMessage(String identifier, DateTime timestamp, T payload, Map<String, Object> metaData) {
        super(identifier, payload, metaData);
        this.timestamp = timestamp;
    }

    /**
     * Copy constructor that allows creation of a new GenericEventMessage with modified metaData. All information
     * from the <code>original</code> is copied, except for the metaData.
     *
     * @param original The original message
     * @param metaData The MetaData for the new message
     */
    private GenericEventMessage(GenericEventMessage<T> original, Map<String, Object> metaData) {
        super(original.getIdentifier(), original.getPayload(), metaData);
        this.timestamp = original.getTimestamp();
    }

    @Override
    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public GenericEventMessage<T> withMetaData(Map<String, Object> newMetaDataValues) {
        if (getMetaData().equals(newMetaDataValues)) {
            return this;
        }
        return new GenericEventMessage<T>(this, newMetaDataValues);
    }

    @Override
    public GenericEventMessage<T> andMetaData(Map<String, Object> additionalMetaDataValues) {
        if (additionalMetaDataValues.isEmpty()) {
            return this;
        }
        return new GenericEventMessage<T>(this, getMetaData().mergedWith(additionalMetaDataValues));
    }

    @Override
    public String toString() {
        return String.format("GenericEventMessage[%s]", getPayload().toString());
    }
}
