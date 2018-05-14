package es.ucm.fdi.model.simulation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.SimObject;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.util.EventType;
import es.ucm.fdi.util.MultiTreeMap;

/**
 * Clase que representa el simulador de tráfico, almacenando 
 * los {@link Event Events} de la simulación, así como los 
 * objetos de simulación en un {@link RoadMap}, y el 
 * tiempo de actual de la simulación.
 */
public class TrafficSimulation {

	
	// ** CLASES INTERNAS (PARA EVENTOS) ** //
	public interface Listener {
		void update(UpdateEvent ue, String error);
	}
	
	public class UpdateEvent {
		
		EventType event;
		
		public UpdateEvent(EventType ev){
			event = ev;
		}
		
		public EventType getEvent() {
			return event;
		}
		
		public RoadMap getRoadMap() {
			return roadMap;
		}
		
		public MultiTreeMap<Integer, Event> getEventQueue() {
			return events;
		}
		
		public int getCurrentTime() {
			return time;
		}
	}

	// ** ATRIBUTOS ** //
	/**
	 * Mapa de eventos donde: {@code Integer} representa 
	 * el tiempo de ejecución de un evento, {@code Event} 
	 * para añadir listas de eventos que se ejecutan en 
	 * ese tiempo.
	 */
	private MultiTreeMap<Integer, Event> events = new MultiTreeMap<>();

	/**
	 * Lista con los {@code Listener}s registrados en el simulador.
	 */
	private List<Listener> listeners = new ArrayList<>();

	/**
	 * Mapa de simulación.
	 */
	RoadMap roadMap = new RoadMap();

	/**
	 * Tiempo actual de la simulación.
	 */
	private int time = 0;
	
	// ** CONSTRUCTOR ** //
	/**
	 * Constructor vacío del simulador.
	 */
	public TrafficSimulation() {
		/*NADA*/
	}
	
	//** MÉTODO DE EJECUCIÓN DE LA SIMULACIÓN (+ COMPLEMENTARIOS) ** //
	/**
	 * Simula un número determinado de ticks y guarda 
	 * el fichero de salida de esta ejecución.
	 * 
	 * @param steps 	- número de pasos a ejecutar
	 * @param file 		- fichero de salida
	 *
	 * @throws IOException	if an IO error ocurred during
	 * 						reports generation
	 */
	public void execute(int steps, OutputStream file) 
			throws IOException {

		// * //
		// Tiempo límite en que para la simulación.
		int timeLimit = time + steps - 1;

		// ** //
		// Bucle de la simulación.
		while (time <= timeLimit) {
			// 1 // EVENTOS //
			// Se ejecutan los eventos correspondientes a ese tiempo.			
			try {
				executeEvents();
			} catch (SimulationException e1) {
				fireUpdateEvent(EventType.ERROR, e1.getMessage());
				break;
			}
			
			// 2 // SIMULACIÓN //
			proceedAll();
			
			//Aviso a Listeners de avance
			fireUpdateEvent(EventType.ADVANCED, "Advanced error");
			
			// Se avanza un tick.
			time++;

			// 3 // INFORME //
			// Escribir un informe en OutputStream en 
			// caso de que no sea nulo
			try {
				generateReports(file);
			}
			catch (IOException e) {
				throw e;
			}

		}
	}

	/**
	 * Llama a los métodos de avance de {@Road}s
	 * y de {@Junction}s.
	 * 
	 * @throws SimulationException	if event tried to create
	 * 								a {@code SimObject} with 
	 * 								an already existing ID
	 * @throws SimulationException	if event tried to interact
	 * 								with a non-existint 
	 * 								{@code SimObject}
	 */
	private void executeEvents() throws SimulationException{
		if ( events.get(time) != null ) {
			for ( Event event : events.get(time) ) {
				try {
					event.execute(this);
					
				}
				catch (AlreadyExistingSimObjException e1) {
					throw new SimulationException(
						"Simulation error:\n" + e1.getMessage()
					);
				}
				catch (NonExistingSimObjException e2) {
					throw new SimulationException(
						"Simulation error:\n" + e2.getMessage()
					);
				}		
			}
		}
	}

	/**
	 * Llama a los métodos de avance de {@Road}s
	 * y de {@code Junction}s.
	 */
	private void proceedAll(){
		// Para cada carretera, los coches que no están 
		// esperando avanzan.
		for ( Road road : roadMap.getRoads().values() ) {
			road.proceed();
		}

		// Para cada cruce, avanzan los vehículos a la espera que 
		// puedan y se actualiza el semáforo y los tiempos de 
		// avería de los vehículos a la espera.
		for ( Junction junction : roadMap.getJunctions().values() ) {
			junction.proceed();			
		}
	}

	/**
	 * Genera informes de todos los {@code SimObject}s.
	 * 
	 * @param file 	- fichero de salida
	 */
	private void generateReports(OutputStream file) 
			throws IOException {
		
		if (file != null) {
			Ini iniFile = generateIniReports();
			
			// Guardado en el outputStream
			try{
				iniFile.store(file);
			}
			catch (IOException e) {
				throw new IOException(
					"Error when saving file on time " + time + ":" + 
					e.getMessage()
				);
			}
		}
	}
	
	/**
	 * Añade un {@code Event} al mapa de {@code Event}s 
	 * de la simulación, comprobando que el tiempo del 
	 * {@code Event} sea mayor que el de la simulación.
	 * 
	 * @param e 	- {@code Event} a añadir
	 * 
	 * @throws IllegalArgumentException		if event time lower 
	 * 										than sim time
	 */
	public void pushEvent(Event e) 
			throws IllegalArgumentException {
		
		// Comprueba el tiempo.
		if( e.getTime() < time ) {
			throw new IllegalArgumentException(
				"Event time is lower than current time."
			);
		}

		// Añade el evento al mapa.
		events.putValue(e.getTime(), e);
		fireUpdateEvent(EventType.NEW_EVENT, "New Event error.");
	}
	
	// ** MÉTODOS DE GENERACIÓN DE INFORMER ** //
	/**
	 * Genera un string con informes de los {@code SimObject}s 
	 * en la lista. Utilizdo para cargar el informe generado 
	 * en el área de texto de la {@code GUI}.
	 * 
	 * @return	{@code String} con informes creados
	 */
	public String reportsToString(List<SimObject> objectsToReport) {
		Ini iniFile = generateIniReports(objectsToReport);
		
		return iniFile.toString();
	}
	
	/**
	 * Genera un {@code Ini} con informes de todos 
	 * los {@code SimObject}s
	 * 
	 * @return 	{@code Ini} con informes creados
	 */
	private Ini generateIniReports(){
		//Creación de ini
		Ini iniFile = new Ini();
		//Junctions:
		for (Junction junction : roadMap.getJunctions().values() ) {
			iniFile.addsection(junction.generateIniSection(time));
		}
		//Roads:
		for (Road road : roadMap.getRoads().values() ) {
			iniFile.addsection(road.generateIniSection(time));
		}
		//Vehicles:
		for (Vehicle vehicle : roadMap.getVehicles().values() ) {
			iniFile.addsection(vehicle.generateIniSection(time));
		}
		return iniFile;
	}

	/**
	 * Genera un {@code .ini} con informes de los
	 * {@code SimObject}s que recibe como argumento.
	 * 
	 * @return 	{@code Ini} con los informes creados
	 */
	private Ini generateIniReports(List<SimObject> objectsToReport) {
		// Creación de ini
		Ini iniFile = new Ini();
		// SimObjects
		for (SimObject obj : objectsToReport ) {
			iniFile.addsection(obj.generateIniSection(time));
		}

		return iniFile;
	}

	// ** MÉTODOS DE MODIFICACIÓN DE LA SIMULACIÓN ** //
	/**
	 * Añade tiempo de avería a los {@code Vehicle}s con 
	 * los ID de la lista. Además comprueba que existan 
	 * los {@code Vehicle}s referenciados por esos IDs.
	 * 
	 * @param vehiclesID 	- lista de IDs de los 
	 * 						{@code Vehicle}s a 
	 * 						averiar
	 * @param breakDuration - duración del tiempo 
	 * 						de avería a añadir
	 */
	public void makeFaulty(List<String> vehiclesID, int breakDuration) 
			throws NonExistingSimObjException {

		for ( String id : vehiclesID ) {
			Vehicle toBreak = roadMap.getVehicleWithID(id);

			if ( toBreak != null ) {
				toBreak.setBreakdownTime(breakDuration);
			}
			else {
				throw new NonExistingSimObjException(
					"Vehicle with id: " + id + " to make faulty not found."
				);
			}
		}
	}

	/**
	 * Añade un {@code Vehicle} al {@code RoadMap}.
	 * 
	 * @param newVehicle 	- {@code Vehicle} a añadir
	 */

	public void addVehicle(Vehicle newVehicle) {
		// Se guarda en el inventario de objetos de simulación.
		roadMap.addVehicle(newVehicle);
	}

	/**
	 * Añade una {@code Road} al {@code RoadMap}.
	 * 
	 * @param newRoad 	- {@code Road} a añadir
	 */
	public void addRoad(Road newRoad) {
		// Se mete en el RoadMap.
		roadMap.addRoad(newRoad);
	}

	/**
	 * Añade una {@code Junction} al {@code RoadMap}.
	 * 
	 * @param newJunction 	- {@code Junction} a añadir
	 */
	public void addJunction(Junction newJunction) {
		// Se mete en el RoadMap
		roadMap.addJunction(newJunction);
	}

	// ** MÉTODOS DE LISTENERS ** //
	/**
	 *  Añade un {@code Listener} a la lista 
	 * (además, implementa {@code REGISTERED}).
	 * 
	 *  @param l 	{@code Listener} a añadir
	 */
	public void addSimulatorListener(Listener l) {
		listeners.add(l);
		UpdateEvent ue = new UpdateEvent(EventType.REGISTERED);
		// evita pseudo-recursividad
		// Error?
		SwingUtilities.invokeLater(() -> l.update(ue, "Registered error."));
	}

	/**
	 *  Elimina un {@code Listener} de la lista.
	 * 
	 *  @param l 	{@code Listener} a eliminar
	 */
	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	/**
	 * Método de uso interno que informa a todos 
	 * los {@code Listener}s registrados de un 
	 * {@code EventType} en simulación.
	 */
	private void fireUpdateEvent(EventType type, String error) {
		UpdateEvent ue = new UpdateEvent(type);
		for (Listener l : listeners) {
			l.update(ue, error);
		}
	}

	/**
	 * Reinicia el simulador, borrando los eventos
	 * y el mapa.
	 */
	public void reset() {
		events.clear();
		roadMap.clear();
		time = 0;
		fireUpdateEvent(EventType.RESET, "Reset error");
	}
	
	// ** MÉTODOS DE ACCESO ** //
	/**
	 * Devuelve el mapa de la simulación.
	 * 
	 * @return 	{@code RoadMap} con el mapa
	 */
	public RoadMap getRoadMap() {
		return roadMap;
	}

	/**
	 * Devuelve el tiempo actual de la simulación.
	 * 
	 * @return 	{@code int} con tiempo actual
	 */
	public int getCurrentTime() {
		return time;
	}

	/**
	 * Devuelve el listado de eventos de la 
	 * simulación.
	 * 
	 * @return 	{@code MultiTreeMap} con el 
	 * 			listado de eventos
	 */
	public MultiTreeMap<Integer, Event> getEvents() {
		return events;
	}

}
