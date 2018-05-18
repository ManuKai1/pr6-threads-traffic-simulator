package es.ucm.fdi.model.simulation;

import java.util.LinkedHashMap;
import java.util.Map;

import es.ucm.fdi.model.simobj.Junction;
import es.ucm.fdi.model.simobj.Road;
import es.ucm.fdi.model.simobj.Vehicle;

/**
 * Clase que guarda los {@link SimObj} introducidos durante
 * la simulación, que la utiliza como base para realizar
 * sus cálculos.
 */
public class RoadMap {

    // ** MAPA ** //
    private Map<String, Junction>   junctionObjects = new LinkedHashMap<>();
    private Map<String, Road>       roadObjects = new LinkedHashMap<>();
    private Map<String, Vehicle>    vehicleObjects = new LinkedHashMap<>();

    // ** CONSTRUCTOR ** //
    /**
     * Constructor vacío de {@link RoadMap}
     */
    public RoadMap() {
        /*NADA*/
    }

    // ** MÉTODOS DE ACCESO ** //
    /**
     * Devuelve el mapa de <code>Roads</code>.
     * 
     * @return <code>roadObjects</code>
     */
    public Map<String, Road> getRoads() {
        return roadObjects;
    }

    /**
     * Devuelve el mapa de <code>Junctions</code>.
     * 
     * @return <code>junctionObjects</code>
     */
    public Map<String, Junction> getJunctions() {
        return junctionObjects;
    }
    
    /**
     * Devuelve el mapa de <code>Vehicles</code>.
     * 
     * @return <code>vehicleObjects</code>
     */
    public Map<String, Vehicle> getVehicles() {
        return vehicleObjects;
    }

    // ** MÉTODOS DE ADICIÓN ** //
    /**
     * Añade una <code>Junction</code> al mapa 
     * de <code>Junctions</code>.
     * 
     * @param newJunction   <code>Junction</code> a añadir
     */
    public void addJunction(Junction newJunction) {
        junctionObjects.put(newJunction.getID(), newJunction);
    }

    /**
     * Añade una <code>Road</code> al mapa 
     * de <code>Roads</code>.
     * 
     * @param newRoad <code>Road</code> a añadir
     */
    public void addRoad(Road newRoad) {
        roadObjects.put(newRoad.getID(), newRoad);
    }
    
    /**
     * Añade un <code>Vehicle</code> al mapa
     * de <code>Vehicles</code>.
     * 
     * @param newVehicle <code>Vehicle</code> a añadir
     */
    public void addVehicle(Vehicle newVehicle) {
        vehicleObjects.put(newVehicle.getID(), newVehicle);
    }

    // ** MÉTODOS DE COMPROBACIÓN ** //
    /**
     * Comprueba si existe una determinada <code>Junction</code>
     * en el mapa de la simulación.
     * 
     * @param id    id de la <code>Junction</code> 
     *              buscada
     * @return      if <code>Junction</code> found
     */
    public boolean existsJunctionID(String id) {
    	//O(1)
       return junctionObjects.containsKey(id);
    }

    /**
     * Comprueba si existe una determinada <code>Road</code>
     * en el mapa de la simulación.
     * 
     * @param id    id de la <code>Road</code> 
     *              buscada
     * @return      if <code>Road</code> found
     */
    public boolean existsRoadID(String id) {
    	//O(1)
       return roadObjects.containsKey(id);
    }

    /**
     * Comprueba si existe un determinado <code>Vehicle</code> 
     * en el mapa de la simulación.
     * 
     * @param id    id del <code>Vehicle</code>    
     *              buscado
     * @return      if <code>Vehicle</code> found
     */
    public boolean existsVehicleID(String id) {
    	//O(1)
    	return vehicleObjects.containsKey(id);
    }
    
    // ** MÉTODOS DE BÚSQUEDA ** //
    /**
     * Método que busca un <code>Vehicle</code> en el 
     * mapa de la simulación a partir de un id dado. 
     * Devuelve el <code>Vehicle</code> buscado si lo 
     * encuentra y <code>null</code> en caso contrario.
     * 
     * @param id    id del <code>Vehicle</code> buscado
     * @return      <code>Vehicle</code> buscado 
     *              o <code>null</code>
     */
    public Vehicle getVehicleWithID(String id) {
    	//O(1)
        return vehicleObjects.get(id);
    }

    /**
     * Método que busca una <code>Junction</code> en el 
     * mapa de la simulación a partir de un id dado. 
     * Devuelve la <code>Junction</code> buscado si la 
     * encuentra y <code>null</code> en caso contrario.
     * 
     * @param id    id de la <code>Junction</code> buscado
     * @return      <code>Junction</code> buscado 
     *              o <code>null</code>
     */
    public Junction getJunctionWithID(String id) {
    	//O(1)
    	return junctionObjects.get(id);
    }
    
    /**
     * Método que busca una <code>Road</code> en el 
     * mapa de la simulación a partir de un id dado. 
     * Devuelve la <code>Road</code> buscado si la 
     * encuentra y <code>null</code> en caso contrario.
     * 
     * @param id    id de la <code>Road</code> buscado
     * @return      <code>Road</code> buscado 
     *              o <code>null</code>
     */
    public Road getRoadWithID(String id) {
    	//O(1)
    	return roadObjects.get(id);
    }

    // ** MÉTODO DE CLEAR ** //
    /**
     * Método que limpia <code>RoadMap</code> de todos
     * los <code>SimObj</code> introducidos durante la
     * simulación.
     */
    public void clear() {
    	junctionObjects.clear();
    	roadObjects.clear();
    	vehicleObjects.clear();
    }

    
}