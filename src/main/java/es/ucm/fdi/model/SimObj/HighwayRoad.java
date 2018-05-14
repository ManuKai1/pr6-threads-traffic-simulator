package es.ucm.fdi.model.SimObj;

import java.util.ArrayList;

import es.ucm.fdi.ini.IniSection;

/**
 * Clase que representa una vía de varios carriles 
 * como un objeto de simulación. Hereda de {@link Road}
 */
public class HighwayRoad extends Road {

    // ** ATRIBUTOS ** //
    /**
     * Número de carriles de la autopista.
     */
    private int numLanes;

    // ** CONSTRUCTOR ** //
    /**
     * Constructor de {@link HighwayRoad}.
     * 
     * @param identifier    - identificador del objeto
     * @param len           - longitud de la vía
     * @param spLimit       - límite de velocidad
     * @param fromJ         - {@code Junction} donde empieza
     * @param toJ           - {@code Junction} donde acaba
     * @param lanes         - número de carriles
     */
    public HighwayRoad(String identifier, int len, int spLimit, 
            Junction fromJ, Junction toJ, int lanes) {

        super(identifier, len, spLimit, fromJ, toJ);
        numLanes = lanes;
    }
    
    // ** MÉTODOS COMPLEMENTARIOS DE AVANCE ** //
    /**
     * Calcula la velocidad base de la {@code HighwayRoad}:   
     * el mínimo entre la velocidad de congestión y el límite 
     * de velocidad {@code speedLimit}.
     * 
     * @return  la velocidad base de 
     *          la {@code HighwayRoad}.
     */
    @Override
    protected int getBaseSpeed() {
        // Cálculo de velocidadBase según la fórmula
        int congestionSpeed = ( ( speedLimit * numLanes ) / ( Math.max(vehiclesOnRoad.size(), 1) ) ) + 1;

        return ( Math.min(speedLimit, congestionSpeed) );
    }

    /**
     * <p>
     * Modifica la velocidad que llevarán los {@code Vehicle}s 
     * en la {@code HighwayRoad} previo avance.
     * </p> <p>
     * En la {@code HighwayRoad}, el <code>reductionFactor</code> 
     * es inicialmente {@code 1} y aumenta a {@code 2} si 
     * el número de {@code Vehicle}s averiados supera al 
     * número de carriles {@code numLanes}.
     * </p>
     * 
     * @param onRoad    - lista de {@code Vehicle}s 
     *                  en <code>DirtRoad</code>
     */
    @Override
    protected void vehicleSpeedModifier(ArrayList<Vehicle> onRoad) {
        // Velocidad máxima a la que pueden avanzar los vehículos.
        int baseSpeed = getBaseSpeed();

        // Factor de reducción de velocidad en caso de obstáculos delante.
        int reductionFactor = 1;

        // Número de vehículos averiados.
        int brokenVehicles = 0;

        // Se modifica la velocidad a la que avanzarán los vehículos,
        // teniendo en cuenta el factor de reducción.
        for (Vehicle v : onRoad) {
            v.setSpeed(baseSpeed / reductionFactor);

            if (v.getBreakdownTime() > 0) {
                brokenVehicles += 1;
            }

            if (brokenVehicles >= numLanes) {
                reductionFactor = 2;
            }
        }
    }
    
    // ** MÉTODO DE INFORME ** //
    /**
     * Genera una {@code IniSection} que informa de los 
     * atributos de la {@code HighwayRoad} en el 
     * tiempo del simulador.
     * 
     * @param simTime   - tiempo del simulador
     * 
     * @return          {@code IniSection} con información
     *                  de la {@code HighwayRoad}
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
		return "lanes";
	}
}