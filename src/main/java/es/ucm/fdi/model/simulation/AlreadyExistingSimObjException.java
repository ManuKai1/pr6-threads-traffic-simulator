package es.ucm.fdi.model.simulation;

/**
 * Excepción utilizada cuando se procede a introducir
 * un nuevo elemento en la simulación que ya existe.
 */
@SuppressWarnings("serial")
public class AlreadyExistingSimObjException extends SimulationException {

    public AlreadyExistingSimObjException(String info) {
        super(info);
    }

}