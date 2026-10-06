# 领域聚合直接标注为 JPA 实体，不设独立的持久化模型

访问控制上下文的 `User` / `Role` / `Permission` 聚合根同时是 `@Entity`。我们放弃了"纯领域模型 + 独立持久化实体 + 映射器"的方案：这个上下文只有三个聚合、结构稳定，映射层带来的重复代码远超它换来的隔离收益。作为补偿，**仓储端口保持框架无关**——`domain.repository` 下的接口不出现任何 Spring / JPA 类型，Spring Data 接口与适配器都收在 `infrastructure.persistence`，领域代码经由适配器调用它们。

## Consequences

- JPA 约束会渗进聚合：字段不能是 `final`，必须留 `protected` 无参构造器，`Role.permissions` / `User.roles` 只能用 `Set` 的具体实现初始化。
- 聚合的关联是 `FetchType.EAGER`。`open-in-view=false`，若在事务外访问懒加载集合会抛 `LazyInitializationException`；EAGER 让聚合在任何地方都是完整可用的，代价是载入 `User` 必然带上其 `Role` 与 `Permission`。对访问控制这种小而有界的图是划算的，但不适用于其他上下文。
