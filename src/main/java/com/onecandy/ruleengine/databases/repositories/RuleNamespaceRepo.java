package com.onecandy.ruleengine.databases.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onecandy.ruleengine.databases.models.RuleNamespace;
import java.util.List;

public interface RuleNamespaceRepo extends JpaRepository<RuleNamespace, Long> {
    List<RuleNamespace> findByNamespace(String namespace);
    RuleNamespace findByNamespaceAndIsActive(String namespace, boolean isActive);
}
