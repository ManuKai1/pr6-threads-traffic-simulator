package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.model.simobj.BikeVehicle;
import es.ucm.fdi.model.simobj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de un nuevo
 * {@link BikeVehicle} en la simulación. Hereda de 
 * {@link NewVehicle}.
 */
public class NewBikeVehicle extends NewVehicle {

	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link BikeVehicle}.
	 * 
	 * @param newTime 	- tiempo de ejecución del 
	 * 					evento
	 * @param ID 		- identificador del nuevo 
	 * 					{@code BikeVehicle}
	 * @param max 		- máxima velocidad alcanzable
	 * @param trip 		- ruta de {@code Junction}s
	 */
	public NewBikeVehicle(int newTime, String ID, int max, List<String> trip) {
		super(newTime, ID, max, trip);
	}

	// ** MÉTODO DE EJECUCIÓN ** //
	/**
	 * {@inheritDoc}
	 * <p>
	 * El evento {@code NewBikeVehicle} crea un nuevo objeto
	 * {@code BikeVehicle} en la simulación.
	 * </p>
	 * 
	 * @param sim {@inheritDoc}
	 * 
	 * @throws AlreadyExistingSimObjException 	if {@code Vehicle} ID 
	 * 											already registered
	 * @throws NonExistingSimObjException 		if a {@code Junction} on 
	 * 											the itinerary does not exist
	 */
	@Override
	public void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException, NonExistingSimObjException {
		try {
			super.execute(sim);
		} 
		catch (AlreadyExistingSimObjException e) {
			throw e;
		} catch (NonExistingSimObjException e) {
			throw e;
		}
	}

	// ** MÉTODO DE DESCRIPCIÓN ** //
	/**
	 * <p>
	 * Devuelve la descripción {@code NewBikeVehicle}
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New bike vehicle v1"
	 * </p>
	 * 
	 * @return 	{@code String} con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New bike vehicle ");
		description.append(id);

		return description.toString();
	}

	// ** MÉTODO DE NUEVO VEHÍCULO ** //
	/**
	 * Método que genera un nuevo {@code BikeVehicle}
	 * a partir de los atributos del evento.
	 * 
	 * @param sim 	- la simulación sobre la que 
	 * 				se ejecuta el evento
	 * 
	 * @return 		{@code BikeVehicle} con los datos 
	 * 				del evento
	 * 
	 * @throws NonExistingSimObjException 	si alguna {@code Junction}
	 * 										o {@code Road} en la ruta 
	 * 										no está registrada
	 */
	@Override
	protected BikeVehicle newVehicle(TrafficSimulation sim) 
			throws NonExistingSimObjException {

		ArrayList<Junction> trip = new ArrayList<Junction>();

		// Deben existir todos los cruces del 
		// itinerario en el momento del evento.
		for ( String jID : tripID ) {
			Junction j = sim.getRoadMap().getJunctionWithID(jID);
			if (j != null) {
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
			return	new BikeVehicle( id, trip, maxSpeed );
		} 
		catch (SimulationException e) {
			throw new NonExistingSimObjException(e.getMessage());
		}
	}
}
