import java.util.*;

public class Solucion {
    // Mapa de camion -> paquetes asignados
    private Map<Camion, List<Paquete>> asignaciones;
    private List<Paquete> noAsignados;

    public Solucion(List<Camion> camiones) {
        asignaciones = new LinkedHashMap<>();
        for (Camion c : camiones) {
            asignaciones.put(c, new ArrayList<>());
        }
        noAsignados = new ArrayList<>();
    }

    // Constructor de copia — usado por backtracking para guardar el mejor estado
    public Solucion(Solucion otra) {
        asignaciones = new LinkedHashMap<>();
        for (Map.Entry<Camion, List<Paquete>> entry : otra.asignaciones.entrySet()) {
            asignaciones.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        noAsignados = new ArrayList<>(otra.noAsignados);
    }

    public int getPesoNoAsignado() {
        return noAsignados.stream().mapToInt(Paquete::getPesoKg).sum();
    }

    public void imprimir() {
        for (Map.Entry<Camion, List<Paquete>> entry : asignaciones.entrySet()) {
            Camion c = entry.getKey();
            System.out.println("Camion " + c.getId() + " (" + c.getPatente() + "): " + entry.getValue());
        }
        System.out.println("Paquetes no asignados: " + noAsignados);
        System.out.println("Peso no asignado: " + getPesoNoAsignado() + " kg.");
    }

    public List<Paquete> getNoAsignados() { return noAsignados; }
    public Map<Camion, List<Paquete>> getAsignaciones() { return asignaciones; }
}
