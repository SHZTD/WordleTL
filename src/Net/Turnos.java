package Net;

import java.util.ArrayList;
import java.util.List;

class Turnos {
    private final List<String> players;
    private int currentTurnIndex;

    public Turnos() {
        this.players = new ArrayList<>();
        this.currentTurnIndex = 0;
    }

    public void addPlayer(String playerId) {
        if (!players.contains(playerId)) {
            players.add(playerId);
        }
    }

    public boolean isPlayerTurn(String playerId) {
        return players.get(currentTurnIndex).equals(playerId);
    }

    public void nextTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % players.size();
    }
}