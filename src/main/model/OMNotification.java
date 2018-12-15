package main.model;

import net.jini.core.entry.Entry;

/**
 *
 * OMNotification JavaSpace entry. Used to store notification information. Implements {@link Entry} to be retrieved
 * from {@link net.jini.space.JavaSpace}
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Entry
 */
public class OMNotification implements Entry {
    public String topicId;
    public String topicName;
    public String userId;
    public OMComment comment;
    public Boolean delete;
    public Integer index;

    public OMNotification() {

    }
}
