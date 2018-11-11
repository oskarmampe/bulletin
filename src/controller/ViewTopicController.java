package controller;

import model.OMTopic;

import java.util.HashMap;

public class ViewTopicController implements ParametrizedController<String, OMTopic> {
    @Override
    public void setParamters(HashMap<String, OMTopic> map) {
        System.out.println(map.get("topic").title + map.get("topic").owner);
    }
}
