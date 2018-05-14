package es.ucm.fdi.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.view.SimWindow;


public class ExampleMain {

	// ** ATRIBUTOS ** //
	/**
	 * Default time limit if none indicated by user.
	 */
	private final static Integer _TIMELIMIT_DEFAULT = 10;

	/**
	 * Default execution mode if none indicated by user.
	 */
	private final static String _MODE_DEFAULT = "batch";
	
	/**
	 * Execution time limit: number of ticks the simulator will do.
	 */
	private static Integer _timeLimit = null;

	/**
	 * {@code String} with the input file pathname.
	 */
	private static String _inFile = null;

	/**
	 * {@code String} with the output file pathname.
	 */
	private static String _outFile = null;

	/**
	 * Mode of execution: 'batch' or 'gui'.
	 */
	private static String _mode = null;

	
	
	
	
	// ** MAIN ** //
	public static void main(String[] args) {

		start(args);
	}

	// ** MÉTODOS DE PARSEO DE ARGS ** //
	/**
	 * Parses introduced {@code args}. If error found, a 
	 * {@code ParseException} is caught and the program 
	 * exits with {@code 1}.
	 * 
	 * @param args 	- arguments of the introduced 
	 * 				command line
	 */
	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseModeOption(line);
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseOutFileOption(line);
			parseStepsOption(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
	}

	/**
	 * Generates and returns a collection of possible 
	 * {@code Option}s to be used in a {@code CommandLine}.
	 * 
	 * @return 	collection of {@code Option}s
	 */
	private static Options buildOptions() {
		// Colección
		Options cmdLineOptions = new Options();

		// Comando de ayuda: -h; --help; "Print this message"
		cmdLineOptions.addOption(
			Option.builder("h")
			.longOpt("help")
			.desc("Print this message")
			.build()
		);
		
		// Comando de input: -i; --input; <arg.ini>; "Events input file"
		cmdLineOptions.addOption(
			Option.builder("i")
			.longOpt("input")
			.hasArg()
			.desc("Events input file")
			.build()
		);

		// Comando de modo: -m, --mode; <arg>; "'batch' for batch mode and 'gui' for GUI mode"
		cmdLineOptions.addOption(
			Option.builder("m")
			.longOpt("mode")
			.hasArg()
			.desc("'batch' for batch mode and 'gui' for GUI mode (default value is 'batch')")
			.build()
		);

		// Comando de salida: -o; --output; <arg.ini>; "Output file, where reports are written"
		cmdLineOptions.addOption(
			Option.builder("o")
			.longOpt("output")
			.hasArg()
			.desc("Output file, where reports are written.")
			.build()
		);
		
		// Comando de ticks: -t; --ticks; <x>; "Ticks to execute the simulator's main loop..."
		cmdLineOptions.addOption(
			Option.builder("t")
			.longOpt("ticks")
			.hasArg()
			.desc("Ticks to execute the simulator's main loop (default value is " + _TIMELIMIT_DEFAULT + ").")
			.build()
		);

		return cmdLineOptions;
	}

	/**
	 * Comprueba si el modo introducido es válido, si no,
	 * lanza una excepción. Si no se ha introducido ningún
	 * modo, se ejecuta el modo por defecto {@code _MODE_DEFAULT}.
	 * 
	 * @param line 	- {@code CommandLine} introduced
	 * 
	 * @throws ParseException 	if not a valid mode
	 * 							introduced
	 */
	private static void parseModeOption(CommandLine line) 
			throws ParseException {

		_mode = line.getOptionValue("m");
		
		if (_mode == null) {
			_mode = _MODE_DEFAULT;
		}

		if ( ! _mode.equals("batch") && ! _mode.equals("gui") ) {
			throw new ParseException("Not a valid execution mode.");
		}
	}


	/**
	 * If indicated, help is shown in console with 
	 * the help messages of all options available.
	 * 
	 * @param line 				- {@code CommandLine} introduced
	 * @param cmdLineOptions 	- collection of {@code Option}s 
	 * 							available
	 */
	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if ( line.hasOption("h") ) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(ExampleMain.class.getCanonicalName(), cmdLineOptions, true);
			
			System.exit(0);
		}
	}

	/**
	 * <p>
	 * Modifies the input file name attribute {@code _inFile} 
	 * with the one indicated in the command line, after 
	 * parsing it. 
	 * </p> <p>
	 * If in {@code GUI} mode, no input file
	 * is needed.
	 * </p>
	 * 
	 * @param line 	- {@code CommandLine} introduced
	 * 
	 * @throws ParseException 	if not a valid input 
	 * 							file name for batch
	 */
	private static void parseInFileOption(CommandLine line) 
			throws ParseException {

		_inFile = line.getOptionValue("i");
		if (_inFile == null) {
			if( ! _mode.equals("gui") ) {
				throw new ParseException("An events file is missing");
			}
		}
	}

	/**
	 * Modifies the output file name attribute {@code _outFile}
	 * with the one indicated in the command line
	 * 
	 * @param line 	- {@code CommandLine} introduced
	 */
	private static void parseOutFileOption(CommandLine line) {
		_outFile = line.getOptionValue("o");
	}

	/**
	 * <p>
	 * Stores the number of steps indicated by the command line and 
	 * in attribute {@code _timeLimit}.
	 * </p> <p>
	 * If no value is indicated, automatically set up to 
	 * {@code _TIMELIMIT_DEFAULT}
	 * </p>
	 * 
	 * @param line 	- {@code CommandLine} introduced
	 * 
	 * @throws ParseException 	if the time value 
	 * 							is not valid
	 */
	private static void parseStepsOption(CommandLine line) 
			throws ParseException {

		// Si no se ha introducido ningún valor, se toma por defecto.
		String t = line.getOptionValue("t", _TIMELIMIT_DEFAULT.toString());

		// Se comprueba que el valor introducido sea válido.
		try {
			_timeLimit = Integer.parseInt(t);
			assert (_timeLimit < 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time limit: " + t);
		}
	}

	// ** MÉTODOS DE TESTEO ** //
	/**
	 * <p>
	 * Runs the simulator on all files that end with {@code .ini} 
	 * in the given {@code path}, and compares the output to the 
	 * expected.
	 * </p> <p>
	 * It is assumed that for input file {@code example.ini} the 
	 * expected output is stored in {@code example.ini.eout}.
	 * </p> <p>
	 * The simulator's output will be stored in {@codeexample.ini.out}.
	 * </p>
	 * 
	 * @param path 	- {@code String} with the 
	 * 				directory path
	 * 
	 * @throws IOException 	if failure in reading/writing 
	 * 						the files.
	 */
	static void test(String path) throws IOException {
		// Directorio.
		File dir = new File(path);

		// Directorio ok?
		if ( ! dir.exists() ) {
			throw new FileNotFoundException(path);
		}
		
		// Array de archivos de prueba (filtrado por "acabados en .ini")
		File[] files = dir.listFiles( 
			new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".ini");
				}
			}
		);

		// Prueba de todos los archivos del directorio.
		for (File file : files) {
			test(
				file.getAbsolutePath(), 
				file.getAbsolutePath() + ".out", 
				file.getAbsolutePath() + ".eout",
				_TIMELIMIT_DEFAULT
			);
		}
	}

	/**
	 * Runs the simulator on a file {@code inFile}, writes 
	 * the simulation report in {@code outFile}, and compares 
	 * the result with the expected report stored in the file 
	 * {@code expectedOutFile}.
	 * 
	 * @param inFile 			- {@code String} with the input 
	 * 							file abstract pathname
	 * @param outFile 			- {@code String} with the output 
	 * 							file abstract pathname
	 * @param expectedOutFile 	- {@code String} with the expected 
	 * 							output file abstract pathname
	 * @param timeLimit 		- execution time limit
	 * 
	 * @throws IOException 		if failure in reading/writing 
	 * 							of files
	 */
	private static void test(String inFile, String outFile, 
			String expectedOutFile, int timeLimit) 
			throws IOException {

		_inFile = inFile;
		_outFile = outFile;		
		_timeLimit = timeLimit;

		// Ejecución en batch.
		try {
			startBatchMode();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Comprobación del resultado.
		boolean equalOutput = ( new Ini(_outFile) ).equals( new Ini(expectedOutFile) );
		
		// Muestra por consola.
		System.out.println(
			"Result for: '" + _inFile + "' : " + 
			( equalOutput ? ("OK!") : ("not equal to expected output +'" + expectedOutFile + "'") )
 		);
	}

	// ** EJECUCIÓN EN BATCH ** //
	/**
	 * Run the simulation in {@code batch} mode.
	 * 
	 * @throws IOException 	if failure in reading/writing 
	 * 						of files
	 */
	private static void startBatchMode() throws Exception {		
		// Argumentos
		Ini iniInput = new Ini(_inFile);
		OutputStream os = System.out;
		if(_outFile != null){
			File outFile = new File(_outFile);
			os = new FileOutputStream(outFile);
		}
		
		// Controlador
		Controller control = new Controller(iniInput, os, _timeLimit);

		// Ejecución y captura de excepciones
		try {
			control.executeBatch();
		}
		catch (Exception e) {
			throw e;
		}
	}

	// ** EJECUCIÓN EN GUI ** //
	/**
	 * Run the simulation in {@code GUI} mode.
	 * 
	 * @throws Exception 	if Swing interface fails
	 */
	private static void startGUIMode() throws Exception {
		// Argumentos
		Ini iniInput = (_inFile != null) ? new Ini(_inFile) : null;

		// Interfaz gráfica
		try {
			SwingUtilities.invokeAndWait(
				() -> new SimWindow(
						new Controller(iniInput, null, _timeLimit),
						_inFile)
			);
		} catch (Exception e) {
			throw e;
		}		
	}

	// ** EJECUCIÓN DEL PROGRAMA CON LÍNEA DE COMANDOS ** //
	/**
	 * Runs the simulation in with a {@code CommandLine} as
	 * arguments.
	 * 
	 * @param args 	- simulation arguments
	 */
	private static void start(String[] args) {
		try	{
			parseArgs(args);
			switch (_mode) {
			case "batch" : 
				startBatchMode();
				break;
			case "gui":
				startGUIMode();
				break;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Aborting execution...");
		}
	}
}
