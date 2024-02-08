package com.sapog87.visual_novel.core.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Node {
    private int id;
    private String name;
    private Map<String, String> data;
    @JsonProperty("class")
    private String myClass;
    private String html;
    private boolean typenode;
    private Map<String, Input> inputs;
    private Map<String, Output> outputs;
    private double pos_x;
    private double pos_y;

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", data=" + data +
                ", myClass='" + myClass + '\'' +
                ", html='" + html + '\'' +
                ", typenode=" + typenode +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                ", pos_x=" + pos_x +
                ", pos_y=" + pos_y +
                '}';
    }
}
