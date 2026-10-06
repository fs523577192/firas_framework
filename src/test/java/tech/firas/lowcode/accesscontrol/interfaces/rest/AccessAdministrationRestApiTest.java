package tech.firas.lowcode.accesscontrol.interfaces.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import tech.firas.lowcode.PostgreSqlIntegrationTest;

class AccessAdministrationRestApiTest extends PostgreSqlIntegrationTest {

    private static final String CREATE_EDITOR = "{\"code\": \"editor\", \"description\": \"Can author documents\"}";
    private static final String REWORDED_EDITOR =
            "{\"description\": \"Can author, revise and publish\"}";
    private static final String DEFINE_READ = "{\"code\": \"document:read\", \"description\": \"Read any document\"}";
    private static final String REWORDED_READ =
            "{\"description\": \"Read any document anywhere\"}";
    private static final String GRANT_READ = "{\"code\": \"document:read\"}";
    private static final String ASSIGN_EDITOR = "{\"roleCode\": \"editor\"}";
    private static final String REGISTER_ALICE = "{\"username\": \"alice\", \"password\": \"s3cret\"}";
    private static final String NEW_PASSWORD = "{\"password\": \"a-new-password\"}";
    private static final String BROKEN_PERMISSION = "{\"code\": \"NOT A PERMISSION CODE\", \"description\": \"Broken\"}";

    @Autowired
    private MockMvc mockMvc;

    private MockHttpServletRequestBuilder asRoot(MockHttpServletRequestBuilder request) {
        return request.with(httpBasic("root", "pw"));
    }

    @Test
    void refusesAnAnonymousCaller() throws Exception {
        mockMvc.perform(get("/api/access/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void refusesAnAuthenticatedCallerWhoseRolesDoNotCarryTheAdministerPermission() throws Exception {
        givenAUserHoldingNoRole("alice", "pw");

        mockMvc.perform(post("/api/access/admin/roles").with(httpBasic("alice", "pw"))
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_EDITOR))
                .andExpect(status().isForbidden());
    }

    @Test
    void letsAnAdministratorCreateARole() throws Exception {
        givenAnAdministrator("root", "pw");

        mockMvc.perform(asRoot(post("/api/access/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_EDITOR)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("editor"))
                .andExpect(jsonPath("$.description").value("Can author documents"));
    }

    @Test
    void reportsADuplicateRoleCodeAsAConflict() throws Exception {
        givenAnAdministrator("root", "pw");

        mockMvc.perform(asRoot(post("/api/access/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_EDITOR)))
                .andExpect(status().isCreated());
        mockMvc.perform(asRoot(post("/api/access/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_EDITOR)))
                .andExpect(status().isConflict());
    }

    @Test
    void reportsAReferenceToSomethingThatDoesNotExistAsNotFound() throws Exception {
        givenAnAdministrator("root", "pw");

        mockMvc.perform(asRoot(post("/api/access/admin/users/ghost/roles")
                        .contentType(MediaType.APPLICATION_JSON).content(ASSIGN_EDITOR)))
                .andExpect(status().isNotFound());
    }

    @Test
    void reportsAMalformedPermissionCodeAsABadRequest() throws Exception {
        givenAnAdministrator("root", "pw");

        mockMvc.perform(asRoot(post("/api/access/admin/permissions")
                        .contentType(MediaType.APPLICATION_JSON).content(BROKEN_PERMISSION)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tellsAnAuthenticatedCallerWhoTheyAreAndWhatTheyMayDo() throws Exception {
        givenAnAdministrator("root", "pw");

        mockMvc.perform(get("/api/access/me").with(httpBasic("root", "pw")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("root"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.roleCodes", hasItem("super-admin")))
                .andExpect(jsonPath("$.permissions", hasItem(ADMINISTER_ACCESS)));
    }

    @Test
    void givesAnAuthenticatedCallerALiveAuthorizationDecision() throws Exception {
        givenAnAdministrator("root", "pw");

        mockMvc.perform(get("/api/access/decisions/access:administer").with(httpBasic("root", "pw")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.reason").value(containsString("holds")));
    }

    @Test
    void letsAClientThatSendsAnAuthorizationHeaderMutateWithoutACsrfToken() throws Exception {
        givenAnAdministrator("root", "pw");

        mockMvc.perform(post("/api/access/admin/roles").with(httpBasic("root", "pw"))
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_EDITOR))
                .andExpect(status().isCreated());
    }

    @Test
    void refusesASessionAuthenticatedMutationThatCarriesNoCsrfToken() throws Exception {
        givenAnAdministrator("root", "pw");
        MvcResult login = mockMvc.perform(formLogin("/login").user("root").password("pw"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(post("/api/access/admin/roles").session(session)
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_EDITOR))
                .andExpect(status().isForbidden());
    }

    @Test
    void keepsACallerAuthenticatedAcrossRequestsWithinASession() throws Exception {
        givenAnAdministrator("root", "pw");

        MvcResult login = mockMvc.perform(formLogin("/login").user("root").password("pw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername("root"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(get("/api/access/me").session(session))
                .andExpect(authenticated().withUsername("root"))
                .andExpect(jsonPath("$.username").value("root"));
    }

    @Test
    void letsAnAdministratorUpdateWhatTheyCreated() throws Exception {
        givenAnAdministrator("root", "pw");
        givenAnEditorAbleToReadDocuments();

        mockMvc.perform(asRoot(patch("/api/access/admin/roles/editor")
                        .contentType(MediaType.APPLICATION_JSON).content(REWORDED_EDITOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Can author, revise and publish"));

        mockMvc.perform(asRoot(patch("/api/access/admin/permissions/document:read")
                        .contentType(MediaType.APPLICATION_JSON).content(REWORDED_READ)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Read any document anywhere"));

        mockMvc.perform(asRoot(post("/api/access/admin/users/alice/password")
                        .contentType(MediaType.APPLICATION_JSON).content(NEW_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void letsAnAdministratorDeleteWhatNobodyReferencesAnyMore() throws Exception {
        givenAnAdministrator("root", "pw");
        givenAnEditorAbleToReadDocuments();

        mockMvc.perform(asRoot(delete("/api/access/admin/users/alice"))).andExpect(status().isNoContent());
        mockMvc.perform(asRoot(delete("/api/access/admin/roles/editor"))).andExpect(status().isNoContent());
        mockMvc.perform(asRoot(delete("/api/access/admin/permissions/document:read"))).andExpect(status().isNoContent());

        mockMvc.perform(asRoot(get("/api/access/admin/users")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(asRoot(get("/api/access/admin/roles")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(asRoot(get("/api/access/admin/permissions")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void refusesToDeleteARoleThatUsersStillHold() throws Exception {
        givenAnAdministrator("root", "pw");
        givenAnEditorAbleToReadDocuments();

        mockMvc.perform(asRoot(delete("/api/access/admin/roles/editor"))).andExpect(status().isConflict());
    }

    private void givenAnEditorAbleToReadDocuments() throws Exception {
        mockMvc.perform(asRoot(post("/api/access/admin/permissions")
                        .contentType(MediaType.APPLICATION_JSON).content(DEFINE_READ)))
                .andExpect(status().isCreated());
        mockMvc.perform(asRoot(post("/api/access/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_EDITOR)))
                .andExpect(status().isCreated());
        mockMvc.perform(asRoot(post("/api/access/admin/roles/editor/permissions")
                        .contentType(MediaType.APPLICATION_JSON).content(GRANT_READ)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissionCodes[0]").value("document:read"));
        mockMvc.perform(asRoot(post("/api/access/admin/users")
                        .contentType(MediaType.APPLICATION_JSON).content(REGISTER_ALICE)))
                .andExpect(status().isCreated());
        mockMvc.perform(asRoot(post("/api/access/admin/users/alice/roles")
                        .contentType(MediaType.APPLICATION_JSON).content(ASSIGN_EDITOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleCodes[0]").value("editor"))
                .andExpect(jsonPath("$.permissions[0]").value("document:read"));
    }
}
