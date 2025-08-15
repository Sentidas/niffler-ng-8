package guru.qa.niffler.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FriendsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Sql(scripts = "/allFriendsShouldBeReturned.sql")
    @Test
    void currentUserShouldBeReturned() throws Exception {

        mockMvc.perform(get("/internal/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Lisa")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Lisa"));

        mockMvc.perform(get("/internal/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "LisaFriend")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("LisaFriend"));

        mockMvc.perform(get("/internal/friends/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "Lisa")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username == 'LisaFriend')]").exists())
                .andExpect(jsonPath("$[?(@.username == 'LisaFriend')].currency").value("EUR"))
                .andExpect(jsonPath("$[?(@.username == 'LisaFriend')].friendshipStatus").value("FRIEND"));
    }
}