package com.sapog87.visual_novel.core.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Drawflow implements Serializable {
    @JsonProperty("Home")
    private Home home;

    @Override
    public String toString() {
        return "Drawflow{" +
                "home=" + home +
                '}';
    }
}
