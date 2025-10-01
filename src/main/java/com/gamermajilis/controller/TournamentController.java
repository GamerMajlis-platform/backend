package com.gamermajilis.controller;
import org.springframework.web.bind.annotation.*;

import com.gamermajilis.model.Tournament;
import com.gamermajilis.model.User;
import com.gamermajilis.repository.UserRepository;
import com.gamermajilis.service.TournamentService;
import com.gamermajilis.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private static final Logger logger = LoggerFactory.getLogger(TournamentController.class);

    private final TournamentService tournamentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    // Create tournament
    @PostMapping
    public ResponseEntity<Tournament> createTournament(@Valid @RequestBody Tournament tournament, HttpServletRequest request) {
        // Extract user ID from JWT token
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get the user from database and set as organizer
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        tournament.setOrganizer(userOpt.get());
        Tournament createdTournament = tournamentService.createTournament(tournament);
        return new ResponseEntity<>(createdTournament, HttpStatus.CREATED);
    }

    // Helper method to extract user ID from JWT token
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            logger.warn("Invalid token in request", e);
            return null;
        }
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