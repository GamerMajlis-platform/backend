package com.gamermajilis.service;

import com.gamermajilis.model.Tournament;
import com.gamermajilis.model.TournamentStatus;
import com.gamermajilis.repositories.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament createTournament(Tournament tournament) {
        // Business rule: name must be unique is handled by DB constraint
        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament updateTournament(Long id, Tournament tournament) {
        Tournament existing = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        if (!existing.canModify()) {
            throw new RuntimeException("Tournament cannot be modified after it started");
        }

        existing.setName(tournament.getName());
        existing.setDescription(tournament.getDescription());
        existing.setGameTitle(tournament.getGameTitle());
        existing.setMaxParticipants(tournament.getMaxParticipants());
        existing.setStartDate(tournament.getStartDate());
        existing.setRules(tournament.getRules());
        existing.setRegulations(tournament.getRegulations());

        return tournamentRepository.save(existing);
    }

    @Override
    public void deleteTournament(Long id) {
        Tournament existing = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        existing.setDeletedAt(java.time.LocalDateTime.now());
        tournamentRepository.save(existing);
    }

    @Override
    public Optional<Tournament> getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findByDeletedAtIsNullOrderByCreatedAtDesc();
    }

    @Override
    public List<Tournament> getTournamentsByOrganizer(Long organizerId) {
        return tournamentRepository.findByOrganizerId(organizerId);
    }

    @Override
    public List<Tournament> getTournamentsByStatus(String status) {
        TournamentStatus ts = TournamentStatus.valueOf(status.toUpperCase());
        return tournamentRepository.findByStatus(ts);
    }

    @Override
    public void addModerator(Long tournamentId, Long moderatorId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        if (!tournament.getModeratorIds().contains(moderatorId)) {
            tournament.getModeratorIds().add(moderatorId);
            tournamentRepository.save(tournament);
        }
    }

    @Override
    public void incrementViewCount(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        tournament.incrementViewCount();
        tournamentRepository.save(tournament);
    }
}
