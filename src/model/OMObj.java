package model;

import net.jini.core.entry.Entry;

public class OMObj implements Entry {
    public String contents;

    public OMObj() {
        //REQURIED NO ARG CONSTRUCTOR
    }

    public OMObj(String contents) {
        this.contents = contents;
    }
}
