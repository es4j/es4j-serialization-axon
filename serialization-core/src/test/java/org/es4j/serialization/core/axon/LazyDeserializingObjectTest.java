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

package org.es4j.serialization.core.axon;

import org.es4j.serialization.api.axon.SerializedObject;
import org.es4j.serialization.api.axon.SerializedType;
import org.es4j.serialization.api.axon.Serializer;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 * @author Allard Buijze
 */
public class LazyDeserializingObjectTest {

    private Serializer mockSerializer;

    private SerializedType mockType;
    private SerializedObject mockObject;

    private String mockDeserializedObject = "I'm a mock";

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        mockSerializer = mock(Serializer.class);
        mockType = mock(SerializedType.class);
        mockObject = new SimpleSerializedObject(mockDeserializedObject, String.class, mockType);

        when(mockSerializer.classForType(mockType)).thenReturn(String.class);
        when(mockSerializer.deserialize(mockObject)).thenReturn(mockDeserializedObject);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLazilyDeserialized() {
        LazyDeserializingObject<Object> testSubject = new LazyDeserializingObject<Object>(mockObject, mockSerializer);
        verify(mockSerializer, never()).deserialize(Mockito.any(SerializedObject.class));
        assertEquals(String.class, testSubject.getType());
        assertFalse(testSubject.isDeserialized());
        verify(mockSerializer, never()).deserialize(Mockito.any(SerializedObject.class));
        assertSame(mockDeserializedObject, testSubject.getObject());
        assertTrue(testSubject.isDeserialized());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLazilyDeserialized_NullObject() {
        new LazyDeserializingObject<Object>(null, mockSerializer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLazilyDeserialized_NullSerializer() {
        new LazyDeserializingObject<Object>(mockObject, null);
    }

    @Test
    public void testWithProvidedDeserializedInstance() {
        LazyDeserializingObject<Object> testSubject = new LazyDeserializingObject<Object>(mockDeserializedObject);
        assertEquals(mockDeserializedObject.getClass(), testSubject.getType());
        assertSame(mockDeserializedObject, testSubject.getObject());
        assertTrue(testSubject.isDeserialized());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithProvidedDeserializedNullInstance() {
        new LazyDeserializingObject<Object>(null);
    }
}
