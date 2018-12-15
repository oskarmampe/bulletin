package main.model;

import net.jini.core.entry.Entry;

/**
 *
 * OMNotificationRegister JavaSpace entry. Used to store notification register information.
 * Implements {@link Entry} to be retrieved from {@link net.jini.space.JavaSpace}
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Entry
 */
public class OMNotificationRegister implements Entry {
    public String topicId;
    public String userId;


    public OMNotificationRegister() {

    }
}
