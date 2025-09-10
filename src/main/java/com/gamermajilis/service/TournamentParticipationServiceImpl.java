package com.gamermajilis.service;

import com.gamermajilis.model.ParticipationStatus;
import com.gamermajilis.model.Tournament;
import com.gamermajilis.model.TournamentParticipation;
import com.gamermajilis.model.User;
import com.gamermajilis.repositories.TournamentParticipationRepository;
import com.gamermajilis.repositories.TournamentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentParticipationServiceImpl implements TournamentParticipationService {

    private final TournamentParticipationRepository participationRepository;
    private final TournamentRepository tournamentRepository;

    public TournamentParticipationServiceImpl(TournamentParticipationRepository participationRepository, TournamentRepository tournamentRepository) {
        this.participationRepository = participationRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public TournamentParticipation registerParticipant(Long tournamentId, Long participantId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        
        // Ensure tournament is open for registration
        if (!tournament.isRegistrationOpen()) {
            throw new RuntimeException("Registration for this tournament is closed.");
        }

        // Check if the tournament is full
        if (tournament.isFull()) {
            throw new RuntimeException("Tournament is full.");
        }

        // Check if the user is already registered
        Optional<TournamentParticipation> existingParticipation = participationRepository.findByTournamentIdAndParticipantId(tournamentId, participantId);
        if (existingParticipation.isPresent()) {
            throw new RuntimeException("User is already registered for this tournament.");
        }

        // Create a new tournament participation
        TournamentParticipation participation = new TournamentParticipation();
        participation.setTournament(tournament);
        participation.setParticipant(new User(participantId)); // Assuming the User is being handled properly.
        participation.setRegistrationDate(LocalDateTime.now());
        participation.setStatus(ParticipationStatus.REGISTERED);
        
        // Save the participation
        tournament.incrementParticipantCount();
        tournamentRepository.save(tournament);
        
        return participationRepository.save(participation);
    }

    @Override
    public void checkInParticipant(Long tournamentId, Long participantId) {
        TournamentParticipation participation = participationRepository.findByTournamentIdAndParticipantId(tournamentId, participantId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
        
        // Check if participant is already checked in
        if (participation.getCheckedIn()) {
            throw new RuntimeException("Participant is already checked in.");
        }
        
        participation.checkIn();
        participationRepository.save(participation);
    }

    @Override
    public void disqualifyParticipant(Long tournamentId, Long participantId, String reason) {
        TournamentParticipation participation = participationRepository.findByTournamentIdAndParticipantId(tournamentId, participantId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
        
        // If participant is already disqualified, don't proceed
        if (participation.getDisqualified()) {
            throw new RuntimeException("Participant is already disqualified.");
        }
        
        participation.disqualify(reason);
        participationRepository.save(participation);
    }

    @Override
    public void submitMatchResult(Long tournamentId, Long participantId, boolean won) {
        TournamentParticipation participation = participationRepository.findByTournamentIdAndParticipantId(tournamentId, participantId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
        
        if (participation.getStatus() != ParticipationStatus.CONFIRMED) {
            throw new RuntimeException("Participant must be confirmed to submit results.");
        }
        
        participation.addMatchResult(won);
        participationRepository.save(participation);
    }

    @Override
    public List<TournamentParticipation> getParticipantsByTournament(Long tournamentId) {
        return participationRepository.findByTournamentId(tournamentId);
    }

    @Override
    public Optional<TournamentParticipation> getParticipation(Long tournamentId, Long participantId) {
        return participationRepository.findByTournamentIdAndParticipantId(tournamentId, participantId);
    }
}
