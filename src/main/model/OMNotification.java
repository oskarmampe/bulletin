package main.model;

import net.jini.core.entry.Entry;

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
