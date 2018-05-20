package es.ucm.fdi.model.events;


import es.ucm.fdi.model.simobj.Junction;
import es.ucm.fdi.model.simobj.Road;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una
 * {@link Road} en la simulación.
 */
public class NewRoad extends Event {
	
	/**
	 * Identificador del objeto de simulación.
	 */
	protected String id;

	/**
	 * Longitud de la {@code Road}.
	 */
	protected int length;

	/**
	 * Límite de velocidad de la {@code Road}.
	 */
	protected int speedLimit;

	/**
	 * {@code Junction} donde empieza
	 * la {@code Road}.
	 */
	protected String fromJunctionID;

	/**
	 * {@code Junction} donde acaba
	 * la {@code Road}.
	 */
	protected String toJunctionID;	

	/**
	 * Constructor de {@link NewRoad}.
	 * 
	 * @param newTime 	- tiempo de ejecución del evento
	 * @param ID 		- identificador de la nueva {@code Road}
	 * @param max 		- longitud de la vía
	 * @param lim 		- límite de velocidad
	 * @param fromID 	- {@code Junction} donde empieza
	 * @param toID 		- {@code Junction} donde acaba
	 */
	public NewRoad(int newTime, String ID, int lgth, 
			int lim, String fromID, String toID) {

		super(newTime);
		id = ID;
		fromJunctionID = fromID;
		toJunctionID = toID;
		speedLimit = lim;
		length = lgth;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * El {@code NewRoad} crea una nueva
	 * {@code Road}  dentro de la simulación.
	 * </p> <p>
	 * La ejecución del evento puede fallar por la 
	 * presencia de un {@code SimObject} ya 
	 * registrado en la simulación con el ID de la 
	 * nueva {@code Road}.
	 * </p>
	 * 
	 * @param sim 	- la simulación sobre la que
	 * 				se ejecuta el evento
	 * 
	 * @throws AlreadyExistingSimObjException 	if {@code Road} ID
	 * 											already registered
	 * @throws NonExistingSimObjException 	    if source or target 
     *                                          {@code Junction}s not
     *                                          registered
	 */
	@Override
	public void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException, NonExistingSimObjException {

		if ( ! sim.getRoadMap().existsRoadID(id) ) {
			try {
				sim.addRoad( newRoad(sim) );			
			}
			catch (NonExistingSimObjException e) {
				throw e;
			}
		}
		else {
			throw new AlreadyExistingSimObjException(
				"Road with id: " + id + " already in simulation."
			);
		}
	}

	/**
	 * <p>
	 * Devuelve la descripción {@code NewRoad}
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New road r3"
	 * </p>
	 * 
	 * @return 	{@code String} con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New road ");
		description.append(id);

		return description.toString();
	}

	/**
	 * Método que genera una nueva {@code Road}
	 * a partir de los atributos del evento.
	 * 
	 * @param sim 	- la simulación sobre la que
	 * 				se ejecuta el evento
	 * 
	 * @return 		{@code Road} con los datos 
	 * 				del evento
	 * 
	 * @throws NonExistingSimObjException 	    if source or target 
     *                                          {@code Junction}s not
     *                                          registered
	 */
	protected Road newRoad(TrafficSimulation sim) 
			throws NonExistingSimObjException {

		Junction fromJunction = sim.getRoadMap().getJunctionWithID(fromJunctionID);
		Junction toJunction = sim.getRoadMap().getJunctionWithID(toJunctionID);

		if ( fromJunction != null && toJunction != null ) {
			return 	new Road(id, length, speedLimit, 
							fromJunction, toJunction);
		}
		else {
			throw new NonExistingSimObjException(
				"One or both junctions from Road with id: " + id + 
				" don't exist."
			);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de {@code NewRoad}, comprueba
	 * también que los IDs, la longitud, el límite de
	 * velocidad y las {@code Junction}s de entrada
	 * y salida son iguales.
	 * </p>
	 * 
	 * @param obj 	- objeto a comparar
	 * 
	 * @return 		if {@code NewRoad} 
	 * 				equals {@code obj}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean same;
		same = super.equals(obj);

		if (same) {
			NewRoad other = (NewRoad) obj;

			same = ( same && id == other.id );
			same = ( same && length == other.length );
			same = ( same && speedLimit == other.speedLimit );
			same = ( same && fromJunctionID.equals(other.fromJunctionID) );
			same = ( same && toJunctionID.equals(other.toJunctionID) );
		}
		
		return same;
	}
}
