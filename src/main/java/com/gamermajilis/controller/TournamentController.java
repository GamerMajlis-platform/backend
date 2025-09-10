package com.gamermajilis.controller;
import org.springframework.web.bind.annotation.*;

import com.gamermajilis.model.Tournament;
import com.gamermajilis.service.TournamentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    // Create tournament
    @PostMapping
    public ResponseEntity<Tournament> createTournament(@Valid @RequestBody Tournament tournament) {
        Tournament createdTournament = tournamentService.createTournament(tournament);
        return new ResponseEntity<>(createdTournament, HttpStatus.CREATED);
    }

    // Update tournament
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @Valid @RequestBody Tournament tournament) {
        Tournament updatedTournament = tournamentService.updateTournament(id, tournament);
        return new ResponseEntity<>(updatedTournament, HttpStatus.OK);
    }

    // Delete tournament
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

    // Get tournament details
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentService.getTournamentById(id);
        return tournament.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all tournaments
    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    // Get tournaments by organizer
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Tournament>> getTournamentsByOrganizer(@PathVariable Long organizerId) {
        List<Tournament> tournaments = tournamentService.getTournamentsByOrganizer(organizerId);
        return ResponseEntity.ok(tournaments);
    }

    // Add a moderator to a tournament
    @PostMapping("/{id}/moderators")
    public ResponseEntity<Void> addModerator(@PathVariable Long id, @RequestParam Long moderatorId) {
        tournamentService.addModerator(id, moderatorId);
        return ResponseEntity.ok().build();
    }

    // Increment tournament view count
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        tournamentService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }
}
/**
 This controller will handle the endpoints related to tournament management:
* Create Tournament
* Update Tournament
* Delete Tournament
* View Tournament Details
* Add Moderator
* Increment View Count
**/