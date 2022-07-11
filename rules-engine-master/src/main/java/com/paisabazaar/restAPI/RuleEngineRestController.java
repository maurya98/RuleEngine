package com.paisabazaar.restAPI;

import com.google.common.base.Enums;
import com.paisabazaar.knowledgeBase.KnowledgeBaseService;
import com.paisabazaar.knowledgeBase.models.Rule;
import com.paisabazaar.ruleEngine.RuleEngine;
import com.paisabazaar.rulesImpl.insuranceRuleEngine.InsuranceDetails;
import com.paisabazaar.rulesImpl.insuranceRuleEngine.InsuranceInferenceEngine;
import com.paisabazaar.rulesImpl.insuranceRuleEngine.PolicyHolderDetails;
import com.paisabazaar.rulesImpl.loanRuleEngine.LoanDetails;
import com.paisabazaar.rulesImpl.loanRuleEngine.LoanInferenceEngine;
import com.paisabazaar.rulesImpl.loanRuleEngine.UserDetails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class RuleEngineRestController {
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    @Autowired
    private RuleEngine ruleEngine;
    @Autowired
    private LoanInferenceEngine loanInferenceEngine;
    @Autowired
    private InsuranceInferenceEngine insuranceInferenceEngine;

    @GetMapping(value = "/get-all-rules/{ruleNamespace}")
    public ResponseEntity<?> getRulesByNamespace(@PathVariable("ruleNamespace") String ruleNamespace) {
        RuleNamespace namespace = Enums.getIfPresent(RuleNamespace.class, ruleNamespace.toUpperCase()).or(RuleNamespace.DEFAULT);
        List<Rule> allRules = knowledgeBaseService.getAllRuleByNamespace(namespace.toString());
        return ResponseEntity.ok(allRules);
    }

    @GetMapping(value = "/get-all-rules")
    public ResponseEntity<?> getAllRules() {
        List<Rule> allRules = knowledgeBaseService.getAllRules();
        return ResponseEntity.ok(allRules);
    }

    @PostMapping(value = "/loan")
    public ResponseEntity<?> postUserLoanDetails(@RequestBody UserDetails userDetails) {
        List<LoanDetails> result = (List<LoanDetails>) ruleEngine.run(loanInferenceEngine, userDetails);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/insurance")
    public ResponseEntity<?> postCarLoanDetails(@RequestBody PolicyHolderDetails policyHolderDetails) {
        InsuranceDetails result = (InsuranceDetails) ruleEngine.run(insuranceInferenceEngine, policyHolderDetails);
        return ResponseEntity.ok(result);
    }
}
