package es.ucm.fdi.model.events;


import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de una
 * {@link Junction} en la simulación.
 */
public class NewJunction extends Event {

	/**
	 * Identificador del objeto de simulación.
	 */
	protected String id;

	/**
	 * Constructor de {@link NewJunction}.
	 * 
	 * @param newTime 	- tiempo de ejecución del evento
	 * @param ID 		- identificador de la nueva
	 * 					{@code Junction}
	 */
	public NewJunction(int newTime, String ID) {
		super(newTime);
		id = ID;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * El {@code NewJunction} crea una nueva
	 * {@code Junction} dentro de la simulación.
	 * </p> <p>
	 * La ejecución del evento puede fallar por la presencia
	 * de un {@code SimObject} ya registrado en la simulación
	 * con el ID de la nueva {@code Junction}.
	 * </p>
	 * 
	 * @param sim 	- la simulación sobre la que 
	 * 				se ejecuta el evento
	 * 
	 * @throws AlreadyExistingSimObjException 	if {@code Junction}	ID 
	 * 											already registered 
	 */
	@Override
	public void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException {
		if ( ! sim.getRoadMap().existsJunctionID(id) ) {
			sim.addJunction( newJunction() );
		} 
		else {
			throw new AlreadyExistingSimObjException(
				"Junction with id:" + id + " already in simulation."
			);
		}
	}

	/**
	 * <p>
	 * Devuelve la descripción {@code NewJunction}
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New junction j3"
	 * </p>
	 * 
	 * @return 	{@code String} con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New junction ");
		description.append(id);

		return 	description.toString();
	}
	
	/**
	 * Método que genera una nueva {@code Junction}
	 * a partir de los actributos del evento.
	 * 
	 * @return 	{@code Junction} with indicated ID
	 */
	protected Junction newJunction() {
		return new Junction(id);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de {@code NewJunction},
	 * comprueba también que los IDs sean iguales.
	 * </p>
	 * 
	 * @param obj 	- objeto a comparar
	 * 
	 * @return 	if {@code NewJunction} 
	 * 			equals {@code obj}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean same;
		same = super.equals(obj);

		if (same) {
			NewJunction other = (NewJunction) obj;

			same = ( same && id == other.id );
		}
		
		return same;
	}
}
