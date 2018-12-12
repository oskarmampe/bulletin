package main.model;

import net.jini.core.entry.Entry;

public class OMComment implements Entry {
    public String content;
    public Integer index;
    public String owner;
    public Boolean privateMessage;
    public String topicId;

    public OMComment() {
        //REQUIRED EMPTY CONSTRUCTOR
    }

    public OMComment(String content, boolean privateMessage, int index, String owner, String topicId) {
        this.content = content;
        this.privateMessage = privateMessage;
        this.index = index;
        this.owner = owner;
        this.topicId = topicId;
    }
}
