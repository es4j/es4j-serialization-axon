package org.es4j.serialization.core.axon;

import java.util.Collections;
import java.util.Map;
import org.es4j.messaging.api.axon.EventMessage;
import org.es4j.messaging.api.axon.MetaData;
import org.es4j.serialization.api.axon.SerializedObject;
import org.es4j.serialization.api.axon.SerializedType;
import org.es4j.serialization.api.axon.Serializer;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Allard Buijze
 */
public class SerializedEventMessageTest {

    private SerializedObject<String> serializedPayload = new SimpleSerializedObject<String>("serialized",
                                                                                            String.class,
                                                                                            "java.lang.Object",
                                                                                            "1");
    private SerializedObject<String> serializedMetaData = new SerializedMetaData<String>("serialized",
                                                                                         String.class);

    private Object deserializedPayload = new Object();
    private MetaData deserializedMetaData = MetaData.emptyInstance();
    private Serializer serializer = mock(Serializer.class);
    private String eventId = "eventId";
    private DateTime timestamp = new DateTime();

    @Before
    public void setUp() {
        when(serializer.deserialize(serializedMetaData)).thenReturn(deserializedMetaData);
        when(serializer.deserialize(serializedPayload)).thenReturn(deserializedPayload);
        when(serializer.classForType(isA(SerializedType.class))).thenReturn(Object.class);
    }

    @Test
    public void testConstructor() {
        SerializedEventMessage<Object> message1 = new SerializedEventMessage<Object>(eventId, timestamp,
                                                                                     serializedPayload,
                                                                                     serializedMetaData, serializer);

        assertSame(MetaData.emptyInstance(), message1.getMetaData());
        assertEquals(Object.class, message1.getPayloadType());
        assertFalse(message1.isPayloadDeserialized());
        assertEquals(Object.class, message1.getPayload().getClass());
        assertTrue(message1.isPayloadDeserialized());
    }

    @Test
    public void testWithMetaData() {
        Map<String, Object> metaDataMap = Collections.singletonMap("key", (Object) "value");
        MetaData metaData = MetaData.from(metaDataMap);
        when(serializer.deserialize(serializedMetaData)).thenReturn(metaData);
        SerializedEventMessage<Object> message = new SerializedEventMessage<Object>(eventId, timestamp,
                                                                                    serializedPayload,
                                                                                    serializedMetaData, serializer);
        EventMessage<Object> message1 = message.withMetaData(MetaData.emptyInstance());
        EventMessage<Object> message2 = message.withMetaData(
                MetaData.from(Collections.singletonMap("key", (Object) "otherValue")));

        assertEquals(0, message1.getMetaData().size());
        assertEquals(1, message2.getMetaData().size());
    }

    @Test
    public void testAndMetaData() {
        Map<String, Object> metaDataMap = Collections.singletonMap("key", (Object) "value");
        MetaData metaData = MetaData.from(metaDataMap);
        when(serializer.deserialize(serializedMetaData)).thenReturn(metaData);
        EventMessage<Object> message = new SerializedEventMessage<Object>(eventId, timestamp, serializedPayload,
                                                                          serializedMetaData, serializer);
        EventMessage<Object> message1 = message.andMetaData(MetaData.emptyInstance());
        EventMessage<Object> message2 = message.andMetaData(
                MetaData.from(Collections.singletonMap("key", (Object) "otherValue")));

        assertEquals(1, message1.getMetaData().size());
        assertEquals("value", message1.getMetaData().get("key"));
        assertEquals(1, message2.getMetaData().size());
        assertEquals("otherValue", message2.getMetaData().get("key"));
    }
}
