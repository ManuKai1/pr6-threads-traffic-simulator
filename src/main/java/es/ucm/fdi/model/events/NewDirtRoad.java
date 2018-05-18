package es.ucm.fdi.model.events;


import es.ucm.fdi.model.simobj.DirtRoad;
import es.ucm.fdi.model.simobj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una
 * {@link DirtRoad} en la simulación. Hereda de 
 * {@link NewRoad}
 */
public class NewDirtRoad extends NewRoad {

    // ** CONSTRUCTOR ** //
    /**
     * Constructor de {@link NewDirtRoad}.
	 * 
	 * @param newTime   - tiempo de ejecución del evento
	 * @param ID        - identificador de la nueva
     *                  {@code DirtRoad}
	 * @param max       - longitud de la vía
	 * @param lim       - límite de velocidad
	 * @param fromID    {@code Junction} donde empieza
	 * @param toID      {@code Junction} donde acaba
	 */
    public NewDirtRoad(int newTime, String ID, int lgth, int lim,
            String fromID, String toID) {

        super(newTime, ID, lgth, lim, fromID, toID);
    }

    // ** MÉTODO DE EJECUCIÓN ** //
    /**
     * {@inheritDoc}
     * <p>
     * El {@code NewDirtRoad} crea un nuevo objeto
     * {@code DirtRoad} en la simulación.
     * </p>
     * 
     * @param sim   - la simulación sobre la que
     *              se ejecuta el evento
     * 
     * @throws AlreadyExistingSimObjException   if {@code Road} ID
     *                                          already registered
     * @throws NonExistingSimObjException 	    if source or target 
     *                                          {@code Junction}s not
     *                                          registered
     */
    @Override
    public void execute(TrafficSimulation sim) 
            throws AlreadyExistingSimObjException, NonExistingSimObjException {
        
        try {
            super.execute(sim);
        } catch (AlreadyExistingSimObjException e) {
            throw e;
        }
        catch (NonExistingSimObjException e) {
            throw e;
        }
    }

    // ** MÉTODO DE DESCRIPCIÓN ** //
    /**
     * <p>
     * Devuelve la descripción {@code NewDirtRoad}
     * utilizada en las tablas de la GUI. Ejemplo:
     * </p> <p>
     * "New dirt road r3"
     * </p>
     * 
     * @return 	{@code String} con la descripción
     */
    @Override
    protected String getEventDescription() {
        // Descripción del evento.
        StringBuilder description = new StringBuilder();
        description.append("New dirt road ");
        description.append(id);

        return description.toString();
    }

    // ** MÉTODO DE NUEVA CARRETERA ** //
    /**
     * Método que genera una nueva {@code DirtRoad}
     * a partir de los atributos del evento.
     * 
     * @param sim      - la simulación sobre la que
     *                 se ejecuta el evento
     * 
     * @return  {@code DirtRoad} con los 
     *          datos del evento
     * 
     * @throws NonExistingSimObjException 	    if source or target 
     *                                          {@code Junction}s not
     *                                          registered
     */
    @Override
    protected DirtRoad newRoad(TrafficSimulation sim) 
            throws NonExistingSimObjException {

        Junction fromJunction = sim.getRoadMap().getJunctionWithID(fromJunctionID);
        Junction toJunction = sim.getRoadMap().getJunctionWithID(toJunctionID);

        if ( fromJunction != null && toJunction != null ) {
            return   new DirtRoad(id, length, speedLimit,
                            fromJunction, toJunction);
        } 
        else {
            throw new NonExistingSimObjException(
                "One or both junctions from Road with id: " + id + 
                " don't exist."
            );
        }
    }
}