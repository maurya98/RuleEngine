package com.onecandy.ruleengine.langParser;

import java.util.HashMap;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@SuppressWarnings("rawtypes")
public class RuleParserImpl implements RuleParser {

    protected DSLParser dslParser;
    protected MVELParser mvelParser;
    private final String INPUT_KEYWORD = "input";
    private final String OUTPUT_KEYWORD = "output";

    protected RuleParserImpl(DSLParser dslParser, MVELParser mvelParser){
        this.dslParser = dslParser;
        this.mvelParser = mvelParser;
    }

    /**
     * Parsing in given priority/steps.
     *
     * Step 1. Resolve domain specific keywords first: $(rulenamespace.keyword)
     * Step 2. Resolve MVEL expression.
     *
     * @param expression
     * @param inputData
     */

    @Override
    public boolean parseCondition(String condition, Object inputData) {
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(condition);
        Map<String, Object> input = new HashMap<>();
        input.put(INPUT_KEYWORD, inputData);
        return mvelParser.parseMvelExpression(resolvedDslExpression, input);
    }

    @Override
    public Object parseAction(String action, Object inputData, Object outputResult) {
        String resolvedDslExpression = dslParser.resolveDomainSpecificKeywords(action);
        Map<String, Object> input = new HashMap<>();
        input.put(INPUT_KEYWORD, inputData);
        input.put(OUTPUT_KEYWORD, outputResult);
        mvelParser.parseMvelExpression(resolvedDslExpression, input);
        return outputResult;
    }

}
