package com.sapog87.visual_novel.app.service;

import com.sapog87.visual_novel.app.dto.VariableDto;
import com.sapog87.visual_novel.app.entity.Variable;
import com.sapog87.visual_novel.app.exception.VariableIllegalStateException;
import com.sapog87.visual_novel.app.exception.VariableNotFoundException;
import com.sapog87.visual_novel.app.repository.VariableRepository;
import org.springframework.stereotype.Service;

@Service
public class VariableService {
    private final VariableRepository variableRepository;

    public VariableService(VariableRepository variableRepository) {this.variableRepository = variableRepository;}

    public Boolean change(String uid, VariableDto variableDto) {
        Variable variable = variableRepository.findByName(variableDto.getName());

        if (variable == null)
            throw new VariableNotFoundException("Variable with name " + variableDto.getName() + " not found");
        if (!variable.getPermanent())
            throw new VariableIllegalStateException("Variable " + variableDto.getName() + " is not permanent");

        variable.setValue(variableDto.getValue());
        variableRepository.save(variable);
        return true;
    }

}
