package com.sapog87.visual_novel.interpreter.processor;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.handler.DefaultStoryNodeHandler;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Processor<Result, Clazz> {
    private final Map<Class<? extends StoryNode>, StoryNodeHandler<Result>> handlers;

    @SuppressWarnings("unchecked")
    protected Processor(Map<String, StoryNodeHandler<Result>> handlerMap/*, Class<Clazz> clz*/) {
        Type superClass = this.getClass().getGenericSuperclass();
        Class<Clazz> clz = (Class<Clazz>) ((ParameterizedType) superClass).getActualTypeArguments()[1];
        this.handlers = handlerMap
                .values().stream()
                .filter(x -> clz.isAssignableFrom(x.getHandledClass()))
                .collect(Collectors.toMap(StoryNodeHandler::getHandledClass, handler -> handler));
    }

    public Result processNode(StoryNode node, User user, Story story, Data data) {
        StoryNodeHandler<Result> handler = handlers.getOrDefault(node.getClass(), new DefaultStoryNodeHandler<>());
        return handler.handle(node, user, story, data);
    }
}
