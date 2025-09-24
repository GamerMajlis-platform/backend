package com.gamermajilis;

import com.gamermajilis.controller.TournamentParticipationController;
import com.gamermajilis.model.TournamentParticipation;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.service.TournamentParticipationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TournamentParticipationController.class)
@Import(TestSecurityConfig.class)
public class TournamentParticipationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentParticipationService participationService;

    private TournamentParticipation mockParticipation;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        mockParticipation = new TournamentParticipation();
        mockParticipation.setId(1L);
    }
    @BeforeEach
    void setUp1() {
        Mockito.when(jwtUtil.validateToken(any())).thenReturn(true); // Mock token validation
        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L); // Mock user ID extraction
    }

    @Test
    void testRegisterParticipant() throws Exception {
        Mockito.when(participationService.registerParticipant(eq(1L), eq(2L))).thenReturn(mockParticipation);

        mockMvc.perform(post("/tournaments/1/participants/register")
                .param("participantId", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testCheckInParticipant() throws Exception {
        mockMvc.perform(post("/tournaments/1/participants/check-in")
                .param("participantId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void testDisqualifyParticipant() throws Exception {
        mockMvc.perform(post("/tournaments/1/participants/disqualify")
                .param("participantId", "2")
                .param("reason", "Violation of rules"))
                .andExpect(status().isOk());
    }

    @Test
    void testSubmitMatchResult() throws Exception {
        mockMvc.perform(post("/tournaments/1/participants/submit-result")
                .param("participantId", "2")
                .param("won", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetParticipants() throws Exception {
        Mockito.when(participationService.getParticipantsByTournament(eq(1L)))
                .thenReturn(Collections.singletonList(mockParticipation));

        mockMvc.perform(get("/tournaments/1/participants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetParticipation() throws Exception {
        Mockito.when(participationService.getParticipation(eq(1L), eq(2L)))
                .thenReturn(Optional.of(mockParticipation));

        mockMvc.perform(get("/tournaments/1/participants/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}