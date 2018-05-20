package es.ucm.fdi.model.simobj;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

/**
 * Clase que representa un cruce como un objeto
 * de simulación. Hereda de {@link SimObject}
 */
public class Junction extends SimObject {

	/**
	 * Array estático con los nombres de las columnas de
	 * la {@code SimTable} de {@code Junction}s.
	 */
	public static String[] descriptionCols = {
		"Report", "ID",
		"Type", "Green", "Red"
	};
	
	/**
	 * Etiqueta que encabeza el informe de una
	 * {@code Junction} cualquiera.
	 */
	protected final String REPORT_TITLE = "[junction_report]";

	/**
	 * Mapa de {@code Road}s entrantes
	 * en la {@code Junction}.
	 */
	protected Map<String, Road> incomingRoads = new LinkedHashMap<>();

	/**
	 * Mapa de {@code Road}s salientes 
	 * en la {@code Junction}.
	 */
	protected Map<String, Road> exitRoads = new LinkedHashMap<>();

	/**
	 * Entero que mediante la operación módulo
	 * representa el semáforo encendido.
	 */
	protected int light = -1;	

	/**
	 * Constructor de {@link Junction}.
	 * 
	 * @param identifier identificador del objeto
	 */
	public Junction(String identifier) {
		super(identifier);
	}

	/**
	 * {@inheritDoc}
	 * Método de AVANCE de {@code Junction}.
	 * <p>
	 * En la primera iteración tras la creación de 
	 * la {@code Junction}, se produce la primera 
	 * actualización del semáforo con {@link #firstLightUpdate()}
	 * </p> <p>
	 * En primer lugar, se actualiza en {@link #roadUpdate(Road)}
	 * la cola de la {@code greenRoad} con el semáforo en verde.
	 * </p> <p>
	 * En segundo lugar, se actualiza el tiempo de avería de los 
	 * {@code Vehicle}s averiados en la cola de espera con 
	 * {@link #refreshWaiting()}
	 * </p> <p> 
	 * Finalmente, se actualiza el semáforo de la {@code Junction}
	 * mediante {@link #lightUpdate()}
	 * </p>
	 */
	@Override
	public void proceed() {
		// Si no tiene carreteras entrantes no necesita
		// ningún control de semáforo
		if ( hasIncomingRoads() ) {
			if (light == -1) {			
				// * //
				// Primera actualización del semáforo.
				firstLightUpdate();
			}
			else {
				// 1 //
				// Actualización de la cola de la Road con el semáforo en verde.
				List<String> array = new ArrayList<>(incomingRoads.keySet());
				String nextRoadID = array.get(light);
				Road greenRoad = incomingRoads.get(nextRoadID);

				roadUpdate(greenRoad);
				
				// 2 //
				// Actualización del tiempo de avería de los coches de la cola.
				greenRoad.refreshWaiting();

				// 3 //
				// Actualización del semáforo.
				lightUpdate();		
			}	
		}	
	}

	/**
	 * Comprueba si la {@code Junction}
	 * tiene {@code Road}s entrantes.
	 * 
	 * @return 	si el número de mapeos clave-valor de 
	 * 			{@code incomingRoads} no es nulo
	 */
	private boolean hasIncomingRoads() {
		return (incomingRoads.size() > 0);
	}

	/**
	 * Actualiza el semáforo en el primer tick 
	 * de la simulación.
	 */
	protected void firstLightUpdate() {
		light = 0; // Suponemos que hay al menos una carretera entrante
		
		List<String> array = new ArrayList<>(incomingRoads.keySet());
		String nextRoadID = array.get(light);
		
		// El semáforo de la carretera se pone verde.
		incomingRoads.get(nextRoadID).setLight(true);
	}

	/**
	 * Actualiza la cola de la {@code Road} 
	 * con el semáforo en verde.
	 * 
	 * @param greenRoad 	{@code Road} con 
	 * 						el semáforo en verde
	 */
	protected void roadUpdate(Road greenRoad) {
		// Si no hay vehículos esperando, no ocurre nada.
		// Si hay vehículos en la cola, se intenta mover uno.
		if ( ! greenRoad.noVehiclesWaiting() ) {
			try {
				// El primer vehículo de la cola puede estar averiado:
				// no ocurre nada, cruzará cuando se agote su breakdownTime.
				greenRoad.moveWaitingVehicle();
			} catch (SimulationException e) {
				// Si la greenRoad está en rojo, se captura la excepción.
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Actualiza el semáforo de la {@code Junction}.
	 */
	protected void lightUpdate() {
		// Tomamos la carretera usada
		List<String> array = new ArrayList<>(incomingRoads.keySet());
		String nextRoad = array.get(light);
		Road usedRoad = incomingRoads.get(nextRoad); // Carretera actualizada

		// * //
		// La carretera actualizada se pone en rojo.
		usedRoad.setLight(false);

		// 1 //
		// El semáforo avanza a la siguiente carretera.
		int numIncomingRoads = incomingRoads.size();
		light = (light + 1) % numIncomingRoads;

		// 2 // 
		// La siguiente carretera se pone en verde.
		nextRoad = array.get(light);
		incomingRoads.get(nextRoad).setLight(true);
	}

	/**
	 * Genera una {@code IniSection} que informa de los 
	 * atributos de la {@code Junction} en el 
	 * tiempo del simulador.
	 * 
	 * @param simTime 	- tiempo del simulador
	 * 
	 * @return 			{@code IniSection} con información
	 * 					de la {@code Junction}
	 */
	public IniSection generateIniSection(int simTime) {
		// 1 //
		// Se crea la etiqueta de la sección (sin corchetes).
		String tag = REPORT_TITLE;
		tag = (String) tag.subSequence(1, tag.length() - 1);
		IniSection section = new IniSection(tag);

		// 2 // 
		// Se generan los datos en el informe.
		section.setValue("id", id);
		section.setValue("time", simTime);
		section.setValue("queues", getQueuesValue() );


		return section;
	}

	/**
	 * Genera un {@code StringBuilder} con la información
	 * sobre las colas de la {@code Junction}.
	 * 
	 * @return 	{@code String} con las colas.
	 */
	protected String getQueuesValue() {
		// Generación del string de queues
		StringBuilder queues = new StringBuilder();
		for (Road incR : incomingRoads.values() ) {
			queues.append(incR.getWaitingState());
			queues.append(",");
		}

		// Borrado de última coma (si queues no es vacío).
		if (queues.length() > 0) {
			queues.deleteCharAt(queues.length() - 1);
		}

		return queues.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * Añade una {@code Junction} al mapa, con los
	 * datos: id, tipo, colas de verde, colas de rojo.
	 * 
	 * @param out {@inheritDoc}
	 */
	@Override
	public void describe(Map<String, Object> out) {
		String green = getGreenDescription();
		String red = getRedDescription();
		
		out.put(descriptionCols[1], id);
		out.put(descriptionCols[2], getType());
		out.put(descriptionCols[3], green);
		out.put(descriptionCols[4], red);
	}

	/**
	 * <p>
	 * Devuelve un {@code String} con el estado de 
	 * la cola de espera de la {@code Road} con el 
	 * semáforo en verde.
	 * </p> <p>
	 * Ejemplo:
	 * </p> <p>
	 * [(r2,green,[v3,v6,v8])]
	 * </p>
	 * 
	 * @return	{@code String} con el estado de la
	 * 			{@code Road} en verde
	 */
	protected String getGreenDescription() {
		StringBuilder green = new StringBuilder();

		// Sólo hay una con el semáforo en verde.
		green.append("[");
		for (Road incR : incomingRoads.values()) {
			if ( incR.isGreen() ) {
				green.append( incR.getWaitingState() );
			}
		}
		green.append("]");

		return green.toString();
	}

	/**
	 * <p>
	 * Devuelve un {@code String} con el estado de 
	 * la cola de espera de las {@code Road}s con el 
	 * semáforo en rojo.
	 * </p> <p>
	 * Ejemplo:
	 * </p> <p>
	 * [(r2,red,[v3,v6,v8]),(r4,red,[v5])]
	 * </p>
	 * 
	 * @return	{@code String} con el estado de la
	 * 			{@code Road} en rojo
	 */
	protected String getRedDescription() {
		StringBuilder red = new StringBuilder();

		// Sólo hay una con el semáforo en verde.
		red.append("[");
		for ( Road incR : incomingRoads.values() ) {
			if ( ! incR.isGreen() ) {
				red.append( incR.getWaitingState() );
			}
		}
		red.append("]");

		return red.toString();
	}
	
	/**
	 * Devuelve la Mapa de {@code Road}s entrantes
	 * 
	 * @return Mapa de {@code Road}s entrantes.
	 */
	public Map<String, Road> getIncomingRoads() {
		return incomingRoads;
	}

	/**
	 * Devuelve el Mapa de {@code Road}s salientes
	 * 
	 * @return mapa de {@code Road}s salientes
	 */
	public Map<String, Road> getExitRoads() {
		return exitRoads;
	}

	/**
	 * Añade una nueva {@code Road} de salida a la 
	 * {@code Junction}.
	 * 
	 * @param newRoad 	- nueva {@code Road} saliente
	 */
	public void addNewExitRoad(Road newRoad) {
		exitRoads.put(newRoad.getID(), newRoad);
	}

	/**
	 * Añade una nueva {@code Road} de entrada a la 
	 * {@code Junction}.
	 * 
	 * @param newRoad 	- nueva {@code Road} entrante
	 */
	public void addNewIncomingRoad(Road newRoad) {
		incomingRoads.put(newRoad.getID(), newRoad);
	}
	
	/**
	 * Método que devuelve la {@code Road} entre dos 
	 * {@code Junction}s. La junction de origen 
	 * es la actual.
	 * 
	 * @param toJunction 	- {@code Junction} de destino
	 * 
	 * @return 				{@code Road} entre las dos 
	 * 						{@code Junction}s
	 * 
	 * @throws SimulationException 	if {@code Road} between 
	 * 								{@code Junction}s not 
	 * 								found
	 */
	public Road getRoadTo(Junction junction) throws SimulationException {
		String toID;
		for (Entry<String, Road> exit : exitRoads.entrySet()) {
			toID = exit.getKey();
			if ( junction.incomingRoads.containsKey(toID) ) {
				return exitRoads.get(toID);
			}
		}
		
		throw new SimulationException(
			"Road not found between junctions with id: " + 
			id + ", " + junction.getID()
		);
	}

	/**
	 * Devuelve un {@code String} con el tipo de 
	 * {@code Junction} que debe ponerse como valor 
	 * en la clave {@code type}, tanto en la 
	 * {@code IniSection} generada en {@code batch} 
	 * como en la información mostrada en las 
	 * tablas de la {@code GUI}.
	 * 
	 * @return 	{@code String} con el 
	 * 			tipo de {@code Junction}
	 */
	protected String getType() {
		return "-";
	}
}
