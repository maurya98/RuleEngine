package com.onecandy.ruleengine.engineConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.onecandy.ruleengine.databases.models.RuleNamespace;
import com.onecandy.ruleengine.databases.models.Rules;
import com.onecandy.ruleengine.databases.repositories.RuleNamespaceRepo;
import com.onecandy.ruleengine.langParser.RuleParser;
import com.onecandy.ruleengine.utils.ClassLoaderUtil;

import java.util.List;
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
        return applyResolvingBusinessLogic(conflictSet, inputData, ruleNamespace);
    }

    protected Object executeRule(List<Rules> rules, Object inputData, String ruleNamespace) {
        RuleNamespace namespace = getNamespace(ruleNamespace);
        Class<?> outputClass = ClassLoaderUtil.loadClass(namespace.getOutputClass());
        Object outputResult = ClassLoaderUtil.createInstance(outputClass);
        return rules.stream().map(rule -> ruleParser.parseAction(rule.getActions(), inputData, outputResult))
                .collect(Collectors.toList());
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
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(script);

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("conflictSet", conflictSet);
        context.setVariable("input", inputData);
        context.setVariable("output", outputResult);
        context.setVariable("ruleParser", ruleParser);

        return (List<Rules>) expression.getValue(context);
    }

    protected abstract RuleNamespace getNamespace(String ruleNamespace);
}
