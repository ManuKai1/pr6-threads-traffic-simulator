package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.model.simobj.CarVehicle;
import es.ucm.fdi.model.simobj.Junction;
import es.ucm.fdi.model.simulation.AlreadyExistingSimObjException;
import es.ucm.fdi.model.simulation.NonExistingSimObjException;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.model.simulation.TrafficSimulation;

/**
 * {@link Event} que representa la creación de un nuevo 
 * {@link CarVehicle} en la simulación. Hereda de 
 * {@link NewVehicle}
 */
public class NewCarVehicle extends NewVehicle {
	
	// ** ATRIBUTOS ** //
	/**
	 * Entero que representa 
	 * la resistencia a las averías.
	 */
	private int resistance;

	/**
	 * Probabilidad de avería del 
	 * {@code CarVehicle}
	 */
	private double faultyChance;
	
	/**
	 * Duración máxima de la avería.
	 */
	private int faultDuration;
	
	/**
	 * Semilla aleatoria.
	 */
	private long randomSeed;
	
	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link NewCarVehicle}.
	 * 
	 * @param newTime 		- tiempo de ejecución del 
	 * 						evento
	 * @param ID 			- identificador del nuevo 
	 * 						{@code CarVehicle}
	 * @param max 			- máxima velocidad alcanzable
	 * @param trip 			- ruta de {@code Junction}s
	 * @param res 			- resistencia a la avería
	 * @param breakChance 	- probabilidad de avería
	 * @param breakDuration - duración máxima de avería
	 * @param seed 			- semilla aleatoria
	 */
	public NewCarVehicle(int newTime, String ID, int max, List<String> trip, 
			int res, double breakChance, int breakDuration, long seed) {
		super(newTime, ID, max, trip);
		resistance = res;
		faultyChance = breakChance;
		faultDuration = breakDuration;
		randomSeed = seed;
	}

	// ** MÉTODO DE EJECUCIÓN ** //
	/**
	 * {@inheritDoc}
	 * <p>
	 * El evento {@code NewCarVehicle} crea un nuevo objeto 
	 * {@code CarVehicle} en la simulación.
	 * </p>
	 * 
	 * @param sim 	- la simulación sobre la que 
	 * 				se ejecuta el evento
	 * 
	 * @throws AlreadyExistingSimObjException 	if {@code Vehicle} ID 
	 * 											already registered
	 * @throws NonExistingSimObjException   	if a junction on the 
	 * 											itinerary does not exist
	 */
	@Override
	public void execute(TrafficSimulation sim) 
			throws AlreadyExistingSimObjException, NonExistingSimObjException {
		try {
			super.execute(sim);
		}
		catch ( AlreadyExistingSimObjException e ) {
			throw e;
		} catch (NonExistingSimObjException e) {
			throw e;
		}
	}

	// ** MÉTODO DE DESCRIPCIÓN ** //
	/**
	 * <p>
	 * Devuelve la descripción de {@code NewCarVehicle}
	 * utilizada en las tablas de la GUI. Ejemplo:
	 * </p> <p>
	 * "New car vehicle v1"
	 * </p>
	 * 
	 * @return 	{@code String} con la descripción
	 */
	@Override
	protected String getEventDescription() {
		// Descripción del evento.
		StringBuilder description = new StringBuilder();
		description.append("New car vehicle ");
		description.append(id);

		return description.toString();
	}
	
	// ** MÉTODO DE NUEVO VEHÍCULO ** //
	/**
	 * Método que genera un nuevo {@code CarVehicle} 
	 * a partir de los atributos del evento.
	 * 
	 * @param sim 	- la simulación sobre la que 
	 * 				se ejecuta el evento
	 * 
	 * @return 		{@code CarVehicle} con los datos
	 * 				del evento
	 * 
	 * @throws NonExistingSimObjException 	si alguna {@code Junction} de 
	 * 										la ruta no está registrada
	 */
	@Override
	protected CarVehicle newVehicle(TrafficSimulation sim) 
			throws NonExistingSimObjException {

		ArrayList<Junction> trip = new ArrayList<Junction>();

		// Deben existir todos los cruces del itinerario 
		// en el momento del evento.
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
			return	new CarVehicle(id, trip, maxSpeed, resistance, faultyChance,
							faultDuration, randomSeed);
		} catch (SimulationException e) {
			throw new NonExistingSimObjException(e.getMessage());
		}
	}	
}
