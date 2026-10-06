# CSRF 只保护会话凭据，不拦截带 Authorization 头的请求

CSRF 攻击的前提是浏览器**自动附带**环境凭据（会话 cookie）。而带显式 `Authorization: Basic` 头的请求不携带环境凭据，浏览器不会替攻击者加上它，因此对这类请求要求 CSRF token 只会让 API 客户端无法调用。`SecurityConfig.CSRF_PROTECTED` 于是把默认的 CSRF 匹配器与"请求不带 `Authorization` 头"做与运算：表单登录会话照常受保护（包括 `/login` 本身，防登录 CSRF），Basic-auth 客户端则被放行。

## Considered Options

- **全局关闭 CSRF**：会让表单登录会话暴露在跨站请求伪造下，不可接受。
- **改为"有会话才要求 CSRF"**：更简单，但登录请求本身此刻还没有会话，会放过登录 CSRF。
- **改用 `CookieCsrfTokenRepository` 让客户端读 cookie 回显**：仍要求每个 API 客户端先取 token 再回显，对 curl / 服务间调用很不友好。

## Consequences

- `CsrfFilter` 自带 `AccessDeniedHandler`，会绕过 `ExceptionHandling` 里为 `/api/**` 与 `/graphql` 配置的 401 entry point，导致匿名调用方拿到 403 而不是 401——而 403 不会触发客户端的重新认证流程。`CsrfConfigurer` 在 Spring Security 7 里没有暴露 `accessDeniedHandler(...)`，所以用 `addObjectPostProcessor` 直接给 `CsrfFilter` 换上 `CsrfAccessDeniedHandler`：匿名请求回 401，已认证请求回 403。
- 这个匹配器是安全边界的一部分。若将来引入 Cookie 之外的环境凭据（如自动附带的客户端证书），需要重新评估。
