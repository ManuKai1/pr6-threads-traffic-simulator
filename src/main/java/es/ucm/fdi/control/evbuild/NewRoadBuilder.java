package es.ucm.fdi.control.evbuild;


import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewRoad;
import es.ucm.fdi.model.simobj.Road;

/**
 * Clase que construye un {@code Event} 
 * {@link NewRoad} utilizado para crear una
 * {@link Road} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewRoadBuilder extends EventBuilder {
	
	/**
	 * Etiqueta utilizada en las {@code IniSection}s
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_road";

	/**
	 * Constructor de {@link NewRoadBuilder} que pasa
	 * el atributo {@code SECTION_TAG} al constructor de 
	 * la superclase.
	 */
	public NewRoadBuilder() {
		super(SECTION_TAG);
	}

	/**
	 * Método de parsing que comprueba si la 
	 * {@code IniSection} pasada como argumento 
	 * representa un evento {@code NewRoad}
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	- {@code IniSection} a parsear
	 * 
	 * @return 		{@code NewRoad} event or 
	 * 				{@code null} if parsing failed
	 * 
	 * @throws IllegalArgumentException if {@code ini} represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
	@Override
	Event parse(IniSection ini)
			throws IllegalArgumentException {

		// Se comprueba si es un NewRoad
		if ( iniNameMatch(ini) && typeMatch(ini, null) ) {
            String id;
			int time = 0;
			int maxSpeed, length;
			String src, dest;
			
			// ID ok?
			try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " in new Road."
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
						"in Road with id " + id
					);
				}
			}

			// SOURCE ok?	
			try {
				src = parseID(ini, "src");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading source junction " +
					"in Road with id " + id
				);
			}
			
			// DESTINY ok?
			try {
				dest = parseID(ini, "dest");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading destination junction " + 
					"in Road with id " + id
				);
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = parsePositiveInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading max speed "+
					"in Road with id " + id
				);
			}
			
			// LENGTH ok?
			try {
				length = parsePositiveInt(ini, "length");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading length "+
					"in Road with id " + id
				);
			}
			
			// New Road.
			return 	new NewRoad(time, id, length, 
							maxSpeed, src, dest);
		}
		else {
			return null;
		}
	}
}
