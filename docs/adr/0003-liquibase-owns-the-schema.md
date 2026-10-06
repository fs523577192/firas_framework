# Liquibase 独占 DDL，Hibernate 只做 validate

表结构由 `src/main/resources/db/changelog/db.changelog-master.yaml` 显式定义，`spring.jpa.hibernate.ddl-auto=validate` 让 Hibernate 在启动时校验实体映射与真实 schema 是否一致，但绝不修改它。

## Considered Options

`ddl-auto=update` 会让实体注解隐式成为 schema 的来源，既无法审阅也无法回滚，还会在不同环境间产生漂移。

## Consequences

- 表名与列名使用领域语言（`ac_role_assignment`、`ac_permission_grant`），而不是 Hibernate 的默认命名。
- 保护性删除有两道防线：适配器在删除前显式检查并抛领域异常（`RoleStillAssignedException` / `PermissionStillGrantedException`），外键作为并发下的最终兜底。级联方向按"谁拥有这条关联"设计：`ac_role_assignment.user_id` 和 `ac_permission_grant.role_id` 带 `ON DELETE CASCADE`（删 User 带走其 RoleAssignment，删 Role 带走其 PermissionGrant）；而 `ac_role_assignment.role_id` 和 `ac_permission_grant.permission_id` **不带** cascade——正是这两条让"仍被引用的 Role / Permission 删不掉"在数据库层也成立。
- 实体与 changelog 若不同步，应用**启动即失败**，集成测试会立刻暴露。这是有意的：宁可启动失败，也不要静默的 schema 漂移。
