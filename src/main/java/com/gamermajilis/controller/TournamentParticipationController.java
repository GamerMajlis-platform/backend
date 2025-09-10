package com.gamermajilis.controller;

import com.gamermajilis.model.TournamentParticipation;
import com.gamermajilis.service.TournamentParticipationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
/**
 This controller will handle the endpoints related to participant actions:
* Register for Tournament
* Check-in
* Disqualify Participant
* Submit Match Result
* View Participants
 */



@RestController
@RequestMapping("/tournaments/{tournamentId}/participants")
public class TournamentParticipationController {

    private final TournamentParticipationService participationService;

    public TournamentParticipationController(TournamentParticipationService participationService) {
        this.participationService = participationService;
    }

    // Register participant for a tournament
    @PostMapping("/register")
    public ResponseEntity<TournamentParticipation> registerParticipant(@PathVariable Long tournamentId, @RequestParam Long participantId) {
        TournamentParticipation participation = participationService.registerParticipant(tournamentId, participantId);
        return new ResponseEntity<>(participation, HttpStatus.CREATED);
    }

    // Check-in participant
    @PostMapping("/check-in")
    public ResponseEntity<Void> checkInParticipant(@PathVariable Long tournamentId, @RequestParam Long participantId) {
        participationService.checkInParticipant(tournamentId, participantId);
        return ResponseEntity.ok().build();
    }

    // Disqualify participant
    @PostMapping("/disqualify")
    public ResponseEntity<Void> disqualifyParticipant(@PathVariable Long tournamentId, @RequestParam Long participantId, @RequestParam String reason) {
        participationService.disqualifyParticipant(tournamentId, participantId, reason);
        return ResponseEntity.ok().build();
    }

    // Submit match result
    @PostMapping("/submit-result")
    public ResponseEntity<Void> submitMatchResult(@PathVariable Long tournamentId, @RequestParam Long participantId, @RequestParam boolean won) {
        participationService.submitMatchResult(tournamentId, participantId, won);
        return ResponseEntity.ok().build();
    }

    // Get all participants of a tournament
    @GetMapping
    public ResponseEntity<List<TournamentParticipation>> getParticipants(@PathVariable Long tournamentId) {
        List<TournamentParticipation> participants = participationService.getParticipantsByTournament(tournamentId);
        return ResponseEntity.ok(participants);
    }

    // Get specific participation details
    @GetMapping("/{participantId}")
    public ResponseEntity<TournamentParticipation> getParticipation(@PathVariable Long tournamentId, @PathVariable Long participantId) {
        Optional<TournamentParticipation> participation = participationService.getParticipation(tournamentId, participantId);
        return participation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
