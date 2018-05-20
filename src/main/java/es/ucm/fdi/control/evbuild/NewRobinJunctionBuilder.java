package es.ucm.fdi.control.evbuild;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.NewRobinJunction;

/**
 * Clase que construye un {@code Event}
 * {@link NewRobinJunction} utilizado para crear una
 * {@link RobinJunction} durante la simulación.
 * Hereda de {@link EventBuilder}.
 */
public class NewRobinJunctionBuilder extends EventBuilder {
    
    /**
     * Etiqueta utilizada en las {@code IniSection}s
     * para representar este tipo de eventos.
     */
    private static final String SECTION_TAG = "new_junction";

    /**
     * Valor que debería almacenar la clave {@code type}
     * de una {@code IniSection} que represente a una
     * {@code RobinJunction}.
     */
    private static final String TYPE = "rr";

    /**
     * Constructor de {@link NewRobinJunctionBuilder} que 
     * pasa el atributo {@code SECTION_TAG} al 
     * constructor de la superclase.
     */
    public NewRobinJunctionBuilder() {
		super(SECTION_TAG);
    }
    
    /**
     * Método de parsing que comprueba si la 
     * {@code IniSection} pasada como argumento 
     * representa un evento {@code NewRobinJunction}
     * y si sus parámetros son correctos.
     * 
     * @param ini 	{@code IniSection} a parsear
     * @return 		{@code NewRobinJunction} event or 
     * 				{@code null} if parsing failed
     * 
     * @throws IllegalArgumentException if {@code ini} represents 
     *	 								the searched event but its 
     *									arguments are not valid
     */
	@Override
	Event parse(IniSection ini) 
            throws IllegalArgumentException {
		
        // Se comprueba si es un NewRobinJunction
		if ( iniNameMatch(ini) && typeMatch(ini, TYPE) ) {
			String id;
            int time = 0;
            int minTime, maxTime;
			
            // ID ok?
            try {
				id = parseID(ini, "id");
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
                    e.getMessage() + " in new Robin Junction."
                );
			}

            // TIME ok?
            if ( existsTimeKey(ini) ) {
				try {
					time = parseNoNegativeInt(ini, "time");
				}
				catch (IllegalArgumentException e ){
					throw new IllegalArgumentException(
                        e.getMessage() + " when reading time " +
                        "in Robin Junction with id " + id
                    );
				}
			}
            
            // TIMELAPSES ok?
            // Tiempo mínimo.
            try {
                minTime = parseNoNegativeInt(ini,"min_time_slice");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    e.getMessage() + " when reading minimum Time " +
                    "in Robin Junction with ID " + id
                );
            }

            // Tiempo máximo.
            try {
                maxTime = parseNoNegativeInt(ini,"max_time_slice");
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    e.getMessage() + " when reading maximum Time "+
                    "in Robin Junction with ID " + id
                );
            }

            // Mínimo menor que máximo.
            if (minTime > maxTime) {
                throw new IllegalArgumentException(
                	"Not a valid time lapse " + 
                    " in Robin Junction ID: " + id
                );
            }
			
            // New Robin Junction.
			return  new NewRobinJunction(time, id, minTime, maxTime);
		}
		else {
            return null;
        }
	}
}