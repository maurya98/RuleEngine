package com.onecandy.ruleengine.service;

import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onecandy.ruleengine.core.RuleService;
import com.onecandy.ruleengine.databases.models.RuleNamespace;
import com.onecandy.ruleengine.databases.repositories.RuleNamespaceRepo;
import com.onecandy.ruleengine.utils.ClassLoaderUtil;
import com.onecandy.ruleengine.utils.CustomResponse;
import com.onecandy.ruleengine.utils.DynamicClassGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RuleEvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(RuleEvaluationService.class);

    private final RuleService ruleService;
    private final RuleNamespaceRepo ruleNamespaceRepo;
    private final ObjectMapper objectMapper;

    public RuleEvaluationService(RuleService ruleService, RuleNamespaceRepo ruleNamespaceRepo, ObjectMapper objectMapper) {
        this.ruleService = ruleService;
        this.ruleNamespaceRepo = ruleNamespaceRepo;
        this.objectMapper = objectMapper;
    }

    public CustomResponse<Object> evaluateRules(String namespace, Map<String, Object> inputData) {
        try {
            RuleNamespace ns = ruleNamespaceRepo.findByNamespaceAndIsActive(namespace, true);
            if (ns == null) {
                return CustomResponse.error(400, namespace, "No namespace Found");
            }
            Map<String, String> fields = parseInputFields(ns.getInputFields());
            Class<?> inputClass = DynamicClassGenerator.generateClass(ns.getNamespace(), fields);
            Object inputObject = ClassLoaderUtil.createInstance(inputClass);
            BeanUtils.copyProperties(inputData, inputObject);

            Object result = ruleService.processRules(namespace, inputObject);
            if (result == null) {
                return CustomResponse.error(400, namespace, "No rule Found");
            }

            return CustomResponse.success(result);
        } catch (Exception e) {
            logger.error("Error evaluating rules for namespace: {}", namespace, e);
            return CustomResponse.error(406, "FAILURE", e.getMessage());
        }
    }

    private Map<String, String> parseInputFields(String inputFields) throws JsonProcessingException {
        return objectMapper.readValue(inputFields, objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
    }
}