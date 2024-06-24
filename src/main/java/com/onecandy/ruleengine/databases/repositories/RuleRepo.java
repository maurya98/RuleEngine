package com.onecandy.ruleengine.databases.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.onecandy.ruleengine.databases.models.Rules;

public interface RuleRepo extends JpaRepository<Rules, Rules.RulesPK> {
    List<Rules> findByRuleNamespace(String ruleNamespace);
    List<Rules> findByRuleNamespaceAndIsActive(String ruleNameSpace, boolean isActive);
}
