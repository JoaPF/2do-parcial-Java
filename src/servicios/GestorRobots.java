package servicios;

import modelos.*;
import java.util.List;

public class GestorRobots {
    private List<Robot> inventario;
    private ServicioArchivo servicioArchivo;

    public GestorRobots() {
        this.servicioArchivo = new ServicioArchivo();
        this.inventario = servicioArchivo.cargarRobotsJson();
    }

    public List<Robot> obtenerTodos() { return inventario; }

    public void agregarRobot(Robot nuevoRobot) throws ExcepcionRobot {

        boolean existe = inventario.stream()
                .anyMatch(r -> r.getNumeroSerie().equals(nuevoRobot.getNumeroSerie()));
        
        if (existe) {
            throw new ExcepcionRobot("El número de serie " + nuevoRobot.getNumeroSerie() + " ya existe.");
        }
        inventario.add(nuevoRobot);
        guardarCambios();
    }

    public void eliminarRobot(Robot robotAEliminar) {
        inventario.remove(robotAEliminar);
        guardarCambios();
    }

    public void actualizarRobot(Robot robotOriginal, Robot robotModificado) throws ExcepcionRobot {
        if (!robotOriginal.getNumeroSerie().equals(robotModificado.getNumeroSerie())) {
             boolean existe = inventario.stream()
                .anyMatch(r -> r != robotOriginal && r.getNumeroSerie().equals(robotModificado.getNumeroSerie()));
             if (existe) throw new ExcepcionRobot("El nuevo número de serie ya está en uso.");
        }
        
        int indice = inventario.indexOf(robotOriginal);
        if (indice >= 0) {
            inventario.set(indice, robotModificado);
            guardarCambios();
        }
    }
    
    public List<Robot> obtenerRobotsBajaEnergia() {
        return inventario.stream()
                .filter(r -> r.getNivelEnergia() < 20)
                .toList(); 
    }

    private void guardarCambios() {
        try {
            servicioArchivo.guardarRobotsJson(inventario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ServicioArchivo obtenerServicioArchivo() { return servicioArchivo; }

}
