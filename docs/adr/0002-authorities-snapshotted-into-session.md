# 权限在登录时快照进会话，而非每请求查库

会话式认证下，`RbacUserDetailsService` 在 Authentication 时一次性把 User 的角色（`ROLE_<code>`）与有效权限（`PermissionCode` 原值）算好放进 `GrantedAuthority`，之后每个请求的 `@PreAuthorize` / `hasAuthority` 判定读的是会话快照，不再访问数据库。

## Considered Options

每请求查库能让授权变更立即生效，但把 RBAC 的判定放到了所有请求的热路径上；会话快照把数据库访问压到登录那一次。

## Consequences

- **撤销有延迟**：回收角色、撤销权限或禁用用户后，已登录会话仍持有旧权限，直到该会话失效或用户重新登录。`AuthorizationService.authorize(...)` 是实时的（它查库），所以程序化判定不受影响——但它不是 `@PreAuthorize` 走的那条路。
- 这是安全相关的取舍，不是纯性能优化。若将来出现"必须立即生效"的合规要求，需要引入会话失效机制（如按用户名索引的 `SessionRegistry` 并在授权变更时踢掉对应会话）。
