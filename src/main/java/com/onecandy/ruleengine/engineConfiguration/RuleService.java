package com.onecandy.ruleengine.engineConfiguration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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
