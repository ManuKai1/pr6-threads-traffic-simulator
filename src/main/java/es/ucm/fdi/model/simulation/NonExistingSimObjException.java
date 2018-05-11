package es.ucm.fdi.model.simulation;

/**
 * Excepción utilizada cuando se procede a, o bien modificar 
 * un determinado elemento que no existe en la simulación, 
 * o bien a introducir un nuevo elemento dependiente de elementos 
 * existentes que no aparecen en la simulación.
 */
@SuppressWarnings("serial")
public class NonExistingSimObjException extends SimulationException {

    public NonExistingSimObjException(String info) {
        super(info);
    }

}