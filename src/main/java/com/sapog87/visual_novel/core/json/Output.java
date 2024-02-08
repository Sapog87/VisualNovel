package com.sapog87.visual_novel.core.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Output {
    private List<Connection> connections;

    @Override
    public String toString() {
        return "Output{" +
                "connections=" + connections +
                '}';
    }
}
