package com.gamermajilis;

import com.gamermajilis.controller.TournamentController;
import com.gamermajilis.model.Tournament;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.service.TournamentService;
import com.gamermajilis.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(TournamentController.class)
@Import(TestSecurityConfig.class)
public class TournamentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    @MockBean
    private JwtUtil jwtUtil;

    private Tournament mockTournament;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        mockTournament = new Tournament();
        mockTournament.setId(1L);
        mockTournament.setName("Test Tournament");
        mockTournament.setGameTitle("Test Game");
        mockTournament.setMaxParticipants(16);
    }

    @BeforeEach
    void setUp1() {
        Mockito.when(jwtUtil.validateToken(any())).thenReturn(true); // Mock token validation
        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L); // Mock user ID extraction
    }

    @Test
    void testCreateTournament() throws Exception {
        Mockito.when(tournamentService.createTournament(any())).thenReturn(mockTournament);

        mockMvc.perform(post("/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Tournament\",\"description\":\"Test Description\",\"gameTitle\":\"Test Game\",\"maxParticipants\":16,\"startDate\":\"2023-12-01T10:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Tournament"));
    }

    @Test
    void testUpdateTournament() throws Exception {
        Mockito.when(tournamentService.updateTournament(eq(1L), any())).thenReturn(mockTournament);

        mockMvc.perform(put("/tournaments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Tournament\",\"description\":\"Updated Description\",\"gameTitle\":\"Updated Game\",\"maxParticipants\":32,\"startDate\":\"2023-12-15T15:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Tournament"));
    }

    @Test
    void testDeleteTournament() throws Exception {
        mockMvc.perform(delete("/tournaments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTournamentById() throws Exception {
        Mockito.when(tournamentService.getTournamentById(eq(1L))).thenReturn(Optional.of(mockTournament));

        mockMvc.perform(get("/tournaments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Tournament"));
    }

    @Test
    void testGetAllTournaments() throws Exception {
        Mockito.when(tournamentService.getAllTournaments()).thenReturn(Collections.singletonList(mockTournament));

        mockMvc.perform(get("/tournaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Tournament"));
    }

    @Test
    void testAddModerator() throws Exception {
        mockMvc.perform(post("/tournaments/1/moderators")
                .param("moderatorId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void testIncrementViewCount() throws Exception {
        mockMvc.perform(post("/tournaments/1/view"))
                .andExpect(status().isOk());
    }
}