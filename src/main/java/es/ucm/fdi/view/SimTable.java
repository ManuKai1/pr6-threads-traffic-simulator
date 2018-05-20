package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import es.ucm.fdi.model.simobj.SimObject;
import es.ucm.fdi.util.Describable;
import es.ucm.fdi.util.TableDataType;

/**
 * Clase que representa un panel en Swing con una
 * tabla de la simulación que muestra los datos de
 * de objetos {@link Describable}.
 */
@SuppressWarnings("serial")
public class SimTable extends JPanel {

    /**
     * Tabla en formato Swing.
     */
    private JTable table;

    /**
     * Lista con los nombres de las columnas
     * de la tabla.
     */
    private List<TableDataType> headers;

    /**
     * Lista con los objetos {@code Describable} que
     * se representan en la tabla.
     */
    private List<? extends Describable> tableElements;

    /**
     * Modelo de tabla para la {@code JTable}.
     */
    private ListOfMapsTableModel model;

    /**
     * Modelo de tabla para representar datos de objetos
     * {@code Describable}. Hereda de {@code AbstractTableModel}.
     */
    private class ListOfMapsTableModel extends AbstractTableModel {
        
        /**
         * Mapa utilizado para actualiza la información de 
         * cada objeto utilando el método {code describe()}
         * en la método {@link #getValueAt(int, int)}.
         */
        public Map<TableDataType, Object> elementData = new HashMap<>();

        /**
         * Mapa utilizado para actualizar las JCheckBox de la tabla
         * mediante su actualización en {@link #setValueAt(Object, int, int)}
         * que indican si ha de hacerse un informe de ese objeto o no.
         */
        public Map<Integer, Boolean> reportChecks = new HashMap<>();


        @Override
        public String getColumnName(int columnIndex) {
            return  headers.get(columnIndex).toString();
        }

        @Override
        public int getRowCount() {
            return  tableElements.size();
        }

        @Override
        public int getColumnCount() {
            return headers.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            
            // Caso de la numeración de la tabla de eventos.
            if ( headers.get(columnIndex) == TableDataType.E_NUM ) {
                return  Integer.toString(rowIndex + 1);
            }

            // Caso de los checks para acotar reports.
            if ( headers.get(columnIndex) == TableDataType.REPORT ) {
                Boolean check = reportChecks.get(rowIndex);

                if (check == null) {
                    return true;
                }
                else {
                    return check;
                }
            }
            
            // Caso normal.
            tableElements.get(rowIndex).describe(elementData);

            return  elementData.get(headers.get(columnIndex));
        }

        /**
         * {@inheritDoc}
         * Se implementa de forma que la tabla renderice de
         * forma automática los {@code Boolean}s como 
         * {@code JCheckBox}s.
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (headers.get(columnIndex) == TableDataType.REPORT) {
                return Boolean.class;
            }
            else {
                return String.class;
            }
        }

        /**
         * {@inheritDoc}
         * Se implementa para que las {@code JCheckBox}s puedan
         * ser editables.
         */
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if ( headers.get(columnIndex) == TableDataType.REPORT ) {
                return true;
            }
            else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         * Se implementa de forma que al hacer click en una
         * {@code JCheckBox}, se modifique su estado.
         */
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if ( headers.get(columnIndex) == TableDataType.REPORT ) {
                reportChecks.put(rowIndex, (Boolean) aValue);
            }
        }
    }

    /**
     * Constructor de una {@link #SimTable} que recibe un
     * array de cabeceras {@code head} y una lista de elementos
     * {@code elements} cuya información se va a mostrar.
     */
    public SimTable(TableDataType[] head, List<? extends Describable> elements) {
        super( new BorderLayout() );
        
        headers = new ArrayList<>(Arrays.asList(head));
        tableElements = elements;
        model = new ListOfMapsTableModel();

        // Se crea la tabla basada en el modelo.
        table = new JTable(model);
        table.setRowSelectionAllowed(false);

        // Se añade la tabla al panel
        this.add( new JScrollPane(table,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        );   
    }

    /** Método interno que llama indica al modelo que
     * los datos de la tabla han cambiado, para que se
     * actualicen en el {@code GUI}.
     */
    private void update() {
        model.fireTableDataChanged();
    }

    /**
     * Método que establece una nueva lista de elementos
     * cuya información debe representarse en la tabla.
     * 
     * @param newList   - nueva lista de elementos
     */
    public void setList(List<? extends Describable> newList){
        tableElements = newList;
        update();
    }


    /**
     * Método que hace un clear de la tabla, haciendo
     * un clear de {@code tableElements} y llamando a
     * {@link #update()}.
     */
    public void clear(){
    	tableElements.clear();
    	update();
    }

    /**
     * Devuelve una lista con los elementos que describe
     * la tabla.
     * 
     * @return  lista de elementos de la tabla
     */
    public List<? extends Describable> getTableElements() {
        return tableElements;
    }

    /**
     * Método que devuelve una lista con los elementos
     * de la tabla cuyas {@code JCheckBox}s están
     * seleccionadas.
     * 
     * @return  {@code List<SimObject} con los
     *          objetos seleccionados
     */
    public List<SimObject> getSelected() {
        List<SimObject> selected = new ArrayList<>();
        
        int columnIndex = headers.indexOf(TableDataType.REPORT);

        for (int row = 0; row < tableElements.size(); ++row) {
            if ( (Boolean) model.getValueAt(row, columnIndex) ) {
                SimObject d = (SimObject) tableElements.get(row);
                selected.add(d);
            }
        }
        
        return selected;
    }
}
