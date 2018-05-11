package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;
import java.util.Random;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

/**
 * Clase que representa un coche como un objeto
 * de simulación. Hereda de {@link Vehicle}
 */
public class CarVehicle extends Vehicle {

	// ** ATRIBUTOS ** //
	/**
	 * Resistencia a las averías.
	 */
	private int resistance;

	/**
	 * Probabilidad de avería.
	 */
	private double faultyChance;
	
	/**
	 * Duración máxima de la avería.
	 */
	private int faultDuration;
	
	/**
	 * Semilla aleatoria.
	 */
	private Random randomSeed;
	
	/**
	 * Distancia transcurrida desde la última avería.
	 */
	private int kmSinceFaulty = 0;
	







	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link CarVehicle}.
	 * 
	 * @param identifier 	- identificador del objeto
	 * @param trp 			- ruta de {@code Junction}s
	 * @param max 			- máxima velocidad alcanzable
	 * @param res 			- resistencia a averiarse
	 * @param breakChance 	- probabilidad de avería
	 * @param breakDuration - duración máxima de avería
	 * @param seed 			- semilla aleatoria
	 * 
	 * @throws SimulationException {@inheritDoc}
	 */
	public CarVehicle(String identifier, ArrayList<Junction> trp,
			int max, int res, double breakChance, int breakDuration, 
			long seed) throws SimulationException {
		super(identifier, trp, max);
		resistance = res;
		faultyChance = breakChance;
		faultDuration = breakDuration;
		randomSeed = new Random(seed);
	}
	






	// ** MÉTODO DE AVANCE ** //
	/**
	 * {@inheritDoc}
	 * <p>
	 * ----------
	 * </p> <p>
	 * *Como {@code CarVehicle}, se comprueba si el
	 * {@code Vehicle} puede averiarse por distancia
	 * recorrida y probabilidad de avería.
	 * </p>
	 */
	@Override
	public void proceed() {
		// 1 //
		// No está averiado, pero puede averiarse
		// si se dan las condiciones.
		if ( ! isFaulty() ) {
			if ( kmSinceFaulty > resistance ) {
				if ( randomSeed.nextDouble() < faultyChance ) {
					// Generamos un tiempo de avería entre 1 y faultDuration
					setBreakdownTime( randomSeed.nextInt(faultDuration) + 1 );
				}
			}
		}

		// 2 //
		// Puede averarse por un evento o si se dan
		// las condiciones anteriores.
		if ( isFaulty() ) {
			kmSinceFaulty = 0;
			actualSpeed = 0;
		}

		// 3 //
		// El coche avanza como un vehículo normal y con
		// las diferencias de kilometraje se calculan la
		// distancia que lleva el coche sin averiarse.
		int oldKilometrage = kilometrage;
		super.proceed();

		kmSinceFaulty += kilometrage - oldKilometrage;
	}
	
	
	
	
	
	
	
	
	// ** MÉTODO DE INFORME ** //
	/**
	 * Genera una {@code IniSection} que informa de
	 * los atributos del {@code CarVehicle} en el 
	 * tiempo del simulador.
	 * 
	 * @param simTime 	- tiempo del simulador
	 * 
	 * @return 			{@code IniSection} con información 
	 * 					del {@code CarVehicle}
	 */
	@Override
	public IniSection generateIniSection(int simTime) {
		// 1 //
		// Se crea la etiqueta de la sección (sin corchetes).
		String tag = REPORT_TITLE;
		tag = (String) tag.subSequence(1, tag.length() - 1);
		IniSection section = new IniSection(tag);

		// 2 // 
		// Se generan los datos en el informe.
		section.setValue("id", id);
		section.setValue("time", simTime);
		section.setValue("type", getType());
		section.setValue("speed", actualSpeed);
		section.setValue("kilometrage", kilometrage);
		section.setValue("faulty", breakdownTime);
		section.setValue("location", getReportLocation());

		return section;
	}

	/**
     * {@inheritDoc}
     * 
     * @return  {@inheritDoc}
     */
    @Override
    protected String getType() {
		return "car";
	}
}
