package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewJunction;
import es.ucm.fdi.model.SimObj.Junction;

/**
 * Clase que construye un {@code Event} 
 * {@link NewJunction} utilizado para crear una 
 * {@link Junction} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewJunctionBuilder extends EventBuilder {
	
	// ** ATRIBUTOS ** //
	/**
	 * Etiqueta utilizada en las {@code IniSection}s
	 * para representar este tipo de eventos.
	 */
	private static final String SECTION_TAG = "new_junction";
	



	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link NewJunctionBuilder} que 
	 * pasa el atributo {@code SECTION_TAG} al 
	 * constructor de la superclase.
	 */
	public NewJunctionBuilder() {
		super(SECTION_TAG);
	}
	




	// ** MÉTODO DE PARSE ** //
	/**
	 * Método de parsing que comprueba si la 
	 * {@code IniSection} pasada como argumento 
	 * representa un evento {@code NewJunction}
	 * y si sus parámetros son correctos.
	 * 
	 * @param ini 	- {@code IniSection} a parsear
	 * 
	 * @return 		{@code NewJunction} event or 
	 * 				{@code null} if parsing failed
	 * 
	 * @throws IllegalArgumentException if {@code ini} represents 
	 *	 								the searched event but its 
	 *									arguments are not valid
	 */
	@Override
	Event parse(IniSection ini) 
			throws IllegalArgumentException {

		// Se comprueba si es un NewJunction
		if ( iniNameMatch(ini) && typeMatch(ini, null) ) {
            String id = ini.getValue("id");
            int time = 0;

            // ID ok?
            try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
					e.getMessage() + " in new Junction."
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
						"in Junction with id " + id
					);
				}
			}

			return 	new NewJunction(time, id);
		}
		else {
			return null;
		}
	}

}
