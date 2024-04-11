package com.sapog87.visual_novel.app.service;

import com.sapog87.visual_novel.app.repository.StoryVariableRepository;
import com.sapog87.visual_novel.app.dto.VariableDto;
import com.sapog87.visual_novel.app.entity.Variable;
import org.springframework.stereotype.Service;

@Service
public class VariableService {
    private final StoryVariableRepository variableRepository;

    public VariableService(StoryVariableRepository variableRepository) {this.variableRepository = variableRepository;}

    public void change(VariableDto variableDto) {
        Variable variable = variableRepository.findByName(variableDto.getName());
        if (!variable.getPermanent()) {
            //TODO создать нормальное исключение
            throw new RuntimeException("Variable " + variableDto.getName() + " is not permanent");
        }
        if (variable == null) {
            //TODO создать VariableNotFoundException
            throw new RuntimeException("VariableNotFoundException");
        }

        variable.setValue(variableDto.getValue());
        variableRepository.save(variable);
    }

}
