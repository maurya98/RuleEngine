package com.tech.knowledgeBase.models;

import com.tech.restAPI.RuleNamespace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Rule {
    RuleNamespace ruleNamespace;
    String ruleId;
    String conditions;
    String actions;
    Integer priority;
    String description;
}
