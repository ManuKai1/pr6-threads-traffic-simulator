package es.ucm.fdi.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.ParseException;

import es.ucm.fdi.control.evbuild.EventParser;
import es.ucm.fdi.ini.*;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.simulation.SimulationException;
import es.ucm.fdi.model.simulation.TrafficSimulation;
import es.ucm.fdi.model.simulation.TrafficSimulation.*;

/**
 * <p>
 * Clase utilizada como controlador del programa.
 * </p> <p>
 * En su método {@link #execute()} crea un simulador
 * {@link TrafficSimulation} a partir de un archivo
 * {@code .ini} que almacena los {@link Event Events}.
 * </p> <p>
 * El simulador se actualiza durante un tiempo determinado
 * {@link #batchTimeLimit}, volcando los resultados en un flujo
 * de salida {@link #outStream}.
 * </p>
 */
public class Controller {
    
    // ** ATRIBUTOS ** //
    /**
     * Archivo {@code .ini} dividido en 
     * {@code IniSection}s del que se extraen 
     * los {@code Event}s de la simulación.
     */
    private Ini iniInput;

    /**
     * Flujo de salida donde se vuelcan los datos
     * del simulador tras cada actualización.
     */
    private OutputStream outStream;

    /**
     * Número de ticks que se ejecuta el simulador 
     * en modo {@code batch}.
     */
    private int batchTimeLimit;

    /**
     * Simulación a la que el controlador tiene acceso.
     */
    private TrafficSimulation simulator;
    
    /**
     * Usado para mostrar errores por consola.
     */
    private class BatchListener implements Listener {

		@Override
		public void update(UpdateEvent ue, String error) {
			switch(ue.getEvent()) {
                case ERROR: 
                    System.err.println(error);
                    break;
                default: 
                    break;
			}
		}
    }

    // ** CONSTRUCTOR ** //
    /**
     * Constructor de {@link Controller} que recibe 
     * el archivo {@code .ini}, el flujo de salida
     * y el tiempo límite de ejecución.
     * 
     * @param in    - {@code Ini} con el archivo 
     *              {@code .ini}
     * @param out   - {@code OutputStream} donde 
     *              se vuelcan los datos
     * @param time  - tiempo límite de ejecución
     */
    public Controller(Ini in, OutputStream out, int time) {
        iniInput = in;
        outStream = out;
        batchTimeLimit = time;
        simulator = new TrafficSimulation();
    }

    // ** EJECUCIÓN EN BATCH ** //
    /**
     * <p>
     * Método de ejecución en modo {@code batch} que:
     * </p> <p>
     * 1. Crea una {@code TrafficSimulation} y un 
     * {@code EventParser}.
     * </p> <p>
     * 2. Recorre las secciones de {@code iniInput} 
     * guardando los eventos en la simulación.
     * </p> <p>
     * 3. Ejecuta la {@code TrafficSimulation}.
     * </p> 
     *
     * @throws ParseException                   if event parsing failed 
     *                                          (no matching event or 
     *                                          invalid data)
     * @throws IllegalArgumentException         if event time is lower 
     *                                          than sim time
     * @throws SimulationException              if an error ocurred during 
     *                                          the execution of events in 
     *                                          the simulation
     * @throws IOException                      if an error ocurred during 
     *                                          report generation in the 
     *                                          simulation
     */
    public void executeBatch() 
            throws  ParseException, IllegalArgumentException, 
                    SimulationException, IOException {

        // 1 //
        // Recorre las secciones del archivo .ini de entrada
        // y construye y guarda los eventos en el simulador.
        try {
            pushEvents();
        }
        catch (ParseException e1) {
            throw e1;
        }
        catch (IllegalArgumentException e2) {
            throw e2;
        }
        
        BatchListener error = new BatchListener();
        simulator.addSimulatorListener(error);
        
        // 2 // 
        // Se ejecuta el simulador el número de pasos batchTimeLimit
        // y se actualiza el OutputStream.
        try {
			simulate(batchTimeLimit);
		}
        catch (IOException e4) {
			throw e4;
		} 
    }

    // ** MÉTODOS DE SIMULACIÓN ** //
    /**
     * Carga los eventos del archivo de entrada
     * {@code iniInput} en el {@code simulator}.
     * 
     * @throws ParseException               if event parsing failed 
     *                                      (no matching event or 
     *                                      invalid data)
     * @throws IllegalArgumentException     if event time is lower 
     *                                      than sim time   
     */
    public void pushEvents() 
            throws ParseException, IllegalArgumentException {
        
        EventParser parser = new EventParser();

        for ( IniSection sec : iniInput.getSections() ) {
        	Event ev;
            
            try {
        		ev = parser.parse(sec);
                
        	}
            catch (IllegalArgumentException e) {
            	throw new ParseException(
                    "Event parsing failed:\n" + 
                    		e.getMessage());
            }

            try {
                simulator.pushEvent(ev);   
            }
            catch (IllegalArgumentException e) {
                throw e; // Illegal time
            }            
        }
    }

    /**
     * Ejecuta el simulador durante un tiempo
     * determinado {@code time}.
     * 
     * @param time -    número de ticks que se
     *                  ejecutará el simulador
     * 
     * @throws SimulationException              if an error ocurred during 
     *                                          the execution of events in 
     *                                          the simulation
     * @throws IOException                      if an error ocurred during 
     *                                          report generation in the 
     *                                          simulation
     */
    public void simulate(int time) 
            throws SimulationException, IOException {

        try {
			simulator.execute(time, outStream);
		}
        catch (IOException e) {
			throw e;
		} 
    }

    // ** SETTERS/GETTERS ** //
    /**
     * Cambia el archivo {@code Ini} de entrada
     * dado un {@code InputStream}
     * 
     * @param is    - {@ InputStream} a partir del que
     *              se genera el nuevo {@code Ini}
     * 
     * @throws IOException  fallo al leer {@code is}
     */
    public void setIniInput(InputStream is) throws IOException {
        try {
			iniInput = new Ini(is);
		} catch (IOException e) {
			throw e;
		}
    }

    /**
     * Devuelve el tiempo actual de ejecución
     * del simulador.
     * 
     * @return  tiempo de ejecución actual
     */
    public int getExecutionTime() {
        return simulator.getCurrentTime();
    }

    /**
     * Devuelve el simulador del controlador.
     * 
     * @return  {@code TrafficSimulation} con el
     *          simulador
     */
	public TrafficSimulation getSimulator() {
		return simulator;
	}
}
