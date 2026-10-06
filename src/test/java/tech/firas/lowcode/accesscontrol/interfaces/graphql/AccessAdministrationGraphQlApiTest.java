package tech.firas.lowcode.accesscontrol.interfaces.graphql;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import tech.firas.lowcode.PostgreSqlIntegrationTest;

class AccessAdministrationGraphQlApiTest extends PostgreSqlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ResultActions execute(String query, String username, String password) throws Exception {
        var request = post("/graphql").contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + escape(query) + "\"}");
        if (username != null) {
            request = request.with(httpBasic(username, password));
        }
        return mockMvc.perform(request);
    }

    private static String escape(String query) {
        return query.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\t", "\\t");
    }

    @Test
    void refusesAnAnonymousCaller() throws Exception {
        execute("query { me { username } }", null, null).andExpect(status().isUnauthorized());
    }

    @Test
    void refusesAnAuthenticatedCallerWhoseRolesDoNotCarryTheAdministerPermission() throws Exception {
        givenAUserHoldingNoRole("alice", "pw");

        execute("""
                mutation { createRole(code: "editor", description: "Can author and revise documents") { code } }
                """, "alice", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createRole").doesNotExist())
                .andExpect(jsonPath("$.errors[0].extensions.code").value("FORBIDDEN"));
    }

    @Test
    void tellsAnAuthenticatedCallerWhoTheyAreAndWhatTheyMayDo() throws Exception {
        givenAnAdministrator("root", "pw");

        execute("query { me { username enabled roleCodes permissions } }", "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.me.username").value("root"))
                .andExpect(jsonPath("$.data.me.enabled").value(true))
                .andExpect(jsonPath("$.data.me.roleCodes", hasItem("super-admin")))
                .andExpect(jsonPath("$.data.me.permissions", hasItem(ADMINISTER_ACCESS)));
    }

    @Test
    void givesAnAuthenticatedCallerALiveAuthorizationDecision() throws Exception {
        givenAnAdministrator("root", "pw");

        execute("""
                query { amIPermitted(permission: "access:administer") { allowed reason } }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amIPermitted.allowed").value(true));
    }

    @Test
    void letsAnAdministratorGrantAPermissionToARoleAndAssignItToAUser() throws Exception {
        givenAnAdministrator("root", "pw");

        execute("""
                mutation {
                  definePermission(code: "document:read", description: "Read any document") { code description }
                  createRole(code: "editor", description: "Can author and revise documents") { code }
                  grantPermission(roleCode: "editor", code: "document:read") { code permissionCodes }
                  registerUser(username: "alice", password: "s3cret") { username enabled }
                  assignRole(username: "alice", roleCode: "editor") { username roleCodes permissions }
                }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.definePermission.code").value("document:read"))
                .andExpect(jsonPath("$.data.definePermission.description").value("Read any document"))
                .andExpect(jsonPath("$.data.createRole.code").value("editor"))
                .andExpect(jsonPath("$.data.grantPermission.permissionCodes[0]").value("document:read"))
                .andExpect(jsonPath("$.data.registerUser.username").value("alice"))
                .andExpect(jsonPath("$.data.assignRole.roleCodes[0]").value("editor"))
                .andExpect(jsonPath("$.data.assignRole.permissions[0]").value("document:read"));
    }

    @Test
    void letsAnAdministratorReadBackTheWholeAccessModel() throws Exception {
        givenAnAdministrator("root", "pw");
        execute("""
                mutation {
                  definePermission(code: "document:read", description: "Read any document") { code }
                  createRole(code: "editor", description: "Can author and revise documents") { code }
                  registerUser(username: "alice", password: "s3cret") { username }
                }
                """, "root", "pw")
                .andExpect(status().isOk());

        execute("""
                query {
                  users { username }
                  user(username: "alice") { username enabled roleCodes }
                  roles { code }
                  role(code: "editor") { code description permissionCodes }
                  permissions { code }
                  permission(code: "document:read") { code description }
                }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users.length()").value(2))
                .andExpect(jsonPath("$.data.user.username").value("alice"))
                .andExpect(jsonPath("$.data.user.enabled").value(true))
                .andExpect(jsonPath("$.data.roles[*].code", hasItem("editor")))
                .andExpect(jsonPath("$.data.role.description").value("Can author and revise documents"))
                .andExpect(jsonPath("$.data.permissions[*].code", hasItem("document:read")))
                .andExpect(jsonPath("$.data.permission.description").value("Read any document"));
    }

    @Test
    void letsAnAdministratorUpdateWhatTheyCreated() throws Exception {
        givenAnAdministrator("root", "pw");
        execute("""
                mutation {
                  definePermission(code: "document:read", description: "Read any document") { code }
                  createRole(code: "editor", description: "Can author and revise documents") { code }
                  registerUser(username: "alice", password: "s3cret") { username }
                }
                """, "root", "pw")
                .andExpect(status().isOk());

        execute("""
                mutation {
                  updateRoleDescription(code: "editor", description: "Can author, revise and publish") {
                    code description
                  }
                  updatePermissionDescription(code: "document:read", description: "Read any document anywhere") {
                    code description
                  }
                  changeUserPassword(username: "alice", password: "a-new-password") { username }
                  disableUser(username: "alice") { username enabled }
                  enableUser(username: "alice") { username enabled }
                }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateRoleDescription.description").value("Can author, revise and publish"))
                .andExpect(jsonPath("$.data.updatePermissionDescription.description")
                        .value("Read any document anywhere"))
                .andExpect(jsonPath("$.data.changeUserPassword.username").value("alice"))
                .andExpect(jsonPath("$.data.disableUser.enabled").value(false))
                .andExpect(jsonPath("$.data.enableUser.enabled").value(true));
    }

    @Test
    void letsAnAdministratorDeleteWhatNobodyReferencesAnyMore() throws Exception {
        givenAnAdministrator("root", "pw");
        execute("""
                mutation {
                  definePermission(code: "document:read", description: "Read any document") { code }
                  createRole(code: "editor", description: "Can author and revise documents") { code }
                  registerUser(username: "alice", password: "s3cret") { username }
                }
                """, "root", "pw")
                .andExpect(status().isOk());

        execute("""
                mutation {
                  deleteUser(username: "alice")
                  deleteRole(code: "editor")
                  deletePermission(code: "document:read")
                }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteUser").value(true))
                .andExpect(jsonPath("$.data.deleteRole").value(true))
                .andExpect(jsonPath("$.data.deletePermission").value(true));

        execute("query { users { username } roles { code } permissions { code } }", "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users.length()").value(1))
                .andExpect(jsonPath("$.data.roles.length()").value(1))
                .andExpect(jsonPath("$.data.permissions.length()").value(1));
    }

    @Test
    void reportsADuplicateRoleCodeAsAConflict() throws Exception {
        givenAnAdministrator("root", "pw");

        execute("""
                mutation { createRole(code: "editor", description: "Can author and revise documents") { code } }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createRole.code").value("editor"));

        execute("""
                mutation { createRole(code: "editor", description: "A second, conflicting role") { code } }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createRole").doesNotExist())
                .andExpect(jsonPath("$.errors[0].extensions.code").value("CONFLICT"));
    }

    @Test
    void reportsAReferenceToSomethingThatDoesNotExistAsNotFound() throws Exception {
        givenAnAdministrator("root", "pw");

        execute("""
                mutation { assignRole(username: "ghost", roleCode: "super-admin") { username } }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].extensions.code").value("NOT_FOUND"));
    }

    @Test
    void reportsAMalformedPermissionCodeAsABadRequest() throws Exception {
        givenAnAdministrator("root", "pw");

        execute("""
                mutation { definePermission(code: "NOT A PERMISSION CODE", description: "Broken") { code } }
                """, "root", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].extensions.code").value("BAD_REQUEST"));
    }
}
