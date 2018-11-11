package controller;

import java.util.HashMap;

public interface ParametrizedController<A, B> {
    void setParamters(HashMap<A, B> map);
}
