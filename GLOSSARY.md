# Access Control

lowcode 平台的访问控制上下文：决定"谁可以做什么"。它管理身份、角色与权限之间的授予关系，并对每一次操作请求给出鉴权判定。

## Language

### 身份

**User**:
一个可被认证的身份主体，持有登录凭据，并被授予零或多个 Role。
_Avoid_: Account、Principal、Member、Subject、用户账号

**Username**:
User 用于登录的唯一可读名称。
_Avoid_: login、loginName

### 授权模型

**Role**:
一组 Permission 的命名集合，是授予 User 的中间层。Role 本身不代表一个人，也不携带凭据。
_Avoid_: Group、Profile、Position、岗位、用户组

**Permission**:
一项原子的操作许可，由 PermissionCode 唯一标识。Permission 是授予的最小单位，不可再分。
_Avoid_: Right、Privilege、Authority、权限点

**PermissionCode**:
Permission 的标识值对象，由两个或更多小写段以 `:` 分隔（例：`document:read`、`report:finance:export`）。全局唯一、不可变。
_Avoid_: permission string、permission key

**RoleAssignment**:
"某个 User 持有某个 Role"这一事实。
_Avoid_: UserRole、user-role mapping

**PermissionGrant**:
"某个 Role 携带某个 Permission"这一事实。
_Avoid_: RolePermission、role-permission mapping

### 鉴权

**Effective Permissions**:
某个 User 的有效权限，即其全部 Role 所携带 Permission 的并集。
_Avoid_: 用户权限、granted permissions（含义模糊，不区分直接授予还是经由 Role）

**AuthorizationDecision**:
针对"某个 User 是否持有某个 PermissionCode"这一问题给出的结果：允许或拒绝，并附带理由。
_Avoid_: check result、auth result

**Authorization**:
给出 AuthorizationDecision 的过程。与 Authentication（确认"你是谁"）严格区分。
_Avoid_: 权限校验、access check

**Authentication**:
确认请求方身份的过程，产出一个已认证的 User。
_Avoid_: login（login 只是 Authentication 的一种入口）

### 管理

**Access Administration**:
建立并维护 User、Role、Permission 及其 RoleAssignment / PermissionGrant 的用例集合。
_Avoid_: 权限管理（过窄，遗漏了 User 与 Role 本身的管理）

**Bootstrap Administrator**:
系统首次启动时自动建立的第一个 User，持有可以执行 Access Administration 的 Role。它的存在让"管理访问控制"这件事有一个起点。
_Avoid_: root、内置账号
