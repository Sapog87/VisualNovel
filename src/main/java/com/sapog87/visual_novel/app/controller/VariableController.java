package com.sapog87.visual_novel.app.controller;

import com.sapog87.visual_novel.app.dto.VariableDto;
import com.sapog87.visual_novel.app.service.VariableService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class VariableController {

    private final VariableService variableService;

    public VariableController(VariableService variableService) {this.variableService = variableService;}

    @PostMapping("/variable")
    public @ResponseBody BooleanWrapper changeVariable(@RequestParam String uid, @RequestBody VariableDto variable) {
        return new BooleanWrapper(variableService.change(uid, variable));
    }

    @GetMapping("/variable")
    public @ResponseBody VariableDto getVariable(@RequestParam String uid) {
        return null;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class BooleanWrapper {
        private boolean value;
    }
}
