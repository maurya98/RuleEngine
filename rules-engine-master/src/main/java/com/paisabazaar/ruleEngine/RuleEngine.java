package com.paisabazaar.ruleEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paisabazaar.knowledgeBase.KnowledgeBaseService;
import com.paisabazaar.knowledgeBase.models.Rule;

import java.util.List;


@Service
public class RuleEngine {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    public Object run(InferenceEngine inferenceEngine, Object inputData) {
        String ruleNamespace = inferenceEngine.getRuleNamespace().toString();
        //TODO: Here for each call, we are fetching all rules from db. It should be cache.
        List<Rule> allRulesByNamespace = knowledgeBaseService.getAllRuleByNamespace(ruleNamespace);
//        for (Rule rule : allRulesByNamespace) {
//			System.out.println(rule.getRuleId()+ " " +rule.getConditions()+" "+ rule.getActions());
//		}
        Object result = inferenceEngine.run(allRulesByNamespace, inputData);
        return result;
    }

}
