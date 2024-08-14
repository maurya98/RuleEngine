package com.onecandy.ruleengine.databases.repositories;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.onecandy.ruleengine.databases.models.DslMapper;

public interface DSLMapperRepo extends JpaRepository<DslMapper, Long> {
    @Cacheable(cacheNames = "findByCategoryAndSubCategoryAndIsActive", key = "#category + _ + #subcategory")
    DslMapper findByCategoryAndSubCategoryAndIsActive(String category, String subcategory, boolean isActive);
    
}
