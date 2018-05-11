package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import org.apache.commons.cli.ParseException;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;

import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.SimObject;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.simulation.RoadMap;
import es.ucm.fdi.model.simulation.TrafficSimulation.Listener;
import es.ucm.fdi.model.simulation.TrafficSimulation.UpdateEvent;

import es.ucm.fdi.util.Describable;
import es.ucm.fdi.util.MultiTreeMap;
import es.ucm.fdi.util.TableDataType;

/**
 * JFrame que representa la interfaz gráfica de la 
 * simulación.
 */
@SuppressWarnings("serial")
public class SimWindow extends JFrame implements Listener {
	
	// ** CONSTANTES ** //
	//Para la ventana
	private final int DEF_HEIGHT = 1000, DEF_WIDTH = 1000;
	
	//Para los SplitPane
	private final double VERTICAL_SPLIT = 0.3, HORIZONTAL_SPLIT = 0.5;
	
	//Para el Spinner
	private final int INITIAL_STEPS = 1;
	private final int MIN_TIME = 1;
	private final int MAX_TIME = 500;
	
	//Para las áreas de texto
	private final String EVENTS_TITLE = "Events";
	private final String REPORTS_TITLE = "Reports";
	
	//Para el menu contextual de eventos
	private final String FRIENDLY_KEY = "friendly";

	// Para las tablas.
	private final TableDataType[] eventDataHeaders = {
			TableDataType.E_NUM,
			TableDataType.E_TIME,
			TableDataType.E_TYPE
	};

	private final TableDataType[] junctionDataHeaders = {
			TableDataType.REPORT,
			TableDataType.ID,
			TableDataType.J_TYPE,
			TableDataType.J_GREEN,
			TableDataType.J_RED,
	};

	private final TableDataType[] roadDataHeaders = {
			TableDataType.REPORT,
			TableDataType.ID,
			TableDataType.R_TYPE,
			TableDataType.R_SOURCE,
			TableDataType.R_TARGET,
			TableDataType.R_LENGHT,
			TableDataType.R_MAX,
			TableDataType.R_STATE,
	};

	private final TableDataType[] vehicleDataHeaders = {
			TableDataType.REPORT,
			TableDataType.ID,
			TableDataType.V_TYPE,
			TableDataType.V_ROAD,
			TableDataType.V_LOCATION,
			TableDataType.V_SPEED,
			TableDataType.V_KM,
			TableDataType.V_FAULTY,
			TableDataType.V_ROUTE
	};






	// ** ATRIBUTOS ** //
	private Controller control;
	private OutputStream reports = null;



	// ** PANELES ** //
	// Panel de eventos e informes
	private JPanel eventsAndReports = new JPanel( new GridLayout(1, 3));
	// Panel de las tablas de los objetos
	private JPanel tablesPanel = new JPanel( new GridLayout(3, 1));
	// Panel del grafo
	private JPanel graphPanel = new JPanel();
	
	// Panel dividido (horizontal)
	// Izquierda: tablas
	// Derecha: grafo
	private JSplitPane tablesAndGraph = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT, tablesPanel, graphPanel);
	
	// Panel dividido (vertical)
	// Abajo: tablas y grafo
	// Arriba eventos e informes
	private JSplitPane lowAndTop = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT, eventsAndReports, tablesAndGraph);



	// ** BARRAS DE MENÚ Y HERRAMIENTAS ** //	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu simulatorMenu = new JMenu("Simulator");
	private JMenu reportsMenu = new JMenu("Reports");
	
	private JToolBar toolBar = new JToolBar();
	


	// ** SELECTOR DE ARCHIVOS ** //
	private JFileChooser fileChooser = new JFileChooser();;
	private File currentFile = null;
	


	// ** SPINNER (SELECCIÓN DE TIEMPO DE EJECUCIÓN) ** //
	private JSpinner stepsSpinner = new JSpinner();	
	private JTextField timeViewer = new JTextField("" + 0, 5);
	
	// ** ÁREAS DE TEXTO (EVENTOS E INFORMES) ** //
	private JTextArea eventsTextArea = new JTextArea();
	private JTextArea reportsTextArea = new JTextArea();
	
	private JLabel infoText = new JLabel("Simulator initialized correctly.");
	
	// ** TABLAS ** //
	private SimTable eventsTable;
	private SimTable junctionsTable;	
	private SimTable roadsTable;
	private SimTable vehiclesTable;

	// ** GRAFO ** //
	private SimGraph simGraph;





	
	// ** ACCIONES ** //
	// Recordar activarlas y desactivarlas
	private SimulatorAction load =
			new SimulatorAction("Load Events", "open.png", 
					"Load an events file",
					KeyEvent.VK_L, "control shift L", 
					() -> loadFile());
	
	private SimulatorAction save =
			new SimulatorAction("Save Events", "save.png", 
					"Save an events file",
					KeyEvent.VK_S, "control shift S", 
					() -> saveFile(eventsTextArea));
	
	private SimulatorAction clear = 
			new SimulatorAction("Clear Events", "clear.png",
					"Clear event zone",
					KeyEvent.VK_C, "control shift C", 
					() -> clearEvents());
	
	private SimulatorAction insertEvents = 
			new SimulatorAction("Insert Events", "events.png",
					"Add events to simulation",
					KeyEvent.VK_E, "control shift E", 
					() -> eventsToSim());
	
	private SimulatorAction run =
			new SimulatorAction("Run", "play.png", 
					"Run the simulator",
					KeyEvent.VK_P, "control shift P", 
					() -> runSimulator());

	private SimulatorAction reset =
			new SimulatorAction("Reset", "reset.png",
					"Reset the simulator",
					KeyEvent.VK_R, "control shift R", 
					() -> resetSimulator());
	
	private SimulatorAction generateRep =
			new SimulatorAction("Generate Reports", "report.png",
					"Report generator",
					KeyEvent.VK_G, "control shift G", 
					() -> generateReports());
	
	private SimulatorAction clearRep =
			new SimulatorAction("Clear Reports", "delete_report.png",
					"Clears reports",
					KeyEvent.VK_D, "control shift D", 
					() -> clearReports());
	
	private SimulatorAction saveRep =
			new SimulatorAction("Save Reports", "save_report.png",
					"Save reports to file",
					KeyEvent.VK_F, "control shift F", 
					() -> saveFile(reportsTextArea));
	
	private SimulatorAction changeOutput =
			new SimulatorAction("Change Output", "report.png",
					"Redirects output to reports area",
					KeyEvent.VK_O, "control shift O", 
					() -> changeOutput());
	
	private SimulatorAction exit =
			new SimulatorAction("Exit", "exit.png",
					"Exit the simulator",
					KeyEvent.VK_ESCAPE, "control shift ESC", 
					() -> quit());
	
	
	// ** CLASE INTERNA ** //
	/**
	 * Clase interna que representa la
	 * redirección de los reports a su área de texto.
	 */
	private class ReportStream extends OutputStream {

		public void write(int arg0) throws IOException {
			reportsTextArea.append("" + (char) arg0);			
		}
		
	}














	// ** CONSTRUCTOR E INICIALIZACIÓN DE GUI ** //
	/**
	 * Constructor de {@link SimWindow} dado un {@code Controller} 
	 * y un posible fichero de entrada.
	 * 
	 * @param ctrl			- controlador a usar
	 * @param inFileName 	- fichero de entrada de eventos
	 */
	public SimWindow(Controller ctrl, String inFileName) {
		super("Traffic Simulator");
		control = ctrl;
		initGUI();
		
		if(inFileName != null){
			currentFile = new File(inFileName);
			fileToEvents();
		}
		control.getSimulator().addSimulatorListener(this);
	}

	/**
	 * Inicialización completa de la interfaz.
	 */
	private void initGUI() {

		initPanels();

		addComponentsToLayout();

		// Añade configuraciones de la ventana principal
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(DEF_WIDTH, DEF_HEIGHT);
		setVisible(true);
		tablesAndGraph.setDividerLocation(HORIZONTAL_SPLIT);
		lowAndTop.setDividerLocation(VERTICAL_SPLIT);
	}

	/**
	 * Crea el layout central de la ventana principal.
	 * (La toolbar y la zona de información se 
	 * añaden en su propia función).
	 */
	private void initPanels() {
		tablesAndGraph.setResizeWeight(.5);
		lowAndTop.setResizeWeight(.5);
		add(lowAndTop, BorderLayout.CENTER);
	}
	
	/**
	 * Añade los componentes principales
	 * a la interfaz.
	 */
	private void addComponentsToLayout(){
		addMenuBar(); // barra de menus
		addToolBar(); // barra de herramientas
		addEventsEditor(); // editor de eventos
		addEventsView(); // cola de eventos
		addReportsArea(); // zona de informes
		addVehiclesTable(); // tabla de vehiculos
		addRoadsTable(); // tabla de carreteras
		addJunctionsTable(); // tabla de cruces
		addMap(); // mapa de carreteras
		addInfoZone(); // barra de estado
		addEditor(); // menu contextual eventos
	}
	
	
	
	





	// ** MÉTODOS DE COMPONENTES ** //
	/**
	 * Función que crea la barra de menú.
	 */
	private void addMenuBar() {
		fileMenu.add(load);
		fileMenu.add(save);
		fileMenu.add(clear);
		fileMenu.addSeparator();
		fileMenu.add(exit);
		
		simulatorMenu.add(run);
		simulatorMenu.add(reset);
		
		
		reportsMenu.add(generateRep);
		reportsMenu.add(saveRep);
		reportsMenu.add(clearRep);
		reportsMenu.addSeparator();
		JCheckBoxMenuItem redirectOutput = 
				new JCheckBoxMenuItem(changeOutput);
		reportsMenu.add(redirectOutput);
		
		menuBar.add(fileMenu);
		menuBar.add(simulatorMenu);
		menuBar.add(reportsMenu);
		
		setJMenuBar(menuBar);
	}

	/**
	 * Función que crea la barra de herramientas.
	 * Además, deshabilita algunas acciones al comienzo.
	 */
	private void addToolBar() {
		toolBar.addSeparator();
		
		toolBar.add(load);
		toolBar.add(save);
		toolBar.add(clear);
		save.setEnabled(false);
		clear.setEnabled(false);
		
		toolBar.addSeparator();
		
		toolBar.add(insertEvents);
		toolBar.add(run);
		toolBar.add(reset);
		insertEvents.setEnabled(false);
		run.setEnabled(false);
		reset.setEnabled(false);
		
		toolBar.addSeparator();
		
		toolBar.add(new JLabel("  Steps:  "));
		stepsSpinner.setModel(new SpinnerNumberModel(INITIAL_STEPS, MIN_TIME, MAX_TIME, 1));
		toolBar.add(stepsSpinner);
		toolBar.add(new JLabel("  Current Time:  "));
		timeViewer.setEditable(false);
		toolBar.add(timeViewer);
		
		toolBar.addSeparator();
		
		toolBar.add(generateRep);
		toolBar.add(clearRep);
		toolBar.add(saveRep);
		generateRep.setEnabled(false);
		clearRep.setEnabled(false);
		saveRep.setEnabled(false);
		
		toolBar.addSeparator();
		
		toolBar.add(exit);
		
		add(toolBar, BorderLayout.PAGE_START);
	}
	
	/**
	 * Creación de la zona de escritura de eventos.
	 */
	private void addEventsEditor(){
		eventsTextArea.setEditable(true);
		eventsTextArea.setLineWrap(true);
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border borderWithTitle = BorderFactory.createTitledBorder(lineBorder, EVENTS_TITLE);
		eventsTextArea.setFont(new Font("Verdana", Font.PLAIN, 12));
		eventsTextArea.setBorder(borderWithTitle);
		//Se activan y desactivan los botones según
		//esté vacío el área de texto.
		eventsTextArea.getDocument().addDocumentListener(
				new DocumentListener(){
			
					public void changedUpdate(DocumentEvent e) {
		                if (eventsTextArea.getText().isEmpty()) {
		                    disableEventButtons();
		                } else {
		                    enableEventButtons();
		                }
		            }

					@Override
					public void insertUpdate(DocumentEvent arg0) {
						if (eventsTextArea.getText().isEmpty()) {
		                    disableEventButtons();
		                } else {
		                    enableEventButtons();
		                }
					}

					@Override
					public void removeUpdate(DocumentEvent arg0) {
						if (eventsTextArea.getText().isEmpty()) {
		                    disableEventButtons();
		                } else {
		                    enableEventButtons();
		                }
					}
		});
		
		eventsAndReports.add(new JScrollPane(eventsTextArea,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}

	/**
	 * Creación de la tabla de eventos.
	 */
	private void addEventsView() {
		MultiTreeMap<Integer, Event> eventsMap = control.getSimulator().getEvents();
		List<Event> eventsList = eventsMap.valuesList();

		eventsTable = new SimTable(eventDataHeaders, eventsList);
		
		eventsAndReports.add(eventsTable);
	}
	
	/**
	 * Creación de la zona de informes.
	 */
	private void addReportsArea() {
		reportsTextArea.setEditable(false);
		reportsTextArea.setLineWrap(true);
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border borderWithTitle = BorderFactory.createTitledBorder(lineBorder, REPORTS_TITLE);
		reportsTextArea.setFont(new Font("Verdana", Font.PLAIN, 12));
		reportsTextArea.setBorder(borderWithTitle);
		eventsAndReports.add(new JScrollPane(reportsTextArea,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}
	
	/**
	 * Creación de la tabla de {@code Junction}s.
	 */
	private void addJunctionsTable() {
		List<Junction> junctions = new ArrayList<>(
			control.getSimulator().getRoadMap().getJunctions().values()
		);
		
		junctionsTable = new SimTable(junctionDataHeaders, junctions);

		tablesPanel.add(junctionsTable);	
	}
	
	/**
	 * Creación de la tabla de {@code Road}s.
	 */
	private void addRoadsTable() {
		List<Road> roads = new ArrayList<>(
			control.getSimulator().getRoadMap().getRoads().values()
			);
			
		roadsTable = new SimTable(roadDataHeaders, roads);
		tablesPanel.add(roadsTable);
	}
	
	/**
	 * Creación de la tabla de {@code Vehicle}s
	 */
	private void addVehiclesTable() {
		List<Vehicle> vehicles = new ArrayList<>(
			control.getSimulator().getRoadMap().getVehicles().values()
			);
			
		vehiclesTable = new SimTable(vehicleDataHeaders, vehicles);
		tablesPanel.add(vehiclesTable);
	}
	
	/**
	 * Creación del grafo.
	 */
	private void addMap() {	
		RoadMap map = control.getSimulator().getRoadMap();

		simGraph = new SimGraph(map);
		graphPanel.add(simGraph);
	}
	
	/**
	 * Creación de la barra de información
	 */
	private void addInfoZone() {
		JPanel infoZone = new JPanel();
		infoZone.setLayout(new FlowLayout(FlowLayout.LEFT));
		infoZone.add(infoText);
		add(infoZone, BorderLayout.PAGE_END);
	}
	
	/**
	 * Creación del menú contextual para
	 * añadir eventos predeterminados.
	 */
	private void addEditor() {
		JPopupMenu editorPopupMenu = new JPopupMenu();
		editorPopupMenu.add(load);
		editorPopupMenu.add(save);
		editorPopupMenu.add(clear);
		editorPopupMenu.addSeparator();
		
		JMenu subMenu = new JMenu("Insert");
		
		try {
			//Fichero de plantillas
			Ini ini = new Ini(new FileInputStream(
					"src/main/resources/util/templates.ini"));
			List<IniSection> sections = ini.getSections();
			//Creación de botones del menú
			for(IniSection section : sections){
				JMenuItem insert = new JMenuItem(section.
						getValue(FRIENDLY_KEY));
				section.eraseKey(FRIENDLY_KEY);
				insert.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						eventsTextArea.insert(section.toString(), 
								eventsTextArea.getCaretPosition());
					}
				});
				subMenu.add(insert);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"templates.ini file missing.", "Templates error",
					JOptionPane.WARNING_MESSAGE);
		}
		editorPopupMenu.add(subMenu);
		//Al hacer clic derecho
		eventsTextArea.addMouseListener(new MouseListener() {

			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger() && editorPopupMenu.isEnabled()) {
					editorPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
	}












	// ** MÉTODO DE UPDATE DE LA GUI ** //
	/**
	 * Recibe eventos y actualiza la GUI convenientemente.
	 */
	@Override
	public void update(UpdateEvent ue, String error) {
		switch(ue.getEvent()){
		case NEW_EVENT :
			List<Event> addedEvents = ue.getEventQueue().valuesList();
			eventsTable.setList(addedEvents);
			infoText.setText("Events added to the simulator.");
			break;
		case ADVANCED :
			timeViewer.setText("" + (control.getExecutionTime() + 1));
			
			List<Junction> addedJunctions = 
				new ArrayList<Junction>(ue.getRoadMap().
						getJunctions().values());
			junctionsTable.setList(addedJunctions);
			
			List<Vehicle> addedVehicles = 
				new ArrayList<Vehicle>(ue.getRoadMap().
						getVehicles().values());
			vehiclesTable.setList(addedVehicles);
			
			List<Road> addedRoads = 
				new ArrayList<Road>(ue.getRoadMap().
						getRoads().values());
			roadsTable.setList(addedRoads);
			
			simGraph.generateGraph();
			
			infoText.setText("Simulation playing...");
			break;
		case RESET :
			clearReports();
			generateRep.setEnabled(false);
			reset.setEnabled(false);
			run.setEnabled(false);
			eventsTable.clear();
			junctionsTable.clear();
			roadsTable.clear();
			vehiclesTable.clear();
			simGraph.generateGraph();
			timeViewer.setText("" + control.getExecutionTime());
			infoText.setText("Simulation reset.");
			break;
		case ERROR:
			JOptionPane.showMessageDialog(this,
					error, "Simulator error", JOptionPane.WARNING_MESSAGE);
			resetSimulator();
			break;
		default : break;
		}
	}

	














	// ** MÉTODOS DE ACCIONES ** //
	/**
	 * Carga en su área correspondiente un fichero de eventos.
	 */
	private void loadFile() {
		// Abre la ventana del selector y espera la respuesta
		// del usuario.
		int returnValue = fileChooser.showOpenDialog(this);
		// Si fue un éxito
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			currentFile = fileChooser.getSelectedFile();
			fileToEvents();
		}
	}

	/**
	 * Guarda en un fichero el texto de cierto recuadro.
	 * 
	 * @param fromArea 	- área de la que se 
	 * 					recibe el texto
	 */
	private void saveFile(JTextArea fromArea) { 
		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION){
			// Creación del OutputStream
			File outFile = null;
			OutputStream os = null;
			try {
				outFile = fileChooser.getSelectedFile();
				os = new FileOutputStream(outFile);
					StringBuilder edited = new StringBuilder();
				edited.append(fromArea.getText());
					os.write(edited.toString().getBytes());	
				//Mensaje de éxito
				JOptionPane.showMessageDialog(this,
					"The file was saved.");
				infoText.setText("File saved.");
					
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,
						"Error while saving the file.");
			}
			finally {
				try {
					os.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this,
							"Error while saving the file.");
				}
			}
		}
	}

	/**
	 * Limpia la zona de eventos
	 */
	private void clearEvents() {
		eventsTextArea.setText("");
		infoText.setText("Events cleared.");
	}

	/**
	 * Método que transfiere los eventos de
	 * la zona de texto al simulador.
	 */
	private void eventsToSim() {
		try {
			control.setIniInput(new ByteArrayInputStream(eventsTextArea.getText().
					getBytes()));
			control.pushEvents();
			run.setEnabled(true);
			reset.setEnabled(true);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		}
	}

	/**
	 * Método que ejecuta el simulador el número 
	 * de pasos que el usuario haya seleccionado.
	 */
	private void runSimulator() {
		try {
			generateRep.setEnabled(true);
			control.getSimulator().execute((int) stepsSpinner.getValue(),
					reports);

			// Se actualiza la tabla de eventos.
			int minTime = control.getSimulator().getCurrentTime();
			
			updateEventsTable(minTime);
			if(reports != null){
				clearRep.setEnabled(true);
				saveRep.setEnabled(true);
			}
		} catch (IOException e) {
			generateRep.setEnabled(false);
			JOptionPane.showMessageDialog(this,
					e.getMessage());
		}
	}
	
	/**
	 * Resetea el simulador
	 */
	private void resetSimulator() {
		control.getSimulator().reset();
	}

	/**
	 * Método que genera en su área los informes
	 * seleccionados, correspondientes al tiempo actual.
	 */
	private void generateReports() {
		// Lista de objetos seleccionados en las tablas.
		List<SimObject> objectsToReport = getSelectedObjects();

		String reports = 
			control.getSimulator().reportsToString(objectsToReport);
		
		// Se cargan los reports 
		reportsTextArea.setText(reports);
		clearRep.setEnabled(true);
		saveRep.setEnabled(true);
		infoText.setText("Generated reports of selected objects for current time.");
	}

	/**
	 * Método que limpia el área de informes.
	 */
	private void clearReports() {
		reportsTextArea.setText("");
		saveRep.setEnabled(false);
		clearRep.setEnabled(false);
		infoText.setText("Reports cleared.");
	}

	/**
	 * Alterna entre salida nula y salida
	 * a zona de reports.
	 */
	private void changeOutput(){
		if(reports == null){
			reports = new ReportStream();
		}
		else reports = null;
	}

	/**
	 * Método que pregunta en un cuadro de diálogo al
	 * usuario si desea salir, terminando el programa si
	 * lo confirma.
	 */
	private void quit() {
		int n = JOptionPane.showOptionDialog(
			new JFrame(),
			"Are you sure you want to quit?",
			"Quit",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null, null, null
		);

		if (n == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}	
	









	// ** MÉTODOS ADICIONALES ** //
	/**
	 * Método que escribe los datos de un
	 * fichero en la zona de eventos.
	 */
	private void fileToEvents() {
		try {
			//Lectura de fichero y paso de bytes a String
		    byte[] byteText = Files.readAllBytes(currentFile.toPath());
		    String text = new String(byteText);
		    eventsTextArea.setText(text);
		    infoText.setText("Events file loaded.");
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(this,
					"Error while loading the file.");
		}
	}	
	
	/**
	 * Activa los botones de eventos, para cuando se pueden
	 * utilizar.
	 */
	private void enableEventButtons() {
	    save.setEnabled(true);
	    clear.setEnabled(true);
	    insertEvents.setEnabled(true);
	}
	
	/**
	 * Desactiva los botones de eventos, para cuando no
	 * se pueden utilizar.
	 */
	private void disableEventButtons(){
	    save.setEnabled(false);
	    clear.setEnabled(false);
	    insertEvents.setEnabled(false);
	}	

	/**
	 * A partir de la lista de elementos de 
	 * {@code EventsTable}, descarta aquellos
	 * {@code Event} cuyo tiempo de ejecución
	 * es menor que {@code minTime}.
	 * 
	 * @see #runSimulator()
	 */
	private void updateEventsTable(int minTime) {

		ArrayList<? extends Describable> tableElements =
				new ArrayList<>( eventsTable.getTableElements() );
		Iterator<? extends Describable> iter = tableElements.iterator();

		while (iter.hasNext()) {
			Event e = (Event) iter.next();

			if (e.getTime() < minTime) {
				iter.remove();
			}
		}

		eventsTable.setList(tableElements);
	}

	/**
	 * Devuelve una lista con los {@code SimObj} seleccionados
	 * en las tablas de la {@code GUI}.
	 * 
	 * @return 	{@code List<SimObject} con los objetos
	 * 			seleccionados
	 */
	private List<SimObject> getSelectedObjects() {
		List<SimObject> reportObjects = new ArrayList<>();

		reportObjects.addAll( junctionsTable.getSelected() );
		reportObjects.addAll( roadsTable.getSelected() );
		reportObjects.addAll( vehiclesTable.getSelected() );


		return reportObjects;
	}
}
