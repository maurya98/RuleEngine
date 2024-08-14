package com.onecandy.ruleengine.databases.repositories;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.onecandy.ruleengine.databases.models.RuleNamespace;
import java.util.List;

public interface RuleNamespaceRepo extends JpaRepository<RuleNamespace, Long> {
    @Cacheable(cacheNames = "findByNamespace", key = "#namespace")
    List<RuleNamespace> findByNamespace(String namespace);

    @Cacheable(cacheNames = "findByNamespaceAndIsActive", key = "#namespace")
    RuleNamespace findByNamespaceAndIsActive(String namespace, boolean isActive);
}
