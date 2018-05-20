package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewCrowdedJunction;
import es.ucm.fdi.model.SimObj.CrowdedJunction;

/**
 * Clase que construye un {@code Event} 
 * {@link NewCrowdedJunction} utilizado para crear una
 * {@link CrowdedJunction} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewCrowdedJunctionBuilder extends EventBuilder {

    /**
     * Etiqueta utilizada en las {@code IniSection}s
     * para representar este tipo de eventos.
     */
    private static final String SECTION_TAG = "new_junction";

    /**
     * Valor que debería almacenar la clave {@code type}
     * de una {@code IniSection} que represente a una
     * {@code CrowdedJunction}.
     */
    private static final String TYPE = "mc";

    /**
     * Constructor de {@link NewCrowdedJunctionBuilder} que pasa
     * el parámetro {@code SECTION_TAG} al constructor de la
     * superclase.
     */
    public NewCrowdedJunctionBuilder() {
        super(SECTION_TAG);
    }

    /**
     * Método de parsing que comprueba si la 
     * {@code IniSection} pasada como argumento 
     * representa un evento {@code NewCrowdedJunction}
     * y si sus parámetros son correctos.
     * 
     * @param ini 	- {@code IniSection} a parsear
     * 
     * @return 		{@code NewCrowdedJunction} event or 
     * 				{@code null} if parsing failed
     * 
     * @throws IllegalArgumentException if {@code ini} represents 
     *	 								the searched event but its 
     *									arguments are not valid
     */
    @Override
    Event parse(IniSection ini) 
            throws IllegalArgumentException {

        // Se comprueba si es un NewCrowdedJunction
        if ( iniNameMatch(ini) && typeMatch(ini, TYPE) ) {
            String id = ini.getValue("id");
            int time = 0;

            // ID ok?
            try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
                    e.getMessage() + " in new Crowded Junction."
                );
			}

            // TIME ok?
            if ( existsTimeKey(ini) ) {
				try {
					time = parseNoNegativeInt(ini, "time");
				}
				catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(
                        e.getMessage() + " when reading time "+ 
                        "in Crowded Junction with id " + id);
				}
			}

            // New Crowded Junction.
            return new NewCrowdedJunction(time, id);
        } 
        else {
            return null;
        }
    }
}