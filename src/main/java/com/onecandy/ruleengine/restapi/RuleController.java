package com.onecandy.ruleengine.restapi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onecandy.ruleengine.databases.models.RuleNamespace;
import com.onecandy.ruleengine.databases.repositories.RuleNamespaceRepo;
import com.onecandy.ruleengine.engineConfiguration.RuleService;
import com.onecandy.ruleengine.utils.ClassLoaderUtil;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;
    
    @Autowired
    private RuleNamespaceRepo ruleNamesapceRepo;

    @PostMapping("/evaluate/{namespace}")
    public ResponseEntity<?> evaluateRules(@PathVariable String namespace, @RequestBody Map<String, Object> inputData) {
        RuleNamespace ns = ruleNamesapceRepo.findByNamespaceAndIsActive(namespace, true);
        if (ns == null) {
            return ResponseEntity.badRequest().body("Namespace not found or inactive");
        }
        Class<?> inputClass = ClassLoaderUtil.loadClass(ns.getInputClass());
        Object inputObject = ClassLoaderUtil.createInstance(inputClass);
        return ResponseEntity.ok(ruleService.processRules(namespace, inputObject));
    }
}