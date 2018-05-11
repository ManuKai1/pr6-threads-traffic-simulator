package es.ucm.fdi.util;

import java.util.Map;

/**
 * Interfaz que hace a un objecto 'describable' en el
 * sentido de que al ejecutar su método {@link #describe()}
 * el objeto se describe en el mapa.
 */
public interface Describable {

    /**
     * Método de actualización de mapa de descripciones.
     * 
     * @param out - mapa a actualizar con los 
     *              pares clave-valor
     */
    public void describe(Map<TableDataType, Object> out);
}