package com.gamermajilis.service;

import com.gamermajilis.model.TournamentParticipation;

import java.util.List;
import java.util.Optional;

public interface TournamentParticipationService {

    TournamentParticipation registerParticipant(Long tournamentId, Long participantId);

    void checkInParticipant(Long tournamentId, Long participantId);

    void disqualifyParticipant(Long tournamentId, Long participantId, String reason);

    void submitMatchResult(Long tournamentId, Long participantId, boolean won);

    List<TournamentParticipation> getParticipantsByTournament(Long tournamentId);

    Optional<TournamentParticipation> getParticipation(Long tournamentId, Long participantId);
}
