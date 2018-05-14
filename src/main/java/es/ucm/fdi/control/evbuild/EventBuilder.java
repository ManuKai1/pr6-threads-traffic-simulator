package es.ucm.fdi.control.evbuild;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;

/**
 * Clase utilizada como base para la construcción de
 * {@link Event Events} del simulador.
 */
public abstract class EventBuilder {

	// ** ATRIBUTOS ** //
	/**
	 * Nombre de la {@code IniSection}.
	 */
	protected String iniName;
	
	// ** CONSTRUCTOR ** //
	/**
	 * Constructor de {@link EventBuilder}.
	 * 
	 * @param name 	- nombre de la 
	 * 				{@code IniSection}
	 */
	public EventBuilder(String name) {
		iniName = name;
	}
	
	// ** MÉTODO ABSTRACTO ** //
	abstract Event parse(IniSection ini);

	// ** MÉTODOS DE COMPROBACIÓN ** //
	// Utilizados para comprobar los datos de los
	// archivos .ini.

	/**
	 * <p>
	 * Método que comprueba si se ha introducido un 
	 * valor para la clave {@code time} de una 
	 * {@code IniSection}.
	 * </p> <p>
	 * Válido para cualquier {@code Event}
	 * representado por la {@code IniSection}
	 * </p>
	 * 
	 * @param ini	- sección de un archivo {@code .ini}
	 * 				que contiene la información de un
	 * 				{@code Event}
	 * 
	 * @return 		if key {@code time} has a value
	 */
	protected boolean existsTimeKey(IniSection ini) {
		String timeKey = ini.getValue("time");

		return (timeKey != null);
	}
	
	/**
	 * <p>
	 * Método que comprueba si se ha introducido un 
	 * valor para la clave {@code seed} de una
	 * {@code IniSection}.
	 * </p> <p>
	 * Válido para el <code>Builder</code> del evento
	 * {@code NewCarVehicle}: {@link NewCarVehicleBuilder}.
	 * </p>
	 * 
	 * @param ini	- sección de un archivo {@code .ini}
	 * 				que contiene la información de un
	 * 				{@code Event}
	 * 
	 * @return 		if key {@code seed} has a value
	 */
	protected boolean existsSeedKey(IniSection ini) {
		String seedKey = ini.getValue("seed");

		return (seedKey != null);
	}
	
	/**
	 * <p>
	 * Método que, dado un {@code String} que representa
	 * el tipo de un {@code SimObj}, comprueba si se
	 * corresponde con el tipo introducido para la clave
	 * {@code type} de la {@code IniSection}
	 * proporcionada.
	 * </p> <p>
	 * En el caso de los objetos más básicos de la simulación,
	 * {@code Junction}, {@code Road} y {@code Vehicle}, 
	 * no se indica su tipo, por lo que el valor de 
	 * {@code seed} es {@code null}.
	 * </p>
	 * 
	 * @param ini	- {@code IniSection} de un archivo
	 *  			{@code .ini} que contiene la
	 * 				información de un {@code Event}
	 * @param type 	- {@code String} con el argumento
	 * 				a comparar
	 * 
	 * @return 		if the value of key {@code seed}
	 * 				matches the given {@code type}
	 */
	protected boolean typeMatch(IniSection ini, String type) {
		if (type == null) {
			return ( ini.getValue("type") == null );
		}

		return	ini.getValue("type").equals(type);
	}
	
	/**
	 * Método que, dado una {@code IniSection},
	 * comprueba si su etiqueta es igual al atributo
	 * {@code iniName} de {@code EventBuilder}.
	 * 
	 * @param ini	- {@code IniSection} de un archivo
	 * 				{@code .ini} cuya etiqueta queremos
	 * 				comparar
	 * 
	 * @return 		if the {@code ini} tag equals attribute
	 * 				{@code iniName}
	 */
	protected boolean iniNameMatch(IniSection ini) {
		
		return	ini.getTag().equals(iniName);
	}
	
	/**
	 * <p>
	 * Método que, dado una {@code IniSection} y la
	 * clave de uno de sus argumentos (clave que debería
	 * almacenar el ID de un {@code SimObj}), comprueba
	 * que el ID es válido y lo devuelve si así es.
	 * </p> <p>
	 * Si el ID no es válido, lanza una excepción.
	 * </p>
	 * 
	 * @param ini	- {@code IniSection} de un archivo
	 * 				{@code .ini} del cual queremos 
	 * 				parsear cierto ID de entre sus valores
	 * @param key	- {@code String} con la clave del
	 * 				valor (ID) de {@code ini} que 
	 * 				queremos comprobar
	 * 
	 * @return 		{@code String} with the key's value 
	 * 				if it is a valid ID
	 * 
	 * @throws IllegalArgumentException 	if the key's value is
	 * 										not a valid ID
	 */
	protected String parseID(IniSection ini, String key) 
			throws IllegalArgumentException {
		
		String id = ini.getValue(key);
		if (id != null) {
			if ( ! validID(id) ) {
				throw new IllegalArgumentException(
					"Illegal ID: " + id
				);
			}

			return id;
		}
		else {
			throw new IllegalArgumentException(
				"No ID found"
			);
		}
	}
	
	/**
	 * <p>
	 * Método que, dado una {@code IniSection} y la clave
	 * de uno de sus argumentos (clave que debería almacenar un
	 * número entero), comprueba si el valor-entero de esa clave
	 * es no negativo y lo devuelve si así es.
	 * </p> <p>
	 * Si el entero no es válido (negativo o no es un entero) se
	 * lanza una excepción.
	 * </p>
	 * 
	 * @param ini	- {@code IniSection} de un archivo
	 * 				{@code .ini} del cual queremos 
	 * 				parsear cierto entero de entre sus valores
	 * @param key	- {@code String} con la clave del
	 * 				valor (entero) de {@code ini} 
	 * 				que queremos comprobar
	 * 
	 * @return		{@code int} with the key's value
	 * 				if it is a no-negative integer
	 * 
	 * @throws IllegalArgumentException 	if the key's value is
	 * 										not a no-negative int
	 */
	protected int parseNoNegativeInt(IniSection ini, String key)
			throws IllegalArgumentException {
		
		int result = 0;

		// Parse del valor de la key, comprobando
		// que sea un entero
		try {
			result = Integer.parseInt(ini.getValue(key));
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"Int reading failure"
			);
		}
		
		// Comprobamos que el numero sea no negativo
		if (result < 0) {
			throw new IllegalArgumentException(
				"Negative int failure"
			);
		}

		return result;
	}
	
	/**
	 * <p>
	 * Método que, dado una {@code IniSection} y la clave
	 * de uno de sus argumentos (clave que debería almacenar un
	 * número entero), comprueba si el valor-entero de esa clave
	 * es positivo distinto de cero y lo devuelve si así es.
	 * </p> <p>
	 * Si el entero no es válido (no positivo o no es un entero) 
	 * se lanza una excepción.
	 * </p>
	 * 
	 * @param ini	{@code IniSection} de un archivo
	 * 				{@code .ini} del cual queremos 
	 * 				parsear cierto entero de entre sus valores
	 * @param key	{@code String} con la clave del
	 * 				valor (entero) de {@code ini} 
	 * 				que queremos comprobar
	 * 
	 * @return		{@code int} with the key's value
	 * 				if it is a positive integer
	 * 
	 * @throws IllegalArgumentException 	if the key's value is
	 * 										not a positive int
	 */
	protected int parsePositiveInt(IniSection ini, String key)
			throws IllegalArgumentException {
		
		int result = 0;

		// Parse del valor de la key, comprobando
		// que sea un entero
		try {
			result = Integer.parseInt(ini.getValue(key));
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"Int reading failure"
			);
		}
		
		// Comprobamos que el valor sea positivo
		if (result <= 0) {
			throw new IllegalArgumentException(
				"Non-positive int failure"
			);
		}

		return result;
	}
	
	/**
	 * <p>
	 * Método que, dado una {@code IniSection} y la clave
	 * de uno de sus argumentos (clave que debería almacenar un
	 * tipo {@code Long}), comprueba si el valor de esa 
	 * clave es un tipo {@code Long} y lo devuelve si así es.
	 * </p> <p>
	 * Si el valor no es válido (no es {@code Long}) se lanza una excepción.
	 * </p>
	 * 
	 * @param ini	{@code IniSection} de un archivo
	 * 				{@code .ini} del cual queremos 
	 * 				parsear cierto número de entre sus valores
	 * @param key	{@code String} con la clave del
	 * 				valor (long) de {@code ini} 
	 * 				que queremos comprobar
	 * 
	 * @return		<code>Long</code> with the key's value
	 * 				if it is a {@code Long}
	 * 
	 * @throws IllegalArgumentException 	if the key's value is
	 * 										not a long number
	 */
	protected long parseLong(IniSection ini, String key)
			throws IllegalArgumentException {
		
		long result = 0;

		// Parse del valor de la key, comprobando
		// que sea un tipo long
		try {
			result = Long.parseLong(ini.getValue(key));
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"Long reading failure"
			);
		}

		return result;
	}
	
	/**
	 * <p>
	 * Método que, dado una {@code IniSection} y la clave
	 * de uno de sus argumentos (clave que debería almacenar una
	 * probabilidad), comprueba si el valor dado es válido para
	 * una probabilidad (entre 0 y 1) y lo devuelve si así es. 
	 * </p> <p>
	 * Si el valor no es válido (no es probabilidad o no es Double)
	 * se lanza una excepción.
	 * </p>
	 * 
	 * @param ini	{@code IniSection} de un archivo
	 * 				{@code .ini} del cual queremos 
	 * 				parsear una probabilidad
	 * @param key	{@code String} con la clave del
	 * 				valor de {@code ini} que queremos 
	 * 				comprobar
	 * 
	 * @return		{@code Double} with the key's value
	 * 				if it is a valid probability
	 * 
	 * @throws IllegalArgumentException 	if the key's value is
	 * 										not a valid probability
	 */
	protected double parseProbability(IniSection ini, String key)
			throws IllegalArgumentException {
		
		double result;
		String toResult = ini.getValue(key);
		if (toResult != null) {
			try {
				result = Double.parseDouble(toResult);
			}
			catch (NumberFormatException e) {
				// El valor no era un real
				throw new IllegalArgumentException(
					"Double reading failure"
				);
			}

			if (result < 0 || result > 1) {
				// La probabilidad se va de los límites
				throw new IllegalArgumentException(
					"Out of bounds probability"
				);
			}

			return result;
		}
		else {
			throw new IllegalArgumentException(
				"Value not found"
			);
		} 
	}
	
	/**
	 * <p>
	 * Método que, dado una {@code IniSection} y la clave
	 * de uno de sus argumentos (clave que debería almacenar una
	 * lista de IDs), comprueba si la lista de IDs es válida (al
	 * menos un elemento y sus elementos son IDs válidos) y la 
	 * devuelve si así es. 
	 * </p> <p>
	 * Si la lista no es válida (algún elemento de la lista no es
	 * un ID válido) se lanza una excepción.
	 * </p>
	 * 
	 * @param ini	{@code IniSection} de un archivo
	 * 				{@code .ini} del cual queremos 
	 * 				parsear una lista de IDs
	 * @param key	{@code String} con la clave del
	 * 				valor de {@code ini} que queremos 
	 * 				comprobar
	 * 
	 * @return		<code>List<String></code> with the key's 
	 * 				value if it is a valid ID list
	 * 
	 * @throws IllegalArgumentException 	if the key's value is
	 * 										not a valid ID list
	 */
	protected List<String> parseIDList(IniSection ini, String key, 
			int minElems) throws IllegalArgumentException {
		
		List<String> result = new ArrayList<>();
		
		// Array de Strings con las IDs.
		String line = ini.getValue(key);
		if (line != null) {
			String[] input = line.split(",");

			// Comprobación de IDs.
			for (String idS : input) {
				if ( ! validID(idS) ) {
					throw new IllegalArgumentException(
						"Illegal ID: " + idS
					);
				}

				result.add(idS);
			}

			// Al menos un elemento.
			if (result.size() < minElems) {
				throw new IllegalArgumentException(
					"Not enough elements"
				);
			}

			return result;
		}
		else {
			throw new IllegalArgumentException(
				"List of elements not found"
			);
		}		 
	}
	
	/**
	 * Comprueba si un {@code ID} dado es válido
	 * para el simulador.
	 * 
	 * @return 	si el {@code ID} es válido
	 */
	static boolean validID(String id) {
		return 	Pattern.matches("\\w+", id);
	}
}
