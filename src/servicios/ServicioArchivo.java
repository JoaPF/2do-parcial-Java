package servicios;

import modelos.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ServicioArchivo {
    private static final String ARCHIVO_DB = "robots_db.json";

    public void guardarRobotsJson(List<Robot> listaRobots) throws IOException {
        String contenidoJson = "[" + listaRobots.stream()
                .map(Robot::aJson)
                .collect(Collectors.joining(",")) + "]";
        Files.writeString(Path.of(ARCHIVO_DB), contenidoJson);
    }

    public void exportarCsv(List<Robot> listaRobots, File archivoDestino) throws IOException {
        StringBuilder sb = new StringBuilder("Serie,Nombre,Energia,Tipo,Detalle\n");
        for (Robot robot : listaRobots) {
            sb.append(String.join(",", 
                robot.getNumeroSerie(), 
                robot.getNombre(), 
                String.valueOf(robot.getNivelEnergia()),
                robot.obtenerTipo(),
                robot.obtenerDetalleEspecifico()
            )).append("\n");
        }
        Files.writeString(archivoDestino.toPath(), sb.toString());
    }

    public List<Robot> cargarRobotsJson() {
        List<Robot> listaRetorno = new ArrayList<>();
        Path rutaArchivo = Path.of(ARCHIVO_DB);
        
        if (!Files.exists(rutaArchivo)) return listaRetorno;

        try {
            String contenido = Files.readString(rutaArchivo);
            // Limpieza básica de corchetes
            contenido = contenido.replace("[", "").replace("]", "");
            if (contenido.isBlank()) return listaRetorno;

            // Separar objetos
            String[] objetosRaw = contenido.split("},"); 
            
            for (String objTexto : objetosRaw) {
                objTexto = objTexto.replace("{", "").replace("}", "").replace("\"", "");
                Map<String, String> mapaDatos = new HashMap<>();
                
                for (String par : objTexto.split(",")) {
                    String[] claveValor = par.split(":");
                    if(claveValor.length == 2) {
                        mapaDatos.put(claveValor[0].trim(), claveValor[1].trim());
                    }
                }
                
                // Reconstrucción de objetos usando los datos del mapa
                String tipo = mapaDatos.get("tipo");
                String nombre = mapaDatos.get("nombre");
                int energia = Integer.parseInt(mapaDatos.get("energia"));
                String serie = mapaDatos.get("serie");
                String extra = mapaDatos.get("extra");

                if ("DOM".equals(tipo)) {
                    listaRetorno.add(new RobotDomestico(nombre, energia, serie, Integer.parseInt(extra)));
                } else if ("IND".equals(tipo)) {
                    listaRetorno.add(new RobotIndustrial(nombre, energia, serie, Double.parseDouble(extra)));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer la base de datos: " + e.getMessage());
        }
        return listaRetorno;
    }
}