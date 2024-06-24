package com.onecandy.ruleengine.databases.models;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;

@Data
@Entity(name = "rules")
@IdClass(Rules.RulesPK.class)
public class Rules {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ruleId;

    private String ruleNamespace;

    @Column(columnDefinition = "TEXT")
    private String conditions;
    
    @Column(columnDefinition = "TEXT")
    private String actions;
    
    private int priority;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "BOOLEAN default 'true'")
    private boolean isActive;
    
    @CreationTimestamp
    private Date createdAt;
    
    @UpdateTimestamp
    private Date updatedAt;
    
    private String createdBy;
    
    private String updatedBy;

    @Data
    public static class RulesPK implements Serializable {
        private Long ruleId;
        private String ruleNamespace;

        // Default constructor is needed for Hibernate
        public RulesPK() {}

        public RulesPK(Long ruleId, String ruleNamespace) {
            this.ruleId = ruleId;
            this.ruleNamespace = ruleNamespace;
        }

        // Equals and hashCode methods, required for composite keys

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RulesPK rulesPK = (RulesPK) o;
            return Objects.equals(ruleId, rulesPK.ruleId) &&
                   Objects.equals(ruleNamespace, rulesPK.ruleNamespace);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ruleId, ruleNamespace);
        }
    }
}
