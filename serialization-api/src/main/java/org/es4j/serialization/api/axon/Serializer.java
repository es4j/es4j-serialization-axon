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

package org.es4j.serialization.api.axon;

/**
 * Interface describing a serialization mechanism. Implementations can serialize objects of given type <code>T</code>
 * to an output stream and read the object back in from an input stream.
 *
 * @author Allard Buijze
 * @since 1.2
 */
public interface Serializer {

    /**
     * Serialize the given <code>object</code> into a Serialized Object containing the given
     * <code>expectedRepresentation</code>.
     * <p/>
     * Use {@link #canSerializeTo(Class)} to detect whether the <code>expectedRepresentation</code> is supported by
     * this serializer.
     *
     * @param object                 The object to serialize
     * @param expectedRepresentation The expected data type representing the serialized object
     * @param <T>                    The expected data type representing the serialized object
     * @return the instance representing the serialized object.
     */
    <T> SerializedObject<T> serialize(Object object, Class<T> expectedRepresentation);

    /**
     * Indicates whether this Serializer is capable of serializing to the given <code>expectedRepresentation</code>.
     * <p/>
     * When <code>true</code>, this does *not* guarantee that the serialization and (optional) conversion will also
     * succeed when executed. For example, when a serializer produces a <code>byte[]</code> containing JSON, trying to
     * convert to a Dom4J Document will fail, even though this serializer has a converter to convert
     * <code>byte[]</code>
     * to Dom4J instances.
     *
     * @param expectedRepresentation The type of data a Serialized Object should contain
     * @param <T>                    The type of data a Serialized Object should contain
     * @return <code>true</code> if the <code>expectedRepresentation</code> is supported, otherwise <code>false</code>.
     */
    <T> boolean canSerializeTo(Class<T> expectedRepresentation);

    /**
     * Deserializes the first object read from the given <code>bytes</code>. The <code>bytes</code> are not consumed
     * from the array or modified in any way. The resulting object instance is cast to the expected type.
     *
     * @param serializedObject the instance describing the type of object and the bytes providing the serialized data
     * @param <T>              The data type of the serialized object
     * @return the serialized object, cast to the expected type
     *
     * @throws ClassCastException if the first object in the stream is not an instance of &lt;T&gt;.
     */
    <T> Object deserialize(SerializedObject<T> serializedObject);

    /**
     * Returns the class for the given type identifier. The result of this method must guarantee that the deserialized
     * SerializedObject with the given <code>type</code> is an instance of the returned Class.
     *
     * @param type The type identifier of the object
     * @return the Class representing the type of the serialized Object.
     */
    Class classForType(SerializedType type);
}
