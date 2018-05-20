package es.ucm.fdi.control.evbuild;

import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.FaultyVehicle;
import es.ucm.fdi.model.simobj.Vehicle;

/**
 * Clase que construye un {@code Event} 
 * {@link FaultyVehicle} utilizado para averiar 
 * {@link Vehicle Vehicles} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class FaultyVehicleBuilder extends EventBuilder {
	
	/**
	 * Etiqueta utilizada en las {@code IniSection}s
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "make_vehicle_faulty";

	/**
	 * Constructor de {@link FaultyVehicleBuilder} que 
	 * pasa el atributo {@code SECTION_TAG} al 
	 * constructor de la superclase.
	 */
	public FaultyVehicleBuilder() {
		super(SECTION_TAG);
	}

	/**
	 * Método de parsing que comprueba si la 
	 * {@code IniSection} pasada como argumento 
	 * representa un evento {@code FaultyVehicle}
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	- {@code IniSection} a parsear
	 * 
	 * @return 		{@code FaultyVehicle} event or 
	 * 				{@code null} if parsing failed
	 * 
	 * @throws IllegalArgumentException if {@code ini} represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
	@Override
	public Event parse(IniSection ini)
			throws IllegalArgumentException {

		if (iniNameMatch(ini)) {
			int time = 0;
			int duration;

			// TIME ok?
			try {
				time = parseNoNegativeInt(ini, "time");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + "when reading time of faulty vehicles."
				);
			}

			// DURATION ok?
			try {
				duration = parsePositiveInt(ini, "duration");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + "when reading duration of faulty vehicles."
				);
			}

			// VEHICLE_LIST ok?
			// Creación de la lista de vehículos.
			List<String> vehicles;
			try {
				vehicles = parseIDList(ini, "vehicles", 1);
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + "when reading list of faulty vehicles."
				);
			}

			// Faulty Vehicle.
			return 	new FaultyVehicle(time, vehicles, duration);
		}
		else 
			return null;
	}
}
