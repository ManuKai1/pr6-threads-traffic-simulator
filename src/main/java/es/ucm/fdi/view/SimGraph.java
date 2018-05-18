package es.ucm.fdi.view;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import es.ucm.fdi.extra.graphlayout.*;
import es.ucm.fdi.model.simobj.Junction;
import es.ucm.fdi.model.simobj.Road;
import es.ucm.fdi.model.simobj.Vehicle;
import es.ucm.fdi.model.simulation.RoadMap;

/**
 * Clase que representa un panel en Swing con una
 * grafo que representa el estado de los objetos 
 * de la simulación.
 */
@SuppressWarnings("serial")
public class SimGraph extends JPanel {

    // ** ATRIBUTOS ** //
    /**
     * El grafo.
     */
    private GraphComponent _graphComp;

    /**
     * El mapa de la simulación.
     */
    private RoadMap roadMap;

    // ** CONSTRUCTOR ** //
    /**
     * Constructos de {@link SimGraph} que recibe el
     * {@code RoadMap} de una simulación.
     */
    public SimGraph(RoadMap map) {
        roadMap = map;
        initGUI();
    }

    private void initGUI() {
        _graphComp = new GraphComponent();

        generateGraph();

        this.add(_graphComp);
        this.setVisible(true);
    }

    // ** MÉTODO DE GENERACIÓND DEL GRAFO ** //
    /**
     * Genera un {@code Graph} a partir del {@code _roadMap} 
     * guardado como atributo y se pasa al atributo
     * {@code _graphComp}.
     */
    public void generateGraph() {
        // Nuevo grafo y mapa Junction-Node.
        Graph graph = new Graph();
        Map<Junction, Node> junctToNode = new HashMap<>();

        // Se añaden las Junction (nodos) al grafo.
        for ( Junction j : roadMap.getJunctions().values() ) {
            Node n = new Node( j.getID() );
            junctToNode.put(j, n);

            graph.addNode(n);
        }

        // Se añaden las Roads (aristas) al grafo
        // junto con los Vehicles (puntos) en ellas
        for ( Road r : roadMap.getRoads().values() ) {
            // Nueva arista
            Edge e = new Edge( 
                r.getID(),
                junctToNode.get( r.getFromJunction() ),
                junctToNode.get( r.getToJunction() ),
                r.getLength(),
                r.isGreen()
            );

            // Puntos en la arista
            for ( Vehicle v : r.getRoadVehicles() ) {
                Dot d = new Dot( v.getID(), v.getLocation(), v.isFaulty() );
                
                e.addDot(d);
            }

            graph.addEdge(e);
        }

        // Se pasa el grafo al GraphComponent
        _graphComp.setGraph(graph);
    }
    
}