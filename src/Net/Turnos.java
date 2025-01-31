package Net;

public class Turnos {
    private final int maxTurnos;
    private int turnoActual;
    private final int maxJugadores;
    private int jugadorActual;

    public Turnos(int maxTurnos, int maxJugadores) {
        this.maxTurnos = maxTurnos;
        this.turnoActual = 1;
        this.maxJugadores = maxJugadores;
        this.jugadorActual = 1;
    }

    public synchronized boolean siguienteTurno() {
        if (turnoActual < maxTurnos) {
            turnoActual++;
            return true;
        }
        return false; // No hay más turnos
    }

    public synchronized int getTurnoActual() {
        return turnoActual;
    }

    public synchronized int getJugadorActual() {
        return jugadorActual;
    }

    public synchronized void cambiarJugador() {
        if (++jugadorActual > maxJugadores) {
            jugadorActual = 1;
        }
    }

    public synchronized boolean esUltimoTurno() {
        return turnoActual >= maxTurnos;
    }
}
