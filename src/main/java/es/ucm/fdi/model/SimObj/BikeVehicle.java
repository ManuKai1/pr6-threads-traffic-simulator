package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.simulation.SimulationException;

/**
 * Clase que representa una bicicleta como un objeto
 * de simulación. Hereda de {@link Vehicle}
 */
public class BikeVehicle extends Vehicle {

	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link BikeVehicle}.
	 * 
	 * @param identifier 	- identificador del objeto
	 * @param trp 			- ruta de {@code Junction}s
	 * @param max 			- máxima velocidad alcanzable
	 * 
	 * @throws SimulationException {@inheritDoc}
	 */
	public BikeVehicle(String identifier, ArrayList<Junction> trp, int max) 
			throws SimulationException {

		super(identifier, trp, max);
	}

	// ** MÉTODOS COMPLEMENTARIOS DE AVANCE ** //
	/**
	 * Modifica el tiempo de avería según el comportamiento 
	 * especial de un {@code BikeVehicle}.
	 * 
	 * @param addedBreakdownTime 	- tiempo de avería a sumar
	 */
	@Override
	public void setBreakdownTime(int addedBreakdownTime)  {
		// Si la bicicleta avanza más rápido que la mitad de su velocidad
		// alcanzable, entonces podrá sumársele el tiempo de avería.
		if ( actualSpeed > (maxSpeed / 2) ) {
			breakdownTime += addedBreakdownTime;
		}
	}

	// ** MÉTODO DE INFORME ** //
	/**
	 * Genera una {@code IniSection} que informa de los 
	 * atributos del {@code BikeVehicle} en el tiempo 
	 * del simulador.
	 * 
	 * @param simTime 	- tiempo del simulador
	 * 
	 * @return 			{@code IniSection} con información 
	 * 					del {@code BikeVehicle}
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
		return "bike";
	}
}
