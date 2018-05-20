package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;
import es.ucm.fdi.util.Describable;

/**
 * Clase con métodos abstractos que sirve de base
 * para cualquier evento del simulador.
 */
public abstract class Event implements Describable {

	/**
	 * Array estático con los nombres de las columnas de
	 * la {@code SimTable} de {@code Junction}s.
	 */
	public static String[] descriptionCols = {
		"#", "Time", "Type"
	};
	
	/**
	 * Tiempo de ejecución del evento.
	 */
	private int time;

	/**
	 * Constructor de {@link Event}.
	 * 
	 * @param newTime 	- tiempo de ejecución 
	 * 					del evento
	 */
	public Event(int newTime) {
		time = newTime;
	}

	/**
	 * Ejecuta el {@code Event} en la simulación {@code sim}.
	 * 
	 * @param sim 	- simulación en la que se
	 * 				ejecuta el evento
	 */
	public abstract void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException, NonExistingSimObjException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void describe(Map<String, Object> out) {
		// Inclusión en el mapa.
		String time = Integer.toString(this.time);
		String description = getEventDescription();
		out.put(descriptionCols[1], time);
		out.put(descriptionCols[2], description);
	}

	protected abstract String getEventDescription();

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
