package org.es4j.serialization.core.axon;

import java.lang.annotation.*;

/**
 * Annotation that attaches revision information to a Serializable object. The revision identifiers is used by
 * upcasters to decide whether they need to process a certain serialized event. Generally, the revision identifier
 * needs to be modified (increased) each time the structure of an event has been changed in an incompatible manner.
 * <p/>
 * Although revision identifiers are inherited, you are strictly advised to only annotate the actual implementation
 * classes used. This will make it easier to keep the necessary upcasters up-to-date.
 *
 * @author Allard Buijze
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Revision {

    /**
     * The revision identifier for this object.
     */
    String value();
}
