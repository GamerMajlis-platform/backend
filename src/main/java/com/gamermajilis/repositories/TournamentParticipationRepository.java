package com.gamermajilis.repositories;

import com.gamermajilis.model.TournamentParticipation;
import com.gamermajilis.model.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentParticipationRepository extends JpaRepository<TournamentParticipation, Long> {
    
    // Find participation by tournament and participant
    Optional<TournamentParticipation> findByTournamentIdAndParticipantId(Long tournamentId, Long participantId);
    
    // List all participants of a tournament
    List<TournamentParticipation> findByTournamentId(Long tournamentId);
    
    // List all tournaments a user participates in
    List<TournamentParticipation> findByParticipantId(Long participantId);
    
    // List participants by status (e.g., confirmed)
    List<TournamentParticipation> findByTournamentIdAndStatus(Long tournamentId, ParticipationStatus status);
}
