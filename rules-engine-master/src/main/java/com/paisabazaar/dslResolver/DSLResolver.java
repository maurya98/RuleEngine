package com.paisabazaar.dslResolver;

public interface DSLResolver {
    String getResolverKeyword();
    Object resolveValue(String keyword);
}
