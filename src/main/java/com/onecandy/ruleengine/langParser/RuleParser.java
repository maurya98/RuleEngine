package com.onecandy.ruleengine.langParser;

public interface RuleParser<INPUT_DATA, OUTPUT_RESULT> {
    boolean parseCondition(String condition, INPUT_DATA inputData);
    OUTPUT_RESULT parseAction(String action, INPUT_DATA inputData, OUTPUT_RESULT outputResult);
}
