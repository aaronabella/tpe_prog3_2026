import java.util.*;

public class Asignador {

    private List<Camion> camiones;
    private List<Paquete> paquetes;
    private int estadosBack;
    private int candidatosGreedy;

    public Asignador(List<Camion> camiones, Collection<Paquete> paquetes) {
        this.camiones = camiones;
        this.paquetes = new ArrayList<>(paquetes);
    }

    public void resolver() {
        System.out.println("=== Estrategia Backtracking ===");
        estadosBack = 0;
        Solucion mejorBT = backtracking(0, new ArrayList<>());
        mejorBT.imprimir();
        System.out.println("Cantidad de estados generados: " + estadosBack);

        // Resetear camiones para el greedy
        resetearCamiones();

        System.out.println("\n=== Estrategia Greedy ===");
        candidatosGreedy = 0;
        Solucion solGreedy = greedy();
        solGreedy.imprimir();
        System.out.println("Candidatos considerados: " + candidatosGreedy);
    }

    /*
     * Estrategia Backtracking:
     * Se recorren los paquetes uno por uno (índice). Para cada paquete se prueban
     * todas las opciones: asignarlo a cada camión que lo acepte, o dejarlo sin asignar.
     * El estado de asignación se mantiene directamente en los objetos Camion
     * (asignar/desasignar), y al llegar a la hoja se toma una foto del estado actual
     * como Solucion. Se conserva siempre la solución con menor peso no asignado.
     * Poda: si ya encontramos una solución con peso 0, no puede existir mejor resultado.
     */
    private Solucion backtracking(int indicePaquete, List<Paquete> noAsignadosActual) {
        estadosBack++;

        if (indicePaquete == paquetes.size()) {
            // Hoja del árbol: construir snapshot del estado actual
            Solucion sol = new Solucion(camiones);
            for (Camion c : camiones) {
                sol.getAsignaciones().put(c, new ArrayList<>(c.getPaquetesAsignados()));
            }
            sol.getNoAsignados().addAll(noAsignadosActual);
            return sol;
        }

        Paquete actual = paquetes.get(indicePaquete);
        Solucion mejorLocal = null;

        // Opción 1: intentar asignar a cada camión válido
        for (Camion c : camiones) {
            if (c.puedeAsignar(actual)) {
                c.asignar(actual);
                Solucion resultado = backtracking(indicePaquete + 1, noAsignadosActual);
                c.desasignar(actual);

                if (mejorLocal == null || resultado.getPesoNoAsignado() < mejorLocal.getPesoNoAsignado()) {
                    mejorLocal = resultado;
                }

                // Poda: peso 0 es el óptimo, no tiene sentido seguir explorando
                if (mejorLocal.getPesoNoAsignado() == 0) return mejorLocal;
            }
        }

        // Opción 2: dejar el paquete sin asignar
        noAsignadosActual.add(actual);
        Solucion sinAsignar = backtracking(indicePaquete + 1, noAsignadosActual);
        noAsignadosActual.remove(noAsignadosActual.size() - 1); // backtrack

        if (mejorLocal == null || sinAsignar.getPesoNoAsignado() < mejorLocal.getPesoNoAsignado()) {
            mejorLocal = sinAsignar;
        }

        return mejorLocal;
    }

    /*
     * Estrategia Greedy:
     * Función de selección: ordenar paquetes de mayor a menor urgencia.
     * La lógica es priorizar los paquetes más urgentes para asegurar que sean asignados primero.
     * Para cada paquete, se busca el camión con menos espacio disponible que aún lo acepte
     * (first-fit decreasing sobre capacidad), minimizando el desperdicio de espacio.
     * Si ningún camión puede tomarlo, queda sin asignar.
     */
    private Solucion greedy() {
        // Ordenar por urgencia descendente
        List<Paquete> ordenados = new ArrayList<>(paquetes);
        ordenados.sort((a, b) -> b.getNivelUrgencia() - a.getNivelUrgencia());

        Solucion sol = new Solucion(camiones);

        for (Paquete p : ordenados) {
            Camion mejor = null;
            int menorEspacioRestante = Integer.MAX_VALUE;

            for (Camion c : camiones) {
                candidatosGreedy++;
                if (c.puedeAsignar(p)) {
                    int espacioRestante = c.getCapacidadKg() - c.getPesoActual() - p.getPesoKg();
                    if (espacioRestante < menorEspacioRestante) {
                        menorEspacioRestante = espacioRestante;
                        mejor = c;
                    }
                }
            }

            if (mejor != null) {
                mejor.asignar(p);
                sol.getAsignaciones().get(mejor).add(p);
            } else {
                sol.getNoAsignados().add(p);
            }
        }

        return sol;
    }

    private void resetearCamiones() {
        for (Camion c : camiones) {
            c.resetear();
        }
    }
}