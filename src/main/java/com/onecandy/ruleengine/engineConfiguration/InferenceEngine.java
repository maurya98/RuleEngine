package com.onecandy.ruleengine.engineConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onecandy.ruleengine.databases.models.RuleNamespace;
import com.onecandy.ruleengine.databases.models.Rules;
import com.onecandy.ruleengine.databases.repositories.RuleNamespaceRepo;
import com.onecandy.ruleengine.langParser.RuleParser;
import com.onecandy.ruleengine.utils.ClassLoaderUtil;
import com.onecandy.ruleengine.utils.DynamicClassGenerator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class InferenceEngine {

    private final RuleParser ruleParser;
    private final RuleNamespaceRepo ruleNamespaceRepo;
    private final ObjectMapper objectMapper;

    @Autowired
    public InferenceEngine(RuleParser ruleParser, RuleNamespaceRepo ruleNamespaceRepo) {
        this.ruleParser = ruleParser;
        this.ruleNamespaceRepo = ruleNamespaceRepo;
        this.objectMapper = new ObjectMapper();
    }

    public Object run(List<Rules> listOfRules, Object inputData, String ruleNamespace) throws Exception {
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

    protected List<Rules> resolve(List<Rules> conflictSet, Object inputData, String ruleNamespace) throws Exception {
        Future<List<Rules>> future = applyResolvingBusinessLogicAsync(conflictSet, inputData, ruleNamespace);
        return future.get(); // wait for the async result
    }

    @Async
    protected CompletableFuture<List<Rules>> applyResolvingBusinessLogicAsync(List<Rules> conflictSet, Object inputData, String ruleNamespace) throws Exception {
        return CompletableFuture.completedFuture(applyResolvingBusinessLogic(conflictSet, inputData, ruleNamespace));
    }

    protected List<Rules> applyResolvingBusinessLogic(List<Rules> conflictSet, Object inputData, String ruleNamespace) throws Exception {
        RuleNamespace namespace = getNamespace(ruleNamespace);
        Map<String, String> fields = objectMapper.readValue(namespace.getOutputFields(), Map.class);
        Class<?> outputClass = DynamicClassGenerator.generateClass(namespace.getNamespace(), fields);
        Object outputResult = ClassLoaderUtil.createInstance(outputClass);

        RuleNamespace businessLogicOpt = ruleNamespaceRepo.findByNamespaceAndIsActive(ruleNamespace, true);
        if (businessLogicOpt.getResolvingScript() == null || businessLogicOpt.getResolvingScript().isEmpty()) {
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

    protected Object executeRule(List<Rules> rules, Object inputData, String ruleNamespace) throws Exception {
        RuleNamespace namespace = getNamespace(ruleNamespace);
        Map<String, String> fields = objectMapper.readValue(namespace.getOutputFields(), Map.class);
        Class<?> outputClass = DynamicClassGenerator.generateClass(namespace.getNamespace(), fields);
        Object outputResult = ClassLoaderUtil.createInstance(outputClass);
        return rules.stream()
                .map(rule -> ruleParser.parseAction(rule.getActions(), inputData, outputResult))
                .collect(Collectors.toList());
    }

    protected abstract RuleNamespace getNamespace(String ruleNamespace);
}