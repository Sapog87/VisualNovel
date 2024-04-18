package com.sapog87.visual_novel.core.json;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class Output implements Serializable {
    private List<Connection> connections;

    @Override
    public String toString() {
        return "Output{" +
                "connections=" + connections +
                '}';
    }
}
