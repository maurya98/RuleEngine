package com.paisabazaar.knowledgeBase;

import com.google.common.base.Enums;
import com.paisabazaar.knowledgeBase.db.RuleDbModel;
import com.paisabazaar.knowledgeBase.db.RulesRepository;
import com.paisabazaar.knowledgeBase.models.Rule;
import com.paisabazaar.restAPI.RuleNamespace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseService {
    @Autowired
    private RulesRepository rulesRepository;

    public List<Rule> getAllRules(){
        return rulesRepository.findAll().stream()
                .map(
                        ruleDbModel -> mapFromDbModel(ruleDbModel)
                )
                .collect(Collectors.toList());
    }

    public List<Rule> getAllRuleByNamespace(String ruleNamespace){
        return rulesRepository.findByRuleNamespace(ruleNamespace).stream()
                .map(
                        ruleDbModel -> mapFromDbModel(ruleDbModel)
                )
                .collect(Collectors.toList());
    }

    private Rule mapFromDbModel(RuleDbModel ruleDbModel){
        RuleNamespace namespace = Enums.getIfPresent(RuleNamespace.class, ruleDbModel.getRuleNamespace().toUpperCase())
                .or(RuleNamespace.DEFAULT);
        return Rule.builder()
                .ruleNamespace(namespace)
                .ruleId(ruleDbModel.getRuleId())
                .conditions(ruleDbModel.getConditions())
                .actions(ruleDbModel.getActions())
                .description(ruleDbModel.getDescription())
                .priority(ruleDbModel.getPriority())
                .build();
    }
}
