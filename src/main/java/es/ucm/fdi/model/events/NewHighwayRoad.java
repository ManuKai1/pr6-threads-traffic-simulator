package es.ucm.fdi.model.events;

import es.ucm.fdi.model.simobj.HighwayRoad;
import es.ucm.fdi.model.simobj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una
 * {@link HighwayRoad} en la simulación. Hereda de 
 * {@link NewRoad}.
 */
public class NewHighwayRoad extends NewRoad {
    
    /**
     * Número de carriles de la vía.
     */
    private int numLanes;

    /**
     * Constructor de {@link NewHighwayRoad}.
     * 
     * @param newTime   - tiempo de ejecución del evento
     * @param ID        - identificador de la nueva 
     *                  {@code HighwayRoad}
     * @param max       - longitud de la vía
     * @param lim       - límite de velocidad
     * @param fromID    - {@code Junction} donde empieza
     * @param toID      - {@code Junction} donde acaba
     * @param numLanes  - número de carriles de la vía
     */
    public NewHighwayRoad(int newTime, String ID, int lgth, int lim,
            String fromID, String toID, int lanes) {

        super(newTime, ID, lgth, lim, fromID, toID);
        numLanes = lanes;
    }

    /**
     * {@inheritDoc}
     * <p>
     * El evento {@code NewHighwayRoad} crea un nuevo objeto
     * {@code HighwayRoad} en la simulación.
     * </p>
     * 
     * @param sim la simulación sobre la que se ejecuta el evento
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
        } catch (NonExistingSimObjException e) {
            throw e;
        }
    }

    /**
     * <p>
     * Devuelve la descripción {@code NewHighwayRoad}
     * utilizada en las tablas de la GUI. Ejemplo:
     * </p> <p>
     * "New highway road r3"
     * </p>
     * 
     * @return 	{@code String} con la descripción
     */
    @Override
    protected String getEventDescription() {
        // Descripción del evento.
        StringBuilder description = new StringBuilder();
        description.append("New highway road ");
        description.append(id);

        return description.toString();
    }

    /**
     * Método que genera una nueva {@code HighwayRoad}
     * a partir de los atributos del evento.
     * 
     * @param sim   la simulación sobre la que 
     *              se ejecuta el evento
     * 
     * @return  {@code HighwayRoad} con los 
     *          datos del evento
     * 
     * @throws NonExistingSimObjException   if source or target 
     *                                      {@code Junction}s not
     *                                      registered
     */
    @Override
    protected HighwayRoad newRoad(TrafficSimulation sim) 
            throws NonExistingSimObjException {
                
        Junction fromJunction = sim.getRoadMap().getJunctionWithID(fromJunctionID);
        Junction toJunction = sim.getRoadMap().getJunctionWithID(toJunctionID);

        if ( fromJunction != null && toJunction != null ) {
            return  new HighwayRoad(id, length, speedLimit, 
                            fromJunction, toJunction, numLanes);
        } 
        else {
            throw new NonExistingSimObjException(
                "One or both junctions from Road with id: " + id + 
                " don't exist."
            );
        }
    }
}