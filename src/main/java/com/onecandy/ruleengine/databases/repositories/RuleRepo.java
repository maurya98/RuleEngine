package com.onecandy.ruleengine.databases.repositories;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.onecandy.ruleengine.databases.models.Rules;

public interface RuleRepo extends JpaRepository<Rules, Rules.RulesPK> {
    @Cacheable(cacheNames = "findByRuleNamespace", key = "#ruleNamespace")
    List<Rules> findByRuleNamespace(String ruleNamespace);

    @Cacheable(cacheNames = "findByRuleNamespaceAndIsActive", key = "#ruleNameSpace")
    List<Rules> findByRuleNamespaceAndIsActive(String ruleNameSpace, boolean isActive);
}
