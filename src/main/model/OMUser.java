package main.model;

import net.jini.core.entry.Entry;

/**
 *
 * OMUser JavaSpace entry. Used to store user information. Implements {@link Entry} to be retrieved
 * from {@link net.jini.space.JavaSpace}
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Entry
 */
public class OMUser implements Entry {
    public String userid;
    public String password;
    public String image;
    public byte[] salt;

    public OMUser() {
        //REQUIRED EMPTY CONSTRUCTOR
    }

    public OMUser(String userid, String password, String image) {
        this.userid = userid;
        this.password = password;
        this.image = image;
    }
}
