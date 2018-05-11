package es.ucm.fdi.util;

/**
 * Tipo enumerado que representa los distintos datos
 * que se van a mostrar en las tablas del {@code GUI}.
 */
public enum TableDataType {
    E_NUM("#"),       // For event numeration
    E_TIME("Time"),   // For event execution time
    E_TYPE("Type"),   // For event description

    ID("ID"),           // For object id
    REPORT("Report"),   // For object report
    
    J_TYPE("Type"),
    J_GREEN("Green"), // For data on green roads
    J_RED("Red"),     // For data on red roads

    R_TYPE("Type"),
    R_SOURCE("Source"),     // For road source junction
    R_TARGET("Target"),     // For road target junction
    R_LENGHT("Length"),     // For road length
    R_MAX("Max Speed"),     // For road max speed
    R_STATE("Vehicles"),    // For road vehicles

    V_TYPE("Type"),
    V_ROAD("Road"),         // For vehicle road
    V_LOCATION("Location"), // For vehicle location on road
    V_SPEED("Speed"),       // For vehicle speed
    V_KM("Km"),             // For vehicle kilometrage
    V_FAULTY("Faulty time"),// For vehicle faulty time
    V_ROUTE("Itinerary");   // For vehicle itinerary

    private String tableHeader;

    private TableDataType(String head) {
        tableHeader = head;
    }

    @Override
    public String toString() {
        return tableHeader;
    }
}