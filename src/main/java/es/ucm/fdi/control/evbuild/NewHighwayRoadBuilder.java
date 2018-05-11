package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewHighwayRoad;
import es.ucm.fdi.model.SimObj.HighwayRoad;

/**
 * Clase que construye un {@code Event} 
 * {@link NewHighwayRoad} utilizado para crear una 
 * {@link HighwayRoad} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewHighwayRoadBuilder extends EventBuilder {

	// ** ATRIBUTOS ** //
	/**
	 * Etiqueta utilizada en las {@code IniSection}s
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_road";

	/**
	 * Valor que debería almacenar la clave {@code type}
	 * de una {@code IniSection} que represente a un
	 * {@code HighwayRoad}.
	 */
	private static final String TYPE = "lanes";




	// ** CONSTRUCTOR ** //
    /**
	 * Constructor de {@link NewHighwayRoadBuilder} que 
	 * pasa el atributo {@code SECTION_TAG} al 
	 * constructor de la superclase.
	 */
    public NewHighwayRoadBuilder() {
        super(SECTION_TAG);
    }




	// ** MÉTODO DE PARSE ** //
	/**
	 * Método de parsing que comprueba si la 
	 * {@code IniSection} pasada como argumento 
	 * representa un evento {@code NewHighwayRoad}
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	- {@code IniSection} a parsear
	 * 
	 * @return 		{@code NewHighwayRoad} event or 
	 * 				{@code null} if parsing failed
	 * 
	 * @throws IllegalArgumentException if {@code ini} represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
    @Override
    Event parse(IniSection ini) 
			throws IllegalArgumentException {

        // Se comprueba si es una NewHighwayRoad.
        if ( iniNameMatch(ini) && typeMatch(ini, TYPE) ) {
            String id;
			int time = 0;
			int maxSpeed, length, lanes;
			String src, dest;
			
			// ID ok?
			try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " in new Highway Road."
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
						"in Highway Road with id " + id
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
					"in Highway Road with id " + id
				);
			}
			
			// DESTINY ok?
			try {
				dest = parseID(ini, "dest");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading destination junction " + 
					"in Highway Road with id " + id
				);
			}
			
			// MAXSPEED ok?
			try {
				maxSpeed = parsePositiveInt(ini, "max_speed");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading max speed "+
					"in Highway Road with id " + id
				);
			}
			
			// LENGTH ok?
			try {
				length = parsePositiveInt(ini, "length");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " when reading length "+
					"in Highway Road with id " + id
				);
			}

            // LANES ok?
            try {
                lanes = parsePositiveInt(ini, "lanes");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
					e.getMessage() + " when reading length " + 
					"in Highway Road with id " + id
				);
            }

            // New Highway Road.
            return 	new NewHighwayRoad(time, id, length, maxSpeed, 
							src, dest, lanes);
        } 
        else {
			return null;
		}
    }
}

