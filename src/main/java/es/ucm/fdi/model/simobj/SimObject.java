package es.ucm.fdi.model.simobj;

import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.Describable;
import es.ucm.fdi.util.TableDataType;

/**
 * Clase base que representa un objeto cualquiera
 * de la simulación, a saber: {@link Junction Junctions}, 
 * {@link Road Roads} y {@link Vehicle Vehicles}
 */
public abstract class SimObject implements Describable { 

	/**
	 * Identificador del objeto de simulación.
	 */
	protected String id;	

	/**
	 * Método de avance de cualquier objeto de la 
	 * simulación. Ocurre en un tick.
	 */
	public abstract void proceed();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void describe(Map<TableDataType, Object> out);

	public abstract IniSection generateIniSection(int simTime);

	/**
	 * Constructor de {@link SimObject}.
	 * 
	 * @param identifier 	- identificador del 
	 * 						objeto
	 */
	public SimObject(String identifier) {
		id = identifier;
	}
	
	/**
	 * Comprueba si el {@code SimObject} es igual a 
	 * un objeto dado {@code obj}.
	 * 
	 * @param obj 	- objeto a comparar
	 * 
	 * @return 	if {@code SimObject} 
	 * 			equals {@code obj}
	 */
	@Override
	public boolean equals(Object obj) {
		// Mismo objeto
		if (this == obj) {
			return true;
		}
		
		// obj no es ningún objeto
		if (obj == null) {
			return false;
		}
		
		// Misma clase.
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		
		// Mismo identificador.
		SimObject other = (SimObject) obj;
		return (id == other.id);
	}	

	/**
	 * Devuelve el identificador del objeto 
	 * de la simulación.
	 * 
	 * @return  identificador del objeto
	 */
	public String getID() {
		return id;
	}
}
