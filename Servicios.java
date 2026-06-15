import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Servicios {

    // Servicio 1: búsqueda O(1) por código
    private HashMap<String, Paquete> paquetesPorCodigo;

    // Servicio 2: listas precargadas para O(1) al retornar
    private List<Paquete> paquetesConAlimentos;
    private List<Paquete> paquetesSinAlimentos;

    // Servicio 3: permite buscar por rango de urgencia eficientemente
    private TreeMap<Integer, List<Paquete>> paquetesPorUrgencia;

    // También guardamos los camiones para la segunda parte
    private List<Camion> camiones;

    /*
     * Complejidad temporal del constructor: O(C + P)
     * donde C = cantidad de camiones y P = cantidad de paquetes.
     * Se recorre cada archivo una vez y cada inserción en HashMap y TreeMap es O(log P) en el peor caso,
     * pero como se insertan P paquetes el total es O(P log P) para el TreeMap.
     * Para HashMap y las listas es O(P). Total: O(C + P log P).
     */
    public Servicios(String pathCamiones, String pathPaquetes) {
        paquetesPorCodigo = new HashMap<>();
        paquetesConAlimentos = new ArrayList<>();
        paquetesSinAlimentos = new ArrayList<>();
        paquetesPorUrgencia = new TreeMap<>();
        camiones = new ArrayList<>();

        cargarCamiones(pathCamiones);
        cargarPaquetes(pathPaquetes);
    }

    private void cargarCamiones(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int total = Integer.parseInt(br.readLine().trim());
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                int id = Integer.parseInt(partes[0].trim());
                String patente = partes[1].trim();
                boolean refrigerado = partes[2].trim().equals("1");
                int capacidad = Integer.parseInt(partes[3].trim());
                camiones.add(new Camion(id, patente, refrigerado, capacidad));
            }
        } catch (IOException e) {
            System.out.println("Error al leer camiones: " + e.getMessage());
        }
    }

    private void cargarPaquetes(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int total = Integer.parseInt(br.readLine().trim());
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                int id = Integer.parseInt(partes[0].trim());
                String codigo = partes[1].trim();
                int peso = Integer.parseInt(partes[2].trim());
                boolean alimentos = partes[3].trim().equals("1");
                int urgencia = Integer.parseInt(partes[4].trim());

                Paquete p = new Paquete(id, codigo, peso, alimentos, urgencia);

                // Para servicio 1
                paquetesPorCodigo.put(codigo, p);

                // Para servicio 2
                if (alimentos) {
                    paquetesConAlimentos.add(p);
                } else {
                    paquetesSinAlimentos.add(p);
                }

                // Para servicio 3
                paquetesPorUrgencia.computeIfAbsent(urgencia, k -> new ArrayList<>()).add(p);
            }
        } catch (IOException e) {
            System.out.println("Error al leer paquetes: " + e.getMessage());
        }
    }

    /*
     * Complejidad temporal: O(1)
     * Búsqueda directa en HashMap por clave (código del paquete).
     */
    public Paquete servicio1(String codigoPaquete) {
        return paquetesPorCodigo.getOrDefault(codigoPaquete, null);
    }

    /*
     * Complejidad temporal: O(1)
     * Las listas ya están armadas al momento de la carga. Solo se retorna la referencia.
     */
    public List<Paquete> servicio2(boolean contieneAlimentos) {
        return contieneAlimentos ? paquetesConAlimentos : paquetesSinAlimentos;
    }

    /*
     * Complejidad temporal: O(log P + k)
     * donde k es la cantidad de paquetes en el rango y P la cantidad total.
     * subMap() en TreeMap es O(log P), luego se recorren solo los resultados del rango.
     */
    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima) {
        List<Paquete> resultado = new ArrayList<>();
        // subMap con true,true incluye ambos extremos
        NavigableMap<Integer, List<Paquete>> rango = paquetesPorUrgencia.subMap(
            urgenciaMinima, true, urgenciaMaxima, true
        );
        for (List<Paquete> lista : rango.values()) {
            resultado.addAll(lista);
        }
        return resultado;
    }

    public List<Camion> getCamiones() { return camiones; }
    public Collection<Paquete> getTodosLosPaquetes() { return paquetesPorCodigo.values(); }
}