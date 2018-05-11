package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.util.TableDataType;

/**
 * Clase que representa un coche como un objeto
 * de simulación. Hereda de {@link SimObject}
 */
public class Vehicle extends SimObject {

	// ** ATRIBUTOS ** //
	/**
	 * Etiqueta que encabeza el informe de un
	 * {@code Vehicle} cualquiera.
	 */
	protected final String REPORT_TITLE = "[vehicle_report]";
	
	/**
	 * Ruta del {@code Vehicle} en forma de
	 * lista de {@code Junction}s.
	 */
	protected List<Junction> trip;

	/**
	 * Última posición en la lista que representa la ruta,
	 * de forma que {@code trip.get(lastTripPos)} es la
	 * última {@code Junction} por la que ha pasado
	 * el {@code Vehicle}.
	 */
	protected int lastTripPos = 0;

	/**
	 * Máxima velocidad que puede alcanzar el {@code Vehicle} 
	 * en cualquier vía.
	 */
	protected int maxSpeed;

	/**
	 * Distancia recorrida por el {@code Vehicle} desde 
	 * que empezo la simulación.
	 */
	protected int kilometrage = 0;

	/**
	 * Tiempo restante hasta la recuperación de un {@code Vehicle}
	 * averiado. Si {@code breakdownTime = 0}, no está averiado.
	 */
	protected int breakdownTime = 0;

	/**
	 * Booleano que indica si el {@code Vehicle}
	 * ha llegado a si destino, es decir, a la última 
	 * {@code Junction} de {@code trip}.
	 */
	protected boolean hasArrived = false;

	/**
	 * Booleano que indica si un {@code Vehicle}
	 * está esperando en la cola de una {@code Road}
	 * para cruzar una {@code Junction}.
	 */
	protected boolean isWaiting = false;

	/**
	 * {@code Road} en la que se encuentra el 
	 * {@code Vehicle}.
	 */
	protected Road road;

	/**
	 * Localización del {@code Vehicle}
	 * dentro de la {@code road}.
	 */
	protected int location = 0;

	/**
	 * Velocidad actual del coche en la {@code road}.
	 */
	protected int actualSpeed = 0;	







	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link Vehicle}.
	 * 
	 * @param identifier 	- identificador del objeto
	 * @param trp 			- ruta de {@code Junction}s
	 * @param max 			- máxima velocidad alcanzable
	 * 
	 * @throws SimulationException 	cuando no se encuentra la 
	 * 								primera carretera entre 
	 * 								sus junction
	 */
	public Vehicle(String identifier, ArrayList<Junction> trp, int max) throws SimulationException {
		super(identifier);
		trip = trp;
		maxSpeed = max;

		// Se mete en la primera carretera.
		try {
			Junction fromJunction = trip.get(lastTripPos);
			Junction nextJunction = trip.get(lastTripPos + 1);
			road = fromJunction.getRoadTo(nextJunction);
						
			road.pushVehicle(this);
		}
		catch (SimulationException e) {
			throw e;
		}
	}
	







	// ** MÉTODO DE AVANCE (+ COMPLEMENTARIOS) ** //
	/**
	 * {@inheritDoc}
	 * Método de AVANCE de {@code Vehicle}:
	 * <p>
	 * En primer lugar, comprueba si el {@code Vehicle} está averiado. 
	 * Si lo está, se reduce su {@code breakdownTime} y no avanza. 
	 * Si no lo está, se comprueba si llegaría al final de la
	 * {@code Road} en este tick.
	 * </p> <p>
	 * Si es así, se le hace esperar en la {@code Junction},
	 * en la cola correspondiente a su {@code Road}. 
	 * Si no, se modifica su  {@code location} sumándola 
	 * su {@code actualSpeed}.
	 * </p>
	 */
	@Override
	public void proceed() {
		// Comprobamos primero si el vehículo está averiado o no
		if ( isFaulty() ) {
			breakdownTime--;
		}
		else {
			// Comprobamos si el vehículo llega al cruce.
			if ( location + actualSpeed >= road.getLength() ) {
				kilometrage += ( road.getLength() - location );
				waitInJunction();
			}
			else {
				location += actualSpeed;
				kilometrage += actualSpeed;
			}
		}		
	}

	/**
	 * <p>
	 * Saca a {@code Vehicle} de {@code road.vehiclesOnRoad}
	 * y lo introduce en {@code road.arrivalsToWaiting}.
	 * </p> <p>
	 * Queda a la espera de ser introducido en la cola del cruce 
	 * {@code road.waiting} una vez se hayan movido todos los 
	 * {@code Vehicle} de la {@code road}.
	 * </p>
	 */
	public void waitInJunction() {
		// Saca al vehículo de la zona de circulación de la Road
		road.popVehicle(this);
		
		// Cálculo del tiempo de llegada.
		float arrivalTime = ( actualSpeed / (road.getLength() - location) );
		// Se mete el Vehicle en la lista de llegados a la cola de espera.
		// Será introducido en road.waiting una vez que todos hayan llegado.
		road.arriveToWaiting(this, arrivalTime);	
		
		// Localización = longitud de Road
		location = road.getLength();
		isWaiting = true;
		actualSpeed = 0;
	}







	// ** MÉTODO DE INFORME (+ COMPLEMENTARIOS) ** //
	/**
	 * Genera una {@code IniSection} que informa de los atributos del
	 * {@code Vehicle} en el timmpo del simulador.
	 * 
	 * @param simTime 	- tiempo del simulador
	 * 
	 * @return 	{@code IniSection} con información 
	 * 			del {@code Vehicle}
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
		section.setValue("speed", actualSpeed);
		section.setValue("kilometrage", kilometrage);
		section.setValue("faulty", breakdownTime);
		section.setValue("location", getReportLocation());

		return section;
	}

	/**
	 * Devuelve la localización del {@code Vehicle}
	 * como debe mostrarse en los informes generados
	 * por el simulador.
	 * 
	 * @return	{@code String} con la localización
	 * 			correcta para el informe
	 */
	protected String getReportLocation() {
		return 	hasArrived ? 
					"arrived" : 
					"(" + road.getID() + "," + location + ")";
	}









	// ** MÉTODO DE DESCRIPCIÓN (+ COMPLEMENTARIOS) ** //
	/**
	 * {@inheritDoc}
	 * Añade una {@code Road} al mapa, con los datos:
	 * id, source, target, length, max speed, vehicles
	 * 
	 * @param out {@inheritDoc}
	 */
	@Override
	public void describe(Map<TableDataType, Object> out) {
		// Strings
		String type = getType();
		String road = this.road.getID();
		String location = getDescriptionLocation();
		String speed = Integer.toString(this.actualSpeed);
		String km = Integer.toString(this.kilometrage);
		String faulty = Integer.toString(this.breakdownTime);
		String route = getRouteDescription();

		// Map update
		out.put(TableDataType.ID, id);
		out.put(TableDataType.V_TYPE, type);
		out.put(TableDataType.V_ROAD, road);
		out.put(TableDataType.V_LOCATION, location);
		out.put(TableDataType.V_SPEED, speed);
		out.put(TableDataType.V_KM, km);
		out.put(TableDataType.V_FAULTY, faulty);
		out.put(TableDataType.V_ROUTE, route);
	}

	/**
	 * Devuelve un {@code String} con la descripción
	 * de la ruta del {@code Vehicle}, como debe
	 * mostrarse en la correspondiente tabla
	 * del {@code GUI}.
	 * 
	 * @return	{@code String} con la descripción
	 * 			de la ruta
	 */
	private String getRouteDescription() {
		StringBuilder route = new StringBuilder();

		route.append("[");
		for (Junction j : trip) {
			route.append( j.getID() );
			route.append(",");
		}

		// Borrado de última coma (mín: "[")
		if (route.length() > 1) {
			route.deleteCharAt(route.length() - 1);
		}
		
		route.append("]");

		return 	route.toString();
	}

	/**
	 * Devuelve la localización del {@code Vehicle}
	 * como debe mostrarse en la correspondiente
	 * tabla del {@code GUI}.
	 * 
	 * @return	{@code String} con la descripción
	 * 			de la localización
	 */
	protected String getDescriptionLocation() {
		return 	hasArrived ?
					"arrived" :
					Integer.toString(location);
	}











	// ** MÉTODOS ADICIONALES ** //
	/**
	 * <p>
	 * Mueve el {@code Vehicle} a la siguiente {@code Road} 
	 * que le  corresponde según su {@code trip}.
	 * </p> <p>
	 * El método falla si no encuentra ninguna {@code Road}
	 * entre las dos {@code Junction}s
	 * 
	 * @throws SimulationException 	si no se encuentra la 
	 * 								siguiente carretera
	 */
	public void moveToNextRoad() throws SimulationException {
		int waitingPos = lastTripPos + 1; // Cruce donde estaba esperando
		int nextWaitingPos = waitingPos + 1; // Cruce donde debe acabar la siguiente road

		if ( nextWaitingPos == trip.size() ) {
			// Última vez. El cruce donde se espera es el destino final.
			hasArrived = true;
		}				 
		else {
			// Cambio normal de una road a otra.
			try {
				Junction fromJunction = trip.get(waitingPos);
				Junction nextJunction = trip.get(nextWaitingPos);
				road = fromJunction.getRoadTo(nextJunction);
				
				road.pushVehicle(this);

				location = 0;
			} catch (SimulationException e) {
				throw e;
			}			
		}

		// Se ha pasado ya la siguiente Junction
		lastTripPos++;
		// El vehículo ya no está esperando
		isWaiting = false;
	}
	
	/**
	 * Añade más tiempo de avería al ya existente.
	 * 
	 * @param addedBreakdownTime 	- tiempo de avería 
	 * 								a sumar
	 */
	public void setBreakdownTime(int addedBreakdownTime)  {
		breakdownTime += addedBreakdownTime;
	}	
	
	/**
	 * Modifica la velocidad del {@code Vehicle} como 
	 * el mínimo entre la velocidad permitida por la 
	 * {@code Road} y la velocidad máxima alcanzable 
	 * por {@code Vehicle}.
	 * 
	 * @param roadSpeed 	- velocidad permitida por 
	 * 						la {@code Road}
	 */
	public void setSpeed(int roadSpeed) {
		if (breakdownTime == 0 ) {
			actualSpeed = Math.min(roadSpeed, maxSpeed);
		}
		else {
			actualSpeed = 0;
		}
	}	
	
	/**
	 * Devuelve el tiempo restante de avería 
	 * del {@code Vehicle}.
	 * 
	 * @return 	tiempo de avería
	 */
	public int getBreakdownTime() {
		return breakdownTime;
	}

	/**
	 * Devuelve la localización del {@code Vehicle}
	 * en la {@code Road}.
	 * 
	 * @return 	la localización del 
	 * 			{@code Vehicle}
	 */
	public int getLocation() {
		return location;
	}
	
	/**
	 * Devuelve si el {@code Vehicle} 
	 * está averiado.
	 * 
	 * @return si hay avería
	 */
	public boolean isFaulty() {
		return (breakdownTime > 0);
	}
	
	/**
	 * Devuelve un {@code String} con el tipo de 
	 * {@code Vehicle} que debe ponerse como valor 
	 * en la clave {@code type}, tanto en la 
	 * {@code IniSection} generada en {@code batch} 
	 * como en la información mostrada en las 
	 * tablas de la {@code GUI}.
	 * 
	 * @return 	{@code String} con el 
	 * 			tipo de {@code Vehicle}
	 */
	protected String getType() {
		return "-";
	}
}


