package com.onecandy.ruleengine.restapi;

import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onecandy.ruleengine.databases.models.RuleNamespace;
import com.onecandy.ruleengine.databases.repositories.RuleNamespaceRepo;
import com.onecandy.ruleengine.engineConfiguration.RuleService;
import com.onecandy.ruleengine.utils.ClassLoaderUtil;
import com.onecandy.ruleengine.utils.CustomResponse;
import com.onecandy.ruleengine.utils.DynamicClassGenerator;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleNamespaceRepo ruleNamesapceRepo;

    @SuppressWarnings("unchecked")
    @PostMapping("/evaluate/{namespace}")
    public CustomResponse<?> evaluateRules(@PathVariable String namespace, @RequestBody Map<String, Object> inputData) {
        try {
            System.out.println(namespace);
            RuleNamespace ns = ruleNamesapceRepo.findByNamespaceAndIsActive(namespace, true);
            if (ns == null) {
                return CustomResponse.error(400, namespace, "No namespace Found");
            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> fields;
            fields = mapper.readValue(ns.getInputFields(), Map.class);
            Class<?> inputClass = DynamicClassGenerator.generateClass(ns.getNamespace(), fields);
            Object inputObject = ClassLoaderUtil.createInstance(inputClass);
            BeanUtils.copyProperties(inputData, inputObject);
            Object result = ruleService.processRules(namespace, inputObject);
            if(result == null){
                return CustomResponse.error(400, namespace, "No rule Found");
            }
            return CustomResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}