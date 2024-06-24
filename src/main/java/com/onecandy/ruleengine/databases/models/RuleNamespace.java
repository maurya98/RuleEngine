package com.onecandy.ruleengine.databases.models;

import java.sql.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "rule_namespace")
public class RuleNamespace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String namespace;
    
    @Column(nullable = false)
    private String inputClass;
    
    @Column(nullable = false)
    private String outputClass;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String resolvingScript;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String postExecutionScript;

    private String createdBy;
    
    private String updatedBy;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @Column(columnDefinition = "BOOLEAN default 'true'")
    private boolean isActive;

}

