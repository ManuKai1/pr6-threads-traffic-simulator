package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;

/**
 * Clase que representa un camino de tierra como 
 * un objeto de simulación. Hereda de {@link Road}.
 */
public class DirtRoad extends Road {


    // ** CONSTRUCTOR ** //
    /**
     * Constructor de {@link DirtRoad}.
     * 
     * @param identifier    - identificador del objeto
     * @param len           - longitud de la vía
     * @param spLimit       - límite de velocidad
     * @param fromJ         - {@code Junction} donde empieza
     * @param toJ           - {@code Junction} donde acaba
     */
    public DirtRoad(String identifier, int len, int spLimit,
            Junction fromJ, Junction toJ) {
        super(identifier, len, spLimit, fromJ, toJ);
    }

    
    
    
    
    
    // ** MÉTODOS COMPLEMENTARIOS DE AVANCE ** //
    /**
     * Calcula la velocidad base de la {@code DirtRoad}: 
     * el límite de velocidad {@code speedLimit}.
     * 
     * @return  la velocidad base de 
     *          la {@code DirtRoad}.
     */
    @Override
    protected int getBaseSpeed() {
        return speedLimit;
    }

    /**
     * <p>
     * Modifica la velocidad que llevarán los 
     * {@code Vehicle}s en la {@code DirtRoad} 
     * previo avance.
     * </p> <p>
     * En la {@code DirtRoad}, el {@code reductionFactor} 
     * aumenta en uno por cada {@code Vehicle} averiado 
     * delante de un {@code Vehicle}.
     * </p>
     * 
     * @param onRoad    lista de {@code Vehicle}s 
     *                  en {@code DirtRoad}.
     */
    @Override
    protected void vehicleSpeedModifier(ArrayList<Vehicle> onRoad) {
        // Velocidad máxima a la que pueden avanzar los vehículos.
        int baseSpeed = getBaseSpeed();

        // Factor de reducción de velocidad en caso de obstáculos delante.
        int reductionFactor = 1;

        // Se modifica la velocidad a la que avanzarán los vehículos,
        // teniendo en cuenta el factor de reducción.
        for (Vehicle v : onRoad) {
            v.setSpeed(baseSpeed / reductionFactor);

            if (v.getBreakdownTime() > 0) {
                reductionFactor += 1;
            }
        }
    }







    // ** MÉTODO DE INFORME (+ COMPLEMENTARIOS) ** //
    /**
     * Genera una {@code IniSection} que informa de los 
     * atributos de la {@code DirtRoad} en el 
     * tiempo del simulador.
     * 
     * @param simTime   tiempo del simulador
     * @return          {@code IniSection} con información
     *                  de la {@code DirtRoad}
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
        section.setValue("state", getRoadState().toString());

        return section;
    }

    /**
     * {@inheritDoc}
     * 
     * @return  {@inheritDoc}
     */
    @Override
    protected String getType() {
		return "dirt";
	}
}