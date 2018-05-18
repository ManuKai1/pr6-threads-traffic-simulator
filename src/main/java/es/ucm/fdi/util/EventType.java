package es.ucm.fdi.util;

/**
 * Tipo enumerado que representa los distintos tipos
 * de eventos que la simulación puede notificar a 
 * sus {@link Listener}s.
 * Registered: se ha registrado un nuevo listener.
 * Reset: se ha reseteado el simulador
 * New event: se ha añadido un nuevo evento.
 * Advanced: la simulación ha avanzado.
 * Error: ha ocurrido un error en la simulación.
 */
public enum EventType {
	REGISTERED,
	RESET,
	NEW_EVENT,
	ADVANCED,
	ERROR;
}
