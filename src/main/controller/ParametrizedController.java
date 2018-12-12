package main.controller;

import java.util.HashMap;
/**
 *
 * Parameterized main.controller interface. If a main.controller is to be passed some value, it needs to implement this interface.
 * Any data to be passed is stored in a HashMap.
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
public interface ParametrizedController<A, B> {
    void setParameters(HashMap<A, B> map);
}
