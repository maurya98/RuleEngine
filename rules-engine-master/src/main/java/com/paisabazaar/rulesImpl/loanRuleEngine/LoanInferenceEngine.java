package com.paisabazaar.rulesImpl.loanRuleEngine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.paisabazaar.restAPI.RuleNamespace;
import com.paisabazaar.ruleEngine.InferenceEngine;

@Slf4j
@Service
public class LoanInferenceEngine extends InferenceEngine<UserDetails, LoanDetails> {

    @Override
    protected RuleNamespace getRuleNamespace() {
        return RuleNamespace.LOAN;
    }

    @Override
    protected LoanDetails initializeOutputResult() {
        return LoanDetails.builder().build();
    }
}