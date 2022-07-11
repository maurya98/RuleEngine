package com.paisabazaar.knowledgeBase.models;

import com.paisabazaar.restAPI.RuleNamespace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
