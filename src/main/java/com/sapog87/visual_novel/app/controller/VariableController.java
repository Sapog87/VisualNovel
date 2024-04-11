package com.sapog87.visual_novel.app.controller;

import com.sapog87.visual_novel.app.dto.VariableDto;
import com.sapog87.visual_novel.app.service.VariableService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VariableController {

    private final VariableService variableService;

    public VariableController(VariableService variableService) {this.variableService = variableService;}

    @PostMapping("/change")
    public @ResponseBody String changeVariable(@RequestBody VariableDto variable) {
        variableService.change(variable);
        //TODO дописать код по изменению переменных извне
        return null;
    }
}
