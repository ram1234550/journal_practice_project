package com.journal.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.backend.entity.User;
import com.journal.backend.repository.ArticleRepository;
import com.journal.backend.repository.ReviewRepository;
import com.journal.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JournalWorkflowIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();

        createUser("Admin User", "admin@test.local", "admin123", List.of("ADMIN"));
        createUser("Chair User", "chair@test.local", "chair123", List.of("CHAIR", "AUTHOR"));
        createUser("Reviewer User", "reviewer@test.local", "reviewer123", List.of("REVIEWER"));
    }

    @Test
    void fullWorkflowPublishesArticle() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Author User",
                                "email", "author@test.local",
                                "password", "author123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("AUTHOR"));

        String authorToken = loginAndGetToken("author@test.local", "author123");

        String createResponse = mockMvc.perform(post("/api/articles")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "A Test Article",
                                "topic", "Technology",
                                "content", "This is a test article."
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long articleId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/articles/my")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("A Test Article"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        String adminToken = loginAndGetToken("admin@test.local", "admin123");
        Long reviewerId = userRepository.findByEmail("reviewer@test.local").orElseThrow().getId();

        mockMvc.perform(post("/api/admin/assign")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "articleId", articleId,
                                "reviewerId", reviewerId
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNDER_REVIEW"));

        String reviewerToken = loginAndGetToken("reviewer@test.local", "reviewer123");

        mockMvc.perform(get("/api/articles/reviewer/me")
                        .header("Authorization", "Bearer " + reviewerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(articleId))
                .andExpect(jsonPath("$[0].status").value("UNDER_REVIEW"));

        mockMvc.perform(post("/api/articles/submit-review")
                        .header("Authorization", "Bearer " + reviewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "articleId", articleId,
                                "verdict", "ACCEPTED",
                                "comment", "Looks good."
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        mockMvc.perform(get("/api/articles/published").param("topic", "Technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(articleId))
                .andExpect(jsonPath("$[0].authorName").value("Author User"));
    }

    @Test
    void publicRootPageIsAvailableWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));
    }

    @Test
    void chairCanUseAdminEndpoints() throws Exception {
        String chairToken = loginAndGetToken("chair@test.local", "chair123");

        mockMvc.perform(get("/api/admin/reviewers")
                        .header("Authorization", "Bearer " + chairToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").exists());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("token").asText();
    }

    private User createUser(String name, String email, String rawPassword, List<String> roles) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
