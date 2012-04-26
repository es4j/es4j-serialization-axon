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

/**
 * Utility class (inspired by Springs Assert class) for doing assertions on parameters and object state. To remove the
 * need for explicit dependencies on Spring, the functionality of that class is migrated to this class.
 *
 * @author Allard Buijze
 * @since 0.3
 */
public abstract class Assert {

    private Assert() {
        // utility class
    }

    /**
     * Asserts that the value of <code>state</code> is true. If not, an IllegalStateException is thrown.
     *
     * @param state   the state validation expression
     * @param message The message that the excetion contains if state evaluates to false
     */
    public static void state(boolean state, String message) {
        if (!state) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Asserts that the given <code>expression</code> is true. If not, an IllegalArgumentException is thrown.
     *
     * @param expression the state validation expression
     * @param message    The message that the exception contains if state evaluates to false
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Asserts that the given <code>expression</code> is false. If not, an IllegalArgumentException is thrown.
     *
     * @param expression the state validation expression
     * @param message    The message that the exception contains if state evaluates to true
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given <code>value</code> is not <code>null</code>. If not, an IllegalArgumentException is
     * thrown.
     *
     * @param value   the value not to be <code>null</code>
     * @param message The message to add to the exception when the assertion fails
     */
    public static void notNull(Object value, String message) {
        isTrue(value != null, message);
    }
}
