package com.onecandy.ruleengine.langParser;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public interface RuleParser<INPUT_DATA, OUTPUT_RESULT> {
    @Bean
    boolean parseCondition(String condition, INPUT_DATA inputData);
    @Bean
    OUTPUT_RESULT parseAction(String action, INPUT_DATA inputData, OUTPUT_RESULT outputResult);
}
