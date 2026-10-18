package com.example.gestiongnet;

import com.example.gestiongnet.model.User;
import com.example.gestiongnet.repository.AgenceRepository;
import com.example.gestiongnet.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class GestionGNetApplicationTests {
    @Autowired private MockMvc mvc;
    @Autowired private UserRepository users;
    @Autowired private AgenceRepository agences;
    @Autowired private PasswordEncoder encoder;
    @Autowired private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        agences.deleteAll();
        users.deleteAll();
        users.save(new User(null, "admin", encoder.encode("test-password-1234"), "ADMIN"));
    }

    @Test
    void publicReadAndProtectedWrite() throws Exception {
        mvc.perform(get("/api/agences")).andExpect(status().isOk());
        mvc.perform(post("/api/agences")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Tunis\",\"adresse\":\"Centre-ville\",\"etat\":true}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void loginRejectsBadCredentials() throws Exception {
        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedAdminCanCreateAndReadAgency() throws Exception {
        String response = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"test-password-1234\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("ADMIN"))
            .andReturn().getResponse().getContentAsString();
        JsonNode json = mapper.readTree(response);
        String token = json.get("token").asText();
        mvc.perform(post("/api/agences")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Agence Tunis\",\"adresse\":\"Tunis Centre\",\"etat\":true}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nom").value("Agence Tunis"));
        mvc.perform(get("/api/agences/search").param("region", "Tunis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nom").value("Agence Tunis"));
    }

    @Test
    void agencyValidationRejectsBlankNameForAdmin() throws Exception {
        String response = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"test-password-1234\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token = mapper.readTree(response).get("token").asText();
        mvc.perform(post("/api/agences")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"\",\"adresse\":\"Tunis Centre\",\"etat\":true}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void missingAgencyReturns404() throws Exception {
        mvc.perform(get("/api/agences/999999")).andExpect(status().isNotFound());
    }

    @Test
    void publicPasswordResetEndpointIsGone() throws Exception {
        mvc.perform(get("/auth/reset-admin-password")).andExpect(status().isUnauthorized());
    }
}
