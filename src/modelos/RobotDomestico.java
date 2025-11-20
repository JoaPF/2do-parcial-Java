package modelos;

public class RobotDomestico extends Robot {
    private int cantidadTareas;

    public RobotDomestico(String nombre, int energia, String serie, int cantidadTareas) throws ExcepcionRobot {
        super(nombre, energia, serie);
        this.cantidadTareas = cantidadTareas;
    }

    public int getCantidadTareas() { return cantidadTareas; }
    public void setCantidadTareas(int cantidadTareas) { this.cantidadTareas = cantidadTareas; }

    @Override
    public String obtenerTipo() { return "Doméstico"; }

    @Override
    public String obtenerDetalleEspecifico() { return cantidadTareas + " tareas"; }

    @Override
    public String aJson() {
        return String.format("{\"tipo\":\"DOM\",\"nombre\":\"%s\",\"energia\":%d,\"serie\":\"%s\",\"extra\":%d}", 
               getNombre(), getNivelEnergia(), getNumeroSerie(), cantidadTareas);
    }
}