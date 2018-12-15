package main.model;

import net.jini.core.entry.Entry;

/**
 *
 * OMTopic JavaSpace entry. Used to store topic information. Implements {@link Entry} to be retrieved
 * from {@link net.jini.space.JavaSpace}
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Entry
 */
public class OMTopic implements Entry {
    public String id;
    public Integer index;
    public String owner;
    public String title;

    public OMTopic() {
        //REQURIED NO ARG CONSTRUCTOR
    }

    public OMTopic(String id, int index, String owner, String title) {
        this.id = id;
        this.index = index;
        this.owner = owner;
        this.title = title;
    }
}
