package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de un nuevo
 * {@link Vehicle} en la simulación.
 */
public class NewVehicle extends Event {

	// ** ATRIBUTOS ** //
	/**
	 * Identificador del objeto de simulación.
	 */
	protected String id;

	/**
	 * Máxima velocidad alcanzable.
	 */
	protected int maxSpeed;

	/**
	 * Ruta del {@code Vehicle}
	 * a lo largo del simulador.
	 */
	protected List<String> tripID;
	




	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link NewVehicle}.
	 * 
	 * @param newTime 	- tiempo de ejecución del evento
	 * @param ID 		- identificador del nuevo 
	 * 					{@code Vehicle}
	 * @param max 		- máxima velocidad alcanzable
	 * @param trip 		- ruta de {@code Junction}s
	 */
	public NewVehicle(int newTime, String ID, int max, List<String> trip) {
		super(newTime);
		id = ID;
		maxSpeed = max;
		tripID = trip;
	}

	
	
	
	
	
	// ** MÉTODO DE EJECUCIÓN ** //
	/**
	 * {@inheritDoc}
	 * <p>
	 * El {@code NewVehicle} crea un nuevo
	 * {@code Vehicle} dentro de la simulación.
	 * </p> <p>
	 * La ejecución del evento puede fallar por la 
	 * presencia de un {@code SimObject} ya 
	 * registrado en la simulación con el ID del 
	 * nuevo {@code Vehicle}.
	 * </p>
	 * 
	 * @param sim 	- la simulación sobre la que 
	 * 				se ejecuta el evento
	 * 
	 * @throws AlreadyExistingSimObjException 	if {@code Vehicle} 
	 * 											ID already registered 
	 * @throws NonExistingSimObjException 		si alguna {@code Junction} de 
	 *	 										la ruta no está registrada
	 */
	@Override
	public void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException, NonExistingSimObjException {
		if ( ! sim.getRoadMap().existsVehicleID(id) ) {
			try {
				Vehicle newV = newVehicle(sim);
				sim.addVehicle(newV);
			}
			catch (NonExistingSimObjException e) {
				throw e;
			}
		}
		else {
			throw new AlreadyExistingSimObjException(
				"Vehicle with id: " + id + " already in simulation."
			);
		}
	}

	
	
	
	
	// ** MÉTODO DE DESCRIPCIÓN ** //
	/**
	 * <p>
	 * Devuelve la descripción {@code NewVehicle}
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New vehicle v1"
	 * </p>
	 * 
	 * @return 	{@coded String} con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New vehicle ");
		description.append(id);

		return description.toString();
	}
	
	
	
	
	
	// ** MÉTODO DE NUEVO VEHÍCULO ** //
	/**
	 * Método que genera un nuevo {@code Vehicle}
	 * a partir de los atributos del evento.
	 * 
	 * @param sim 	- la simulación sobre la que
	 * 				se ejecuta el evento
	 * 
	 * @return 		{@code Vehicle} con los datos
	 * 				del evento
	 * 
	 * @throws NonExistingSimObjException 	si alguna {@code Junction} en 
	 * 										la ruta no está registrada
	 */
	protected Vehicle newVehicle(TrafficSimulation sim) 
			throws NonExistingSimObjException {

		ArrayList<Junction> trip = new ArrayList<Junction>();

		// Deben existir todos los cruces del 
		// itinerario en el momento del evento.
		for ( String jID : tripID ) {
			Junction j = sim.getRoadMap().getJunctionWithID(jID);

			if ( j != null ) {
				trip.add(j);
			}
			else {
				throw new NonExistingSimObjException(
					"Junction with id: " + jID + 
					" from itinerary of vehicle with id: " + id + 
					" not found in simulation."
				);
			}
		}
		try {
			return	new Vehicle(id, trip, maxSpeed);
		} catch (SimulationException e) {
			throw new NonExistingSimObjException(e.getMessage());
		}
	}





	// ** MÉTODO DE COMPARACIÓN ** //
	/**
	 * {@inheritDoc}
	 * <p>
	 * En el caso de {@code NewVehicle},
	 * comprueba también que los IDs, la velocidad
	 * máxima y la ruta son iguales. 
	 * </p>
	 * 
	 * @param obj 	objeto a comparar
	 * @return 		if {@code NewVehicle} equals <code>obj</code>
	 */
	@Override
	public boolean equals(Object obj) {
		boolean same;
		same = super.equals(obj);

		if (same) {
			NewVehicle other = (NewVehicle) obj;

			same = ( same && id == other.id );
			same = ( same && maxSpeed == other.maxSpeed );
			same = ( same && tripID.equals(other.tripID) );
		}
		
		return same;
	}
}
