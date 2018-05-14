package es.ucm.fdi.model.events;


import es.ucm.fdi.model.SimObj.CrowdedJunction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una 
 * {@link CrowdedJunction} en la simulación.
 */
public class NewCrowdedJunction extends NewJunction {

    // ** CONSTRUCTOR ** //
    /**
     * Constructor de {@link NewCrowdedJunction}.
     * 
     * @param newTime   - tiempo de ejecución del 
     *                  evento
     * @param ID        identificador de la nueva 
     *                  {@code CrowdedJunction}
     */
    public NewCrowdedJunction(int newTime, String ID) {
        super(newTime, ID);
    }

    // ** MÉTODO DE EJECUCIÓN ** //
    /**
     * {@inheritDoc}
     * <p>
     * El evento {@code NewCrowdedJunction} crea un nuevo 
     * objeto {@code CrowdedJunction} en la simulación.
     * </p>
     * 
     * @param sim   - la simulación sobre la que
     *              se ejecuta el evento
     * 
     * @throws AlreadyExistingSimObjException   if {@code Junction} ID 
     *                                          already registered
     */
    @Override
    public void execute(TrafficSimulation sim) 
            throws AlreadyExistingSimObjException {
        try {
            super.execute(sim);
        } catch (AlreadyExistingSimObjException e) {
            throw e;
        }
    }

    // ** MÉTODO DE DESCRIPCIÓN ** //
    /**
	 * <p>
	 * Devuelve la descripción {@code NewCrowdedJunction}
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New crowded junction j3"
	 * </p>
	 * 
	 * @return  {@code String} con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New crowded junction ");
		description.append(id);

		return 	description.toString();
	}

    // ** MÉTODO DE NUEVA INTERSECCIÓN ** //
    /**
     * Método que genera una nueva {@code CrowdedJunction}
     * a partir de los atributos del evento.
     * 
     * @return  {@code CrowdedJunction} with 
     *          indicated attributes
     */
    @Override 
    protected CrowdedJunction newJunction() {
        return new CrowdedJunction(id); 
    }
}