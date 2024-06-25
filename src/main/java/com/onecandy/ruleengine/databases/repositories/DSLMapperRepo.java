package com.onecandy.ruleengine.databases.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onecandy.ruleengine.databases.models.DslMapper;

public interface DSLMapperRepo extends JpaRepository<DslMapper, Long> {

    DslMapper findByCategoryAndSubCategoryAndIsActive(String category, String subcategory, boolean isActive);
    
}
