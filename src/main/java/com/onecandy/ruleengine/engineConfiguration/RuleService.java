package com.onecandy.ruleengine.engineConfiguration;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onecandy.ruleengine.databases.models.RuleNamespace;
import com.onecandy.ruleengine.databases.models.Rules;

import com.onecandy.ruleengine.databases.repositories.RuleNamespaceRepo;
import com.onecandy.ruleengine.databases.repositories.RuleRepo;
import com.onecandy.ruleengine.langParser.RuleParser;
import com.onecandy.ruleengine.utils.ClassLoaderUtil;
import com.onecandy.ruleengine.utils.DynamicClassGenerator;

@Service
@SuppressWarnings("rawtypes")
public class RuleService extends InferenceEngine {

    @Autowired
    private RuleRepo ruleRepository;

    @Autowired
    private RuleNamespaceRepo ruleNamespaceRepo;

    @Autowired
    private RuleParser ruleParser;

    public Object processRules(String namespaceName, Object inputData) throws Exception {
        List<Rules> rules = ruleRepository.findByRuleNamespaceAndIsActive(namespaceName, true);
        if(rules.isEmpty()){
            return null;
        }
        Object executionResult = run(rules, inputData, namespaceName);
        return applyAfterExecutionBusinessLogic(executionResult, inputData, namespaceName);
    }

    @SuppressWarnings("unchecked")
    protected Object applyAfterExecutionBusinessLogic(Object conflictSet, Object inputData, String ruleNamespace) throws Exception {
        RuleNamespace namespace = getNamespace(ruleNamespace);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> fields;
        fields = mapper.readValue(namespace.getOutputFields(), Map.class);
        Class<?> outputClass = DynamicClassGenerator.generateClass(namespace.getNamespace(), fields);
        Object outputResult = ClassLoaderUtil.createInstance(outputClass);

        RuleNamespace businessLogicOpt = ruleNamespaceRepo.findByNamespaceAndIsActive(ruleNamespace, true);
        if (businessLogicOpt.getResolvingScript() == null || businessLogicOpt.getResolvingScript() == "") {
            return conflictSet;
        }

        String script = businessLogicOpt.getPostExecutionScript();
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(script);

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("conflictSet", conflictSet);
        context.setVariable("input", inputData);
        context.setVariable("output", outputResult);
        context.setVariable("ruleParser", ruleParser);

        return expression.getValue(context);
    }

    protected RuleNamespace getNamespace(String ruleNamespace) {
        return ruleNamespaceRepo.findByNamespaceAndIsActive(ruleNamespace, true);
    }
}
