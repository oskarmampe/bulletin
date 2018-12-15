package main.model;

import net.jini.core.entry.Entry;

/**
 *
 * OMTopicCounter JavaSpace entry. Used to store topic counter information. Implements {@link Entry} to be retrieved
 * from {@link net.jini.space.JavaSpace}
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Entry
 */
public class OMTopicCounter implements Entry {
    public Integer numOfTopics;

    public OMTopicCounter() {
        //REQUIRED EMPTY CONSTRUCTOR
    }
    public OMTopicCounter(Integer num) {
        numOfTopics = num;
    }
}
