package model;

import net.jini.core.entry.Entry;

public class OMPost implements Entry {
    public String title;
    public Integer index;
    public String name;
    public String topicId;


    public OMPost() {
        //REQUIRED EMPTY CONSTRUCTOR
    }

    public OMPost(String title, int index, String name, String topicId) {
        this.title = title;
        this.index = index;
        this.name = name;
        this.topicId = topicId;
    }
}
