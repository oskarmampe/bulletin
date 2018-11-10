package model;

import net.jini.core.entry.Entry;

public class OMUser implements Entry {
    public String userid;
    public String password;

    public OMUser() {
        //REQUIRED EMPTY CONSTRUCTOR
    }

    public OMUser(String userid, String password) {
        this.userid = userid;
        this.password = password;
    }
}
