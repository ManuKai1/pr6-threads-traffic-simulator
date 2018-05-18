package es.ucm.fdi.model.events;


import es.ucm.fdi.model.simobj.RobinJunction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una 
 * {@link RobinJunction} en la simulación. Heradad
 * de {@link NewJunction}
 */
public class NewRobinJunction extends NewJunction {
    
    // ** ATRIBUTOS ** //
    /**
     * Tiempo mínimo de encendido del semáforo.
     */
    private int minTime;

    /**
     * Tiempo máximo de encendido del semáforo.
     */
    private int maxTime;

    // ** CONSTRUCTOR ** //
    /**
     * Constructor de {@link NewRobinJunction}.
     * 
     * @param newTime   - tiempo de ejecución del evento
     * @param ID        - identificador de la nueva
     *                  {@code RobinJunction}
     * @param minT      - tiempo mínimo de semáforo
     * @param maxT      - tiempo máximo de semáforo
     */
    public NewRobinJunction(int newTime, String ID, int minT, int maxT) {
        super(newTime, ID);
        minTime = minT;
        maxTime = maxT;
    }

    // ** MÉTODO DE EJECUCIÓN ** //
    /**
     * {@inheritDoc}
     * <p>
     * El evento {@code NewRobinJunction} crea un nuevo objeto 
     * {@code RobinJunction} en la  simulación.
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
        }
        catch (AlreadyExistingSimObjException e) {
            throw e;
        }
    }

    // ** MÉTODO DE DESCRIPCIÓN ** //
    /**
	 * <p>
	 * Devuelve la descripción {@code NewRobinJunction}
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New robin junction j3"
	 * </p>
	 * 
	 * @return  {@code String} con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New robin junction ");
		description.append(id);

		return 	description.toString();
	}

    // ** MÉTODO DE NUEVA INTERSECCIÓN ** //
    /**
     * Método que genera una nueva {@code RobinJunction}ç
     * a partir de los actributos del <code>Event</code>.
     * 
     * @return {@code RobinJunction} with indicated attributes
     */
    @Override
    protected RobinJunction newJunction() {
        return  new RobinJunction(id, minTime, maxTime);
    }
}