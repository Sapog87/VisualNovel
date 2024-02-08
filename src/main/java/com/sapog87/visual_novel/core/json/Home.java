package com.sapog87.visual_novel.core.json;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Home {
    private Map<String, Node> data;

    @Override
    public String toString() {
        return "Home{" +
                "data=" + data +
                '}';
    }
}
