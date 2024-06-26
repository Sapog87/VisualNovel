package com.sapog87.visual_novel.core.story.nodes.logical;

public final class DelayStoryNode extends LogicalStoryNode {
    @Override
    public void validate() {
        if (getNext().isEmpty() || getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and outputs");
        }
        if (getNext().get(0).size() != 1 || getPrev().get(0).isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and 1 output");
        }
        if (!getData().containsKey("time")) {
            throw new IllegalArgumentException("node must have {time} field");
        }
        if (getData().get("time").isBlank()) {
            throw new IllegalArgumentException("{time} field in node must not be empty");
        }
        super.validate();

        try {
            double time = Double.parseDouble(getData().get("time"));
            if (time < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("{time} field in node must be positive number");
        }
    }
}
