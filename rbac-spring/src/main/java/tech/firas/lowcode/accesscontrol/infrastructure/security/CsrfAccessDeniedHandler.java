package tech.firas.lowcode.accesscontrol.infrastructure.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CsrfAccessDeniedHandler implements AccessDeniedHandler {

    private final AccessDeniedHandler authenticated = new AccessDeniedHandlerImpl();
    private final HttpStatusEntryPoint anonymous = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
            throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            anonymous.commence(request, response,
                    new InsufficientAuthenticationException(exception.getMessage(), exception));
            return;
        }
        authenticated.handle(request, response, exception);
    }
}
