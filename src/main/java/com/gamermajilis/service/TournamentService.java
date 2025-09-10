/**
Responsibilities:
1. Create, update, delete tournaments
2. Check if a tournament can be modified
3. Add/remove moderators
4. Increment view counts or manage stats
5. Fetch tournaments (by status, organizer, etc.)
**/

package com.gamermajilis.service;

import com.gamermajilis.model.Tournament;

import java.util.List;
import java.util.Optional;

public interface TournamentService {

    Tournament createTournament(Tournament tournament);

    Tournament updateTournament(Long id, Tournament tournament);

    void deleteTournament(Long id);

    Optional<Tournament> getTournamentById(Long id);

    List<Tournament> getAllTournaments();

    List<Tournament> getTournamentsByOrganizer(Long organizerId);

    List<Tournament> getTournamentsByStatus(String status);

    void addModerator(Long tournamentId, Long moderatorId);

    void incrementViewCount(Long tournamentId);
}
