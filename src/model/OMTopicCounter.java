package model;

import net.jini.core.entry.Entry;

public class OMTopicCounter implements Entry {
    public Integer numOfTopics;

    public OMTopicCounter() {
        //REQUIRED EMPTY CONSTRUCTOR
    }
    public OMTopicCounter(Integer num) {
        numOfTopics = num;
    }
}
