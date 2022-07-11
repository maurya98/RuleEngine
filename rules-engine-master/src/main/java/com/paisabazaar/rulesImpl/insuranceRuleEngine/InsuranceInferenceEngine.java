package com.paisabazaar.rulesImpl.insuranceRuleEngine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.paisabazaar.restAPI.RuleNamespace;
import com.paisabazaar.ruleEngine.InferenceEngine;

@Slf4j
@Service
public class InsuranceInferenceEngine extends InferenceEngine<PolicyHolderDetails, InsuranceDetails> {

    @Override
    protected RuleNamespace getRuleNamespace() {
        return RuleNamespace.INSURANCE;
    }

    @Override
    protected InsuranceDetails initializeOutputResult() {
        return InsuranceDetails.builder().build();
    }
}
