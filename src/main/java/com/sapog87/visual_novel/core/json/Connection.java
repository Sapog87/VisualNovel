package com.sapog87.visual_novel.core.json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Connection {
    private String node;
    private String input;
    private String output;

    @Override
    public String toString() {
        return "Connection{" +
                "node='" + node + '\'' +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}
