package com.onecandy.ruleengine.engineConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mvel2.MVEL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onecandy.ruleengine.databases.models.RuleNamespace;
import com.onecandy.ruleengine.databases.models.Rules;

import com.onecandy.ruleengine.databases.repositories.RuleNamespaceRepo;
import com.onecandy.ruleengine.databases.repositories.RuleRepo;
import com.onecandy.ruleengine.langParser.RuleParser;
import com.onecandy.ruleengine.utils.ClassLoaderUtil;

@Service
@SuppressWarnings("rawtypes")
public class RuleService extends InferenceEngine {

    @Autowired
    private RuleRepo ruleRepository;
    
    @Autowired
    private RuleNamespaceRepo ruleNamespaceRepo;

    @Autowired
    private RuleParser ruleParser;

    public Object processRules(String namespaceName, Object inputData) {
        List<Rules> rules = ruleRepository.findByRuleNamespaceAndIsActive(namespaceName, true);
        Object executionResult = run(rules, inputData, namespaceName);
        return applyAfterExecutionBusinessLogic(executionResult, inputData, namespaceName);
    }

        protected Object applyAfterExecutionBusinessLogic(Object conflictSet, Object inputData, String ruleNamespace) {
        RuleNamespace namespace = getNamespace(ruleNamespace);
        Class<?> outputClass = ClassLoaderUtil.loadClass(namespace.getOutputClass());
        Object outputResult = ClassLoaderUtil.createInstance(outputClass);

        RuleNamespace businessLogicOpt = ruleNamespaceRepo.findByNamespaceAndIsActive(ruleNamespace, true);
        if (businessLogicOpt.getResolvingScript() == null || businessLogicOpt.getResolvingScript() == "") {
            return conflictSet;
        }

        String script = businessLogicOpt.getPostExecutionScript();
        Map<String, Object> context = new HashMap<>();
        context.put("conflictSet", conflictSet);
        context.put("input", inputData);
        context.put("output", outputResult);
        context.put("ruleParser", ruleParser);

        return MVEL.eval(script, context);
    }

    protected RuleNamespace getNamespace(String ruleNamespace) {
        return ruleNamespaceRepo.findByNamespaceAndIsActive(ruleNamespace, true);
    }
}

