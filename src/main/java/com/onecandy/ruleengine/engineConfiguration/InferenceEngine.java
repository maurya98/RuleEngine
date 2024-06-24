package com.onecandy.ruleengine.engineConfiguration;

import org.mvel2.MVEL;
import org.springframework.beans.factory.annotation.Autowired;

import com.onecandy.ruleengine.databases.models.RuleNamespace;
import com.onecandy.ruleengine.databases.models.Rules;
import com.onecandy.ruleengine.databases.repositories.RuleNamespaceRepo;
import com.onecandy.ruleengine.langParser.RuleParser;
import com.onecandy.ruleengine.utils.ClassLoaderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class InferenceEngine {

    @Autowired
    private RuleParser ruleParser;

    @Autowired
    private RuleNamespaceRepo ruleNamespaceRepo;

    public Object run(List<Rules> listOfRules, Object inputData, String ruleNamespace) {
        // STEP 1: MATCH
        List<Rules> conflictSet = match(listOfRules, inputData);

        // STEP 2: RESOLVE
        List<Rules> resolvedRule = resolve(conflictSet, inputData, ruleNamespace);
        if (resolvedRule == null) {
            return null;
        }

        // STEP 3: EXECUTE
        return executeRule(resolvedRule, inputData, ruleNamespace);
    }

    protected List<Rules> match(List<Rules> listOfRules, Object inputData) {
        return listOfRules.stream()
                .filter(rule -> ruleParser.parseCondition(rule.getConditions(), inputData))
                .collect(Collectors.toList());
    }

    protected List<Rules> resolve(List<Rules> conflictSet, Object inputData, String ruleNamespace) {
        return applyResolvingBusinessLogic(conflictSet, inputData , ruleNamespace);
    }

    protected Object executeRule(List<Rules> rules, Object inputData, String ruleNamespace) {
        RuleNamespace namespace = getNamespace(ruleNamespace);
        Class<?> outputClass = ClassLoaderUtil.loadClass(namespace.getOutputClass());
        Object outputResult = ClassLoaderUtil.createInstance(outputClass);
        return rules.stream().map(rule -> ruleParser.parseAction(rule.getActions(), inputData, outputResult)).collect(Collectors.toList()); 
    }

    protected List<Rules> applyResolvingBusinessLogic(List<Rules> conflictSet, Object inputData, String ruleNamespace) {
        RuleNamespace namespace = getNamespace(ruleNamespace);
        Class<?> outputClass = ClassLoaderUtil.loadClass(namespace.getOutputClass());
        Object outputResult = ClassLoaderUtil.createInstance(outputClass);

        RuleNamespace businessLogicOpt = ruleNamespaceRepo.findByNamespaceAndIsActive(ruleNamespace, true);
        if (businessLogicOpt.getResolvingScript() == null || businessLogicOpt.getResolvingScript() == "") {
            return conflictSet;
        }

        String script = businessLogicOpt.getResolvingScript();
        Map<String, Object> context = new HashMap<>();
        context.put("conflictSet", conflictSet);
        context.put("input", inputData);
        context.put("output", outputResult);
        context.put("ruleParser", ruleParser);

        return (List<Rules>) MVEL.eval(script, context);
    }

    protected abstract RuleNamespace getNamespace(String ruleNamespace);
}
