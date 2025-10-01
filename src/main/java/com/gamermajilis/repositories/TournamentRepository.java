package com.gamermajilis.repositories;

import com.gamermajilis.model.Tournament;
import com.gamermajilis.model.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    
    // Find all tournaments with a specific status
    List<Tournament> findByStatus(TournamentStatus status);
    
    // Find tournaments by organizer
    List<Tournament> findByOrganizerId(Long organizerId);
    
    // Search by name containing keyword (ignore case)
    List<Tournament> findByNameContainingIgnoreCase(String name);
    
    // Find all tournaments that are open for registration
    List<Tournament> findByStatusAndCurrentParticipantsLessThan(TournamentStatus status, Integer maxParticipants);

    // Find all non-deleted tournaments
    List<Tournament> findByDeletedAtIsNullOrderByCreatedAtDesc();
    
}

