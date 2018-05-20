package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewDirtRoad;
import es.ucm.fdi.model.simobj.DirtRoad;

/**
 * Clase que construye un {@code Event} 
 * {@link NewDirtRoad} utilizado para crear una 
 * {@link DirtRoad} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewDirtRoadBuilder extends EventBuilder {

	/**
	 * Etiqueta utilizada en las {@code IniSection}s
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_road";

	/**
	 * Valor que debería almacenar la clave {@code type}
	 * de una {@code IniSection} que represente a una
	 * {@code DirtRoad}.
	 */
	private static final String TYPE = "dirt";

    /**
	 * Constructor de {@link NewDirtRoadBuilder} que 
	 * pasa el atributo {@code SECTION_TAG} al 
	 * constructor de la superclase.
	 */
    public NewDirtRoadBuilder() {
        super(SECTION_TAG);
    }

    /**
	 * Método de parsing que comprueba si la 
	 * {@code IniSection} pasada como argumento 
	 * representa un evento {@code NewDirtRoad}
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	- {@code IniSection} a parsear
	 * 
	 * @return 		{@code NewDirtRoad} event or 
	 * 				{@code null} if parsing failed
	 * 
	 * @throws IllegalArgumentException if {@code ini} represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
    @Override
    Event parse(IniSection ini)
			throws IllegalArgumentException {

        if (iniNameMatch(ini) && typeMatch(ini, TYPE)) {
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
					e.getMessage() + " in new Dirt Road."
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
						"in Dirt Road with id " + id
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
					"in Dirt Road with id " + id
				);
			}
			
			// DESTINY ok?
			try {
				dest = parseID(ini, "dest");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading destination junction " +
					"in Dirt Road with id " + id
				);
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = parsePositiveInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading max speed " +
					"in Dirt Road with id " + id
				);
			}
			
			// LENGTH ok?
			try {
				length = parsePositiveInt(ini, "length");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading length " +
					"in Dirt Road with id " + id
				);
			}
			
			// New Road.
			return 	new NewDirtRoad(time, id, length, 
							maxSpeed, src, dest);

        } 
        else {
			return null;
		}
    }
}
