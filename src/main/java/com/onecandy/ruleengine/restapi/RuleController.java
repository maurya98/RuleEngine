package com.onecandy.ruleengine.restapi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onecandy.ruleengine.service.RuleEvaluationService;
import com.onecandy.ruleengine.utils.CustomResponse;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleEvaluationService ruleEvaluationService;

    @PostMapping("/evaluate/{namespace}")
    public CustomResponse<?> evaluateRules(@PathVariable String namespace, @RequestBody Map<String, Object> inputData) {
        return ruleEvaluationService.evaluateRules(namespace, inputData);
    }
}