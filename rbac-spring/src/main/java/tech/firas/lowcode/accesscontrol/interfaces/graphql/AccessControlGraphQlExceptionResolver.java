package tech.firas.lowcode.accesscontrol.interfaces.graphql;

import java.util.Map;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import tech.firas.lowcode.accesscontrol.domain.AccessControlErrorType;
import tech.firas.lowcode.accesscontrol.domain.AccessControlException;

@Component
public class AccessControlGraphQlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    private static final String FORBIDDEN = "FORBIDDEN";

    @Override
    protected GraphQLError resolveToSingleError(Throwable exception, DataFetchingEnvironment environment) {
        if (exception instanceof AccessDeniedException) {
            return error(ErrorType.FORBIDDEN, FORBIDDEN, exception, environment);
        }
        if (exception instanceof AccessControlException violation) {
            return error(toErrorType(violation.errorType()), violation.errorType().name(), exception, environment);
        }
        if (exception instanceof IllegalArgumentException) {
            return error(ErrorType.BAD_REQUEST, AccessControlErrorType.BAD_REQUEST.name(), exception, environment);
        }
        return null;
    }

    private ErrorType toErrorType(AccessControlErrorType errorType) {
        return switch (errorType) {
            case NOT_FOUND -> ErrorType.NOT_FOUND;
            case CONFLICT, BAD_REQUEST -> ErrorType.BAD_REQUEST;
        };
    }

    private GraphQLError error(
            ErrorType errorType, String code, Throwable exception, DataFetchingEnvironment environment) {
        return GraphQLError.newError()
                .errorType(errorType)
                .message(exception.getMessage())
                .extensions(Map.of("code", code))
                .path(environment.getExecutionStepInfo().getPath())
                .build();
    }
}
