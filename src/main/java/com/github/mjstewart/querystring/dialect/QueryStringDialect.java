package com.github.mjstewart.querystring.dialect;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class QueryStringDialect extends AbstractDialect implements IExpressionObjectDialect {

    public QueryStringDialect() {
        super("queryStringHelper");
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new QueryStringExpressionFactory();
    }
}
