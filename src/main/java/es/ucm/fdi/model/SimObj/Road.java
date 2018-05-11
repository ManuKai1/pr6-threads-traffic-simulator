package es.ucm.fdi.model.SimObj;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.util.TableDataType;

/**
 * Clase que representa una carretera como un objeto
 * de simulación. Hereda de {@link SimObject}
 */
public class Road extends SimObject {
	
	// ** ATRIBUTOS ** //
	/**
	 * Etiqueta que encabeza el informe de una 
	 * {@code Road} cualquiera.
	 */
	protected static final String REPORT_TITLE = "[road_report]";
	
	/**
	 * Longitud de la {@code Road}.
	 */
	private int length;

	/**
	 * Límite de velocidad para los 
	 * {@code Vehicle}s en la {@code Road}.
	 */
	protected int speedLimit;

	/**
	 * {@code Junction}s donde empieza 
	 * la {@code Road}.
	 */
	private Junction fromJunction;

	/**
	 * {@code Junction}s donde acaba 
	 * la {@code Road}.
	 */
	private Junction toJunction;
	
	/**
	 * Lista de {@code Vehicle}s ordenada por orden 
	 * de entrada en la {@code Road}. Utilizada para 
	 * el caso en que dos {@code Vehicle}s se encuentran 
	 * en la misma posición.
	 */
	private Deque<Vehicle> entryRecord = new ArrayDeque<>();

	/**
	 * Lista de {@code Vehicle}s en la {@code Road} 
	 * que no están esperando a cruzar la {@code toJunction}.
	 */
	protected List<Vehicle> vehiclesOnRoad = new ArrayList<>();

	/**
	 * Lista temporal reutilizada en cada tick en la que 
	 * se ordenan los {@code Vehicle}s que llegan a 
	 * {@code toJunction} por tiempo de llegada.
	 */
	private List<ArrivedVehicle> arrivalsToWaiting = new ArrayList<>();

	/**
	 * Lista de {@code Vehicle}s en la {@code Road} 
	 * que están esperando para cruzar {@code toJunction}.
	 */
	private Deque<Vehicle> waiting = new ArrayDeque<>();

	/**
	 * Booleano que indica si el semáforo de la 
	 * {@code toJunction} está verde para la {@code Road}.
	 */
	private boolean isGreen = false;
	






	// ** COMPARADORES ** //
	/**
	 * Comparador según la localización de 2 {@code Vehicle}s
	 * en la {@code Road}, para ordenar {@code vehiclesOnRoad} 
	 * tras cada avance de los {@code Vehicle}s.
	 */
	private static class CompByLocation implements Comparator<Vehicle> {
		
		Deque<Vehicle> entries;		

		public CompByLocation(Road road) {
			entries = road.getEntryRecord();
		}

		@Override
		public int compare(Vehicle v1, Vehicle v2) {
			int dist = v2.getLocation() - v1.getLocation();

			if (dist != 0) {
				return dist;
			}
			else {
				// Están en la misma posición, se ordena por orden 
				// de entrada en carretera.
				for (Vehicle v : entries ) {
					if (v == v1) {
						return -1;
					}
					if (v == v2) {
						return 1;
					}
				}

				// Fallo del programa (no se debería dar)
				throw new RuntimeException(
					"Vehicles weren´t recorded when entered their road."
				);
			}
		}

		// ROADEND - (v1, 80) < (v2, 78) < (v3, 50) < (v4, 20) - ROADBEGIN
	}

	/**
	 * Comparador según el tiempo de llegada al final 
	 * de la {@code Road}, para ordenar los 
	 * {@code arrivedVehicle}s según su tiempo de llegada.
	 */
	private static class CompArrivedVehicles implements Comparator<ArrivedVehicle> {
		
		@Override
		public int compare(ArrivedVehicle av1, ArrivedVehicle av2) {
			// Si av1.time < av2.time -> av1 < av2
			float diff = av1.getTime() - av2.getTime();
			
			int intDiff;
			if ( diff < 0 ) intDiff = -1;
			else intDiff = 1;
			
			return intDiff;
		}

		// ROADEND - (v1, 0.1s) < (v2, 0.5s) < (v3, 2s) < (v4, 3s) - ROADBEGIN
	}







	// ** CLASE INTERNA ** //
	/**
	 * Clase interna que guarda cada {@code Vehicle} con 
	 * su tiempo de llegada al final de la {@code Road}.
	 */
	private class ArrivedVehicle {
		private Vehicle arrived;
		private float time;

		public ArrivedVehicle(Vehicle arr, float t) {
			arrived = arr;
			time = t;
		}

		public Vehicle getArrived() {
			return arrived;
		}
		
		public float getTime() {
			return time;
		}
	}











	// ** CONSTRUCTOR ** //	
	/**
	 * Constructor de {@link Road}.
	 * 
	 * @param identifier 	- identificador del objeto
	 * @param len 			- longitud de la vía
	 * @param spLimit 		- límite de velocidad
	 * @param fromJ 		- {@code Junction}s donde empieza
	 * @param toJ 			- {@code Junction}s donde acaba
	 */
	public Road(String identifier, int len, int spLimit, 
			Junction fromJ, Junction toJ) {
		super(identifier);
		length = len;
		speedLimit = spLimit;
		fromJunction = fromJ;
		toJunction = toJ;
		
		// Actualización de cruces afectados.
		getInOwnJunctions();	
	}

	
	
	
	
	
	
	
	// ** MÉTODO DE AVANCE (+ COMPLEMENTARIOS) ** //
	/**
	 * {@inheritDoc}
	 * Método de AVANCE de {@code Road}.
	 * <p>
	 * En primer lugar, modifica la velocidad que llevarán 
	 * los {@code Vehicle}s durante el avance, teniendo 
	 * en cuenta factores de la {@code Road}. 
	 * </p> <p>
	 * En segundo lugar, provoca el avance de los {@code Vehicle}s 
	 * en la {@code Road} y los reordena si ha habido adelantamientos. 
	 * </p> <p> 
	 * Finalmente, introduce a los {@code Vehicle}s que han llegado
	 * al final de la {@code Road} en la cola de espera 
	 * {@code waiting}.
	 * </p>
	 */
	@Override
	public void proceed() {
		// * //
		// Se crea lista con los vehículos en la 
		// carretera en ese momento, pues pueden salir 
		// durante su proceed y provocar un error en 
		// el foreach
		ArrayList<Vehicle> onRoad = new ArrayList<>();
		for (Vehicle v : vehiclesOnRoad) {
			onRoad.add(v);
		}

		// 1 //
		// Se modifica la velocidad a la que avanzarán los 
		// vehículos, teniendo en cuenta el factor de reducción.
		vehicleSpeedModifier(onRoad);

		// 2 //
		// Los vehículos avanzan y se pueden adelantar.
		for (Vehicle v : onRoad) {
			v.proceed();
		}
		vehiclesOnRoad.sort(new CompByLocation(this));

		// 3 //
		// Los coches que llegan al final entran 
		// por orden en la cola de espera.
		pushArrivalsToWaiting();
	}

	/**
	 * Modifica la velocidad que llevarán los {@code Vehicle}s
	 * en la {@code Road} previo avance.
	 * 
	 * @param onRoad 	- lista de {@code Vehicle}s 
	 * 					en {@code Road}
	 */
	protected void vehicleSpeedModifier(ArrayList<Vehicle> onRoad) {
		// Velocidad máxima a la que pueden avanzar los vehículos.
		int baseSpeed = getBaseSpeed();
		
		// Factor de reducción de velocidad en caso de obstáculos delante.
		int reductionFactor = 1;

		// Se modifica la velocidad a la que avanzarán los vehículos,
		// teniendo en cuenta el factor de reducción.
		for (Vehicle v : onRoad) {
			v.setSpeed(baseSpeed / reductionFactor);

			if (v.getBreakdownTime() > 0) {
				reductionFactor = 2;
			}
		}
	}

	/**
	 * Inserta los {@code Vehicle}s que han llegado al 
	 * final de la  {@code Road} en {@code waiting}, 
	 * ordenados por tiempo de llegada.
	 */
	public void pushArrivalsToWaiting() {
		// Se hace cuando han avanzado todos los coches.
		arrivalsToWaiting.sort(new CompArrivedVehicles());

		// Se insertan ordenados en la cola de espera.
		for (ArrivedVehicle av : arrivalsToWaiting) {
			waiting.addLast(av.getArrived());
		}

		// Se vacía el array para el siguiente tick
		arrivalsToWaiting.clear();
	}

	/**
	 * Calcula la velocidad base de la {@code Road}: 
	 * el mínimo entre el <code>speedLimit</code> y la 
	 * velocidad que permite la congestión del tráfico en
	 * la {@code Road}.
	 * 
	 * @return 	la velocidad base de 
	 * 			la {@code Road}.
	 */
	protected int getBaseSpeed() {
		// Cálculo de velocidadBase según la fórmula
		int congestionSpeed = ( speedLimit / Math.max(vehiclesOnRoad.size(), 1) ) + 1;

		return ( Math.min(speedLimit, congestionSpeed) );
	}






	

	

	







	// ** MÉTODO DE INFORME (+ COMPLEMENTARIOS) ** //
	/**
	 * Genera una {@code IniSection} que informa de los 
	 * atributos de la {@code Road} en el tiempo del simulador.
	 * 
	 * @param simTime 	- tiempo del simulador
	 * 
	 * @return 			{@code IniSection} con 
	 * 					información de la {@code Road}
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
		section.setValue("state", getRoadState().toString());
		
		
		return section;
	}

	/**
	 * <p>
	 * Devuelve un {@code StringBuilder} con el estado 
	 * de la {@code Road}.
	 * </p> <p>
	 * Ejemplo:
	 * </p> <p>
	 * (v1, 80), (v3, 80), (v2, 76), (v5, 33)
	 * </p>
	 * 
	 * @return 	{@code StringBuilder} with 
	 * 			state of {@code Road}
	 */
	public StringBuilder getRoadState() {
		StringBuilder state = new StringBuilder();

		// Primero los vehículos en la cola de espera.
		for (Vehicle v : waiting) {
			// ID
			state.append("(" + v.getID());
			// Location
			state.append("," + v.getLocation());

			state.append("),");
		}

		// Después los vehículos en la carretera.
		for (Vehicle v : vehiclesOnRoad) {
			// ID
			state.append("(" + v.getID());
			// Location
			state.append("," + v.getLocation());

			state.append("),");
		}
		
		// Borrado de última coma
		if (state.length() > 0) {
			state.deleteCharAt(state.length() - 1);
		}

		return state;
	}







	// ** MÉTODO DE DESCRIPCIÓN (+ COMPLEMENTARIOS) ** //
	/**
	 * {@inheritDoc} Añade una {@code Road} al map, con 
	 * los datos: id, source, target, length, max speed, 
	 * vehicles.
	 * 
	 * @param out {@inheritDoc}
	 */
	@Override
	public void describe(Map<TableDataType, Object> out) {
		String source = fromJunction.getID();
		String target = toJunction.getID();
		String length = Integer.toString(this.length);
		String maxSpeed = Integer.toString(this.speedLimit);
		String state = getRoadStateDescription();

		out.put(TableDataType.ID, id);
		out.put(TableDataType.R_TYPE, getType());
		out.put(TableDataType.R_SOURCE, source);
		out.put(TableDataType.R_TARGET, target);
		out.put(TableDataType.R_LENGHT, length);
		out.put(TableDataType.R_MAX, maxSpeed);
		out.put(TableDataType.R_STATE, state);
	}

	/**
	 * Devuelve un {@code String} con el estado de la
	 * {@code Road} con el formato específico de las
	 * tablas de la {@code GUI}
	 * 
	 * @return 	{@code String} con el estado de 
	 * 			{@code Road} para la tabla
	 */
	private String getRoadStateDescription() {
		StringBuilder state = new StringBuilder();

		state.append("[");
		// Primero los vehículos en la cola de espera.
		for (Vehicle v : waiting) {
			// ID
			state.append(v.getID());
			state.append(",");
		}

		// Después los vehículos en la carretera.
		for (Vehicle v : vehiclesOnRoad) {
			// ID
			state.append(v.getID());
			state.append(",");
		}

		// Borrado de última coma (mín "[")
		if (state.length() > 1) {
			state.deleteCharAt(state.length() - 1);
		}

		state.append("]");

		return state.toString();
	}












	// ** MÉTODOS ADICIONALES (PARA AVANCE GENERAL) ** //
	/**
	 * Guarda un {@code Vehicle} y su tiempo de llegada 
	 * en la lista de {@code Vehicle}s que van a entrar
	 * en {@code waiting}.
	 * 
	 * @param toWait 		- {@code Vehicle} que va a 
	 * 						entrar a la cola de espera
	 * @param arrivalTime 	- tiempo que ha tardado en llegar 
	 * 						al final en el tick actual
	 */
	public void arriveToWaiting(Vehicle toWait, float arrivalTime) {
		// Se guarda en el Map su información de llegada.
		arrivalsToWaiting.add(new ArrivedVehicle(toWait, arrivalTime));
	}	

	/**
	 * Mueve el primer {@code Vehicle} a la espera en la 
	 * {@code Junction}s de salida de la {@code Road}
	 * a su correspondiente {@code Road} indicada por la ruta.
	 * 
	 * @return 	si ha cruzado el 
	 * 			{@code Vehicle}
	 * 
	 * @throws SimulationException 	si la {@code Road} 
	 * 								está en rojo
	 */
	public boolean moveWaitingVehicle() throws SimulationException {
		if ( isGreen ) {
			boolean hasCrossed = false;

			// Primer vehículo que está esperando.
			Vehicle toMove = waiting.getFirst();

			// Si hay algún vehículo y no está averiado.
			if (toMove != null && toMove.getBreakdownTime() == 0) {
				// Se le saca de la lista de espera y del registro de entradas. 
				entryRecord.remove(toMove);
				waiting.pollFirst();

				// Se mueve a la siguiente carretera.
				toMove.moveToNextRoad();

				hasCrossed = true;
			}

			return hasCrossed;
		}
		else {
			throw new SimulationException(
				"Tried to advance waiting vehicle with red traffic lights" + 
				" in road with id: " + id
			);
		}	
	}

	/**
	 * Actualiza el estado de los {@code Vehicle}s
	 * averiados en la cola de espera {@code waiting}.
	 */
	public void refreshWaiting() {
		for ( Vehicle v : waiting ) {
			if ( v.getBreakdownTime() > 0 ) {
				v.setBreakdownTime(-1); // Se resta un día.
			}
		}
	}

	/**
	 * Método que actualiza la información de {@code fromJunction, toJunction}
	 * para que incluyen a la instancia {@code Road} en sus 
	 * listas {@code incomingRoads, exitRoads}.
	 */
	private void getInOwnJunctions() {
		// fromJunction.getExitRoads().add(this);
		// toJunction.getIncomingRoads().add(this);

		fromJunction.addNewExitRoad(this);
		toJunction.addNewIncomingRoad(this);
	}

	/**
	 * Mete un {@code Vehicle} al final de 
	 * {@code vehiclesOnRoad}.
	 * 
	 * @param v 	- {@code Vehicle} a 
	 * 				añadir al final
	 */
	public void pushVehicle(Vehicle v) {
		vehiclesOnRoad.add(v);

		// Se guarda el último de la lista en 
		// el registro de entradas, pues ha sido
		// el último en entrar.
		entryRecord.add(v);
	}

	/**
	 * Saca un {@code Vehicle} de {@code vehiclesOnRoad}.
	 * 
	 * @param v 	- {@code Vehicle} a quitar
	 * 
	 * @throws NoSuchElementException 	si {@code v} no está en
	 * 									{@code vehiclesOnRoad}
	 */
	public void popVehicle(Vehicle v) throws NoSuchElementException {
		if ( ! vehiclesOnRoad.remove(v) ) {
			throw new NoSuchElementException(
				"Vehicle to pop not found."
			);
		}
	}

	/**
	 * Método de modificación del estado del semáforo
	 * 
	 * @param green 	- nuevo estado del semáforo
	 */
	public void setLight(boolean green) {
		isGreen = green;
	}
	











	// ** MÉTODOS ADICIONALES (DE INFO) ** //
	/**
	 * <p>
	 * Devuelve un {@code StringBuilder} con el estado de 
	 * la cola de espera {@code waiting} de {@code Road}.
	 * </p> <p>
	 * Ejemplo:
	 * </p> <p>
	 * (r2,red,[v3,v2,v5])
	 * 
	 * @return 	{@code StringBuilder} with 
	 * 			state of {@code waiting}
	 */
	public StringBuilder getWaitingState() {
		StringBuilder state = new StringBuilder();
		// ID
		state.append("(" + getID() + ",");
		// Semáforo
		state.append(isGreen ? "green" : "red");
		// Cola de espera
		state.append(",[");
		if (waiting.isEmpty()) {
			state.append("]");
		} else {
			for (Vehicle v : waiting) {
				state.append(v.getID() + ",");
			}
			if (waiting.size() > 0) {
				state.deleteCharAt(state.length() - 1);
			}
			state.append("]");
		}
		state.append(")");

		return state;
	}

	/**
	 * <p>
	 * Devuelve un {@code StringBuilder} con el estado de 
	 * la cola de espera {@code waiting} de {@code Road}, 
	 * guardando también el tiempo restante que le queda al semáforo 
	 * en verde para ponerse rojo, {@code lightTime}.
	 * </p> <p>
	 * Ejemplos:
	 * </p> <p>
	 * (r2,red,[v3,v2,v5])
	 * </p> <p>
	 * (r4,green:4,[v1,v6])
	 * 
	 * @param lightTime 	- tiempo restante que el 
	 * 						semáforo estará encendido
	 * 
	 * @return 	{@code StringBuilder} with 
	 * 			state of {@code waiting}
	 */
	public StringBuilder getWaitingState(int lightTime) {
		StringBuilder state = new StringBuilder();
		// ID
		state.append("(" + getID() + ",");
		// Semáforo
		state.append(isGreen ? "green" : "red");
		// Tiempo de semáforo
		state.append(":" + lightTime);
		// Cola de espera
		state.append(",[");
		if (waiting.isEmpty()) {
			state.append("]");
		} else {
			for (Vehicle v : waiting) {
				state.append(v.getID() + ",");
			}
			if (waiting.size() > 0) {
				state.deleteCharAt(state.length() - 1);
			}
			state.append("]");
		}
		state.append(")");

		return state;
	}

	/**
	 * Devuelve la longitud de la vía.
	 * 
	 * @return longitud de la vía
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Devuelve si el semáforo está en verde.
	 * 
	 * @return si el semáforo está en verde
	 */
	public boolean isGreen() {
		return isGreen;
	}

	/**
	 * Devuelve si la cola de espera {@code waiting} 
	 * está vacía.
	 * 
	 * @return si {@code waiting} está vacía
	 */
	public boolean noVehiclesWaiting() {
		return waiting.isEmpty();
	}

	/**
	 * Devuelve la {@code Junction}s desde la que 
	 * empieza la {@code Road}.
	 * 
	 * @return <code>fromJunction</code>
	 */
	public Junction getFromJunction() {
		return fromJunction;
	}

	/**
	 * Devuelve la {@code Junction}s donde acaba la
	 * {@code Road}.
	 * 
	 * @return {@code toJunction}
	 */
	public Junction getToJunction() {
		return toJunction;
	}

	/**
	 * Devuelve la lista de registro de entradas de 
	 * los {@code Vehicle}s que están en la 
	 * {@code Road}
	 * 
	 * @return 	registro de entradas
	 */
	private Deque<Vehicle> getEntryRecord() {
		return entryRecord;
	}

	/**
	 * Devuelve el número de {@code Vehicle}s esperando
	 * en la cola {@code waiting}.
	 * 
	 * @return número de {@code Vehicle}s esperando.
	 */
	public int getNumWaitingVehicles() {
		return waiting.size();
	}

	/**
	 * Devuelve un {@code String} con el tipo de 
	 * {@code Road} que debe ponerse como valor 
	 * en la clave {@code type}, tanto en la 
	 * {@code IniSection} generada en {@code batch} 
	 * como en la información mostrada en las 
	 * tablas de la {@code GUI}.
	 * 
	 * @return 	{@code String} con el 
	 * 			tipo de {@code Road}
	 */
	protected String getType() {
		return "-";
	}

	/**
	 * Devuelve una lista con todos los vehículos en
	 * la {@code Road}, los de {@code vehiclesOnRoad}
	 * y los de {@code waiting}.
	 * 
	 * @return 	{@code List<Vehicle>} con todos
	 * 			los {@code Vehicle}s de la 
	 * 			{@code Road}
	 */
	public List<Vehicle> getRoadVehicles() {
		List<Vehicle> list = new ArrayList<>();

		list.addAll(vehiclesOnRoad);
		list.addAll(waiting);

		return list;
	}
}
