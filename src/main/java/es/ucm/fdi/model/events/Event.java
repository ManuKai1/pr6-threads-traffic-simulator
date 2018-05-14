package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;
import es.ucm.fdi.util.Describable;
import es.ucm.fdi.util.TableDataType;

/**
 * Clase con métodos abstractos que sirve de base
 * para cualquier evento del simulador.
 */
public abstract class Event implements Describable {
	
	// ** ATRIBUTOS ** //
	/**
	 * Tiempo de ejecución del evento.
	 */
	private int time;

	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link Event}.
	 * 
	 * @param newTime 	- tiempo de ejecución 
	 * 					del evento
	 */
	public Event(int newTime) {
		time = newTime;
	}

	// ** MÉTODO DE EJECUCIÓN ** //
	/**
	 * Ejecuta el {@code Event} en la simulación {@code sim}.
	 * 
	 * @param sim 	- simulación en la que se
	 * 				ejecuta el evento
	 */
	public abstract void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException, NonExistingSimObjException;

	// ** MÉTODOS DE DESCRIPCIÓN ** //
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void describe(Map<TableDataType, Object> out) {
		// Inclusión en el mapa.
		String time = Integer.toString(this.time);
		String description = getEventDescription();
		out.put(TableDataType.E_TIME, time);
		out.put(TableDataType.E_TYPE, description);
	}

	protected abstract String getEventDescription();

	// ** MÉTODO DE COMPARACIÓN ** //
	/**
	 * Comprueba si el {@code Event} es igual a 
	 * un objeto dado {@code obj}.
	 * 
	 * @param obj 	- objeto a comparar
	 * 
	 * @return 	if {@code Event} equals {@code obj}
	 */
	@Override
	public boolean equals(Object obj) {
		// Mismo evento.
		if (this == obj) {
			return true;
		}

		// 'obj' no es nada.
		if (obj == null) {
			return false;
		}

		// Clases distintas.
		if (getClass() != obj.getClass()) {
			return false;
		}

		// Comparación del tiempo de ejecución.
		Event other = (Event) obj;
		return (time == other.getTime());
	}

	// ** GETTERS/SETTERS ** //
	/**
	 * Devuelve el tiempo en que se ejecutará 
	 * el {@code Event}.
	 * 
	 * @return 	tiempo de ejecución
	 * 			{@code Event}
	 */
	public int getTime() {
		return time;
	}
}
