package tech.firas.lowcode.accesscontrol.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final RequestMatcher MACHINE_API = new OrRequestMatcher(
            PathPatternRequestMatcher.pathPattern("/api/**"),
            PathPatternRequestMatcher.pathPattern("/graphql"));

    private static final RequestMatcher CSRF_PROTECTED = new AndRequestMatcher(
            CsrfFilter.DEFAULT_CSRF_MATCHER,
            new NegatedRequestMatcher(new RequestHeaderRequestMatcher("Authorization")));

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                PathPatternRequestMatcher.pathPattern("/login"),
                                PathPatternRequestMatcher.pathPattern("/v3/api-docs/**"),
                                PathPatternRequestMatcher.pathPattern("/swagger-ui/**"),
                                PathPatternRequestMatcher.pathPattern("/swagger-ui.html"),
                                PathPatternRequestMatcher.pathPattern("/graphiql/**"))
                        .permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf
                        .requireCsrfProtectionMatcher(CSRF_PROTECTED)
                        .addObjectPostProcessor(new ObjectPostProcessor<CsrfFilter>() {
                            @Override
                            public <O extends CsrfFilter> O postProcess(O filter) {
                                filter.setAccessDeniedHandler(new CsrfAccessDeniedHandler());
                                return filter;
                            }
                        }))
                .exceptionHandling(handling -> handling.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), MACHINE_API))
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .build();
    }
}
