package main.model;

import net.jini.core.entry.Entry;

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
