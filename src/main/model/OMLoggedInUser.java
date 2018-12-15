package main.model;

import net.jini.core.entry.Entry;

/**
 *
 * OMLoggedInUser JavaSpace entry. Used to store logged in information. Implements {@link Entry} to be retrieved
 * from {@link net.jini.space.JavaSpace}
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Entry
 */
public class OMLoggedInUser implements Entry {
    public String userId;

    public OMLoggedInUser() {

    }
}
