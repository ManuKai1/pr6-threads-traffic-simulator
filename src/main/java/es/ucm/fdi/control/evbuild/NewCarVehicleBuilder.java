package es.ucm.fdi.control.evbuild;

import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewCarVehicle;
import es.ucm.fdi.model.SimObj.CarVehicle;

/**
 * Clase que construye un {@code Event} 
 * {@link NewCarVehicle} utilizado para crear un 
 * {@link CarVehicle} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewCarVehicleBuilder extends EventBuilder {

	// ** ATRIBUTOS ** //
	/**
	 * Etiqueta utilizada en las {@code IniSection}s
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_vehicle";

	/**
	 * Valor que debería almacenar la clave {@code type}
	 * de una {@code IniSection} que represente a un
	 * {@code CarVehicle}.
	 */
	private static final String TYPE = "car";




	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link NewCarVehicleBuilder} que 
	 * pasa el atributo {@code SECTION_TAG} al 
	 * constructor de la superclase.
	 */
	public NewCarVehicleBuilder() {
		super(SECTION_TAG);
	}





	// ** MÉTODO DE PARSE ** //
	/**
	 * Método de parsing que comprueba si la 
	 * {@code IniSection} pasada como argumento 
	 * representa un evento {@code NewCarVehicle}
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	- {@code IniSection} a parsear
	 * 
	 * @return 		{@code NewCarVehicle} event or 
	 * 				{@code null} if parsing failed
	 * 
	 * @throws IllegalArgumentException if {@code ini} represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
	@Override
	Event parse(IniSection ini)
			throws IllegalArgumentException {
		
		// Se comprueba si es un NewCarVehicle
		if ( iniNameMatch(ini) && typeMatch(ini, TYPE) ) {
			String id;
			int time = 0;
			int maxSpeed, resistance, faultDuration;
			double faultyChance;
			long seed;

			// ID ok?
			try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException( 
					e.getMessage() + " in new Car."
				);
			}

			// TIME ok?
			if ( existsTimeKey(ini) ) {
				try {
					time = parseNoNegativeInt(ini, "time");
				}
				catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(
						e.getMessage() + " when reading time " +
						"in car with id " + id
					);
				}
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = parseNoNegativeInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading max_speed "+ 
					"in car with id " + id
				);
			}

			// TRIP ok?
			// Creación de la ruta de Junction IDs.
			List<String> trip;
			try {
				trip = parseIDList(ini, "itinerary", 2);
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading itinerary "+ 
					"in car with id " + id
				);
			}

			// RESISTANCE ok?
			try {
				resistance = parsePositiveInt(ini, "resistance");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading resistance " +
					"in car with id " + id
				);
			}

			// FAULTY_CHANCE ok?
			try {
				faultyChance = parseProbability(ini, "fault_probability");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() +" when reading faulty chance "+
					"in car with id " + id
				);
			}

			// FAULT_DURATION ok?
			try {
				faultDuration = parsePositiveInt(ini, "max_fault_duration");
			}
			
			catch (IllegalArgumentException e) {
				//La duración de avería no era un entero
				throw new IllegalArgumentException(
					e.getMessage() + " when reading fault duration "
					+ "in car with id " + id
				);
			}

			// SEED ok?
			if ( existsSeedKey(ini) ) {
				try {
					seed = parseLong(ini, "seed");
				}
				catch (NumberFormatException e) {
					throw new IllegalArgumentException(
						"Seed reading failure in car with ID: " + id
					);
				}
			}
			else {
				seed = System.currentTimeMillis();
			}

			// New Car Vehicle.
			return 	new NewCarVehicle(time, id, maxSpeed, trip, resistance, 
							faultyChance, faultDuration, seed);
		}
		else {
			return null;
		}
	}
}
