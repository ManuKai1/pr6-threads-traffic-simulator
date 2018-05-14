package es.ucm.fdi.control.evbuild;

import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewBikeVehicle;

/**
 * Clase que construye un {@code Event} 
 * {@link NewBikeVehicle} utilizado para crear un 
 * {@link BikeVehicle} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewBikeVehicleBuilder extends EventBuilder {
	
	// ** ATRIBUTOS ** //
	/**
	 * Etiqueta utilizada en las {@code IniSection}s
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_vehicle";

	/**
	 * Valor que debería almacenar la clave {@code type}
	 * de una {@code IniSection} que represente a un
	 * {@code BikeVehicle}.
	 */
	private static final String TYPE = "bike";

	// ** CONSTRUCTOR ** //
	/** 
	 * Constructor de {@link NewBikeVehicleBuilder} que 
	 * pasa el atributo <code>SECTION_TAG</code> al 
	 * constructor de la superclase.
	 */
	public NewBikeVehicleBuilder() {
		super(SECTION_TAG);
	}

	// ** MÉTODO DE PARSE ** //
	/**
	 * Método de parsing que comprueba si la 
	 * {@code IniSection} pasada como argumento 
	 * representa un evento {@code NewBikeVehicle}
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	- {@code IniSection} a parsear
	 * 
	 * @return 		{@code NewBikeVehicle} event or 
	 * 				{@code null} if parsing failed
	 * 
	 * @throws IllegalArgumentException if {@code ini} represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
	@Override
	Event parse(IniSection ini)
			throws IllegalArgumentException {

		// Se comprueba si es un NewBikeVehicle
		if ( iniNameMatch(ini) && typeMatch(ini, TYPE) ) {
			String id;
			int time = 0;
			int maxSpeed;

			// ID ok?
			try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " in new Bike."
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
						"in bike with id: " + id
					);
				}
			}

			// MAXSPEED ok?
			try {
				maxSpeed = parseNoNegativeInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading max_speed " +
					"in bike with id: " + id
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
					e.getMessage() + " when reading itinerary " + 
					"in bike with id: " + id
				);
			}
			
			// New Bike Vehicle.
			return 	new NewBikeVehicle(time, id, maxSpeed, trip);
		}
		else {
			return null;
		}			
	}
}
