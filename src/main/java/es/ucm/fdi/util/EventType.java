package es.ucm.fdi.util;

/**
 * Tipo enumerado que representa los distintos tipos
 * de eventos que la simulación puede notificar a 
 * sus {@link Listener}s.
 */
public enum EventType {
	REGISTERED,
	RESET,
	NEW_EVENT,
	ADVANCED,
	ERROR;
}
