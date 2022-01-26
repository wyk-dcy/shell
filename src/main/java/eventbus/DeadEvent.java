/*
 * Copyright (C) 2007 The Guava Authors
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package eventbus;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Wraps an event that was posted, but which had no subscribers and thus could not be delivered.
 *
 * @author wuyongkang
 */
public class DeadEvent {

    private final Object source;
    private final Object event;

    /**
     * Creates a new DeadEvent.
     *
     * @param source object broadcasting the DeadEvent (generally the {@link EventBusImpl}).
     * @param event  the event that could not be delivered.
     */
    public DeadEvent(Object source, Object event) {
        this.source = Preconditions.checkNotNull(source);
        this.event = Preconditions.checkNotNull(event);
    }

    /**
     * Returns the object that originated this event (<em>not</em> the object that originated the
     * wrapped event). This is generally an {@link EventBusImpl}.
     *
     * @return the source of this event.
     */
    public Object getSource() {
        return source;
    }

    /**
     * Returns the wrapped, 'dead' event, which the system was unable to deliver to any registered
     * subscriber.
     *
     * @return the 'dead' event that could not be delivered.
     */
    public Object getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("source", source).add("event", event).toString();
    }
}