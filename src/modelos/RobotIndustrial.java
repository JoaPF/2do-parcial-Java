package modelos;

public class RobotIndustrial extends Robot {
    private double capacidadCarga;

    public RobotIndustrial(String nombre, int energia, String serie, double capacidadCarga) throws ExcepcionRobot {
        super(nombre, energia, serie);
        this.capacidadCarga = capacidadCarga;
    }

    public double getCapacidadCarga() { return capacidadCarga; }
    public void setCapacidadCarga(double capacidadCarga) { this.capacidadCarga = capacidadCarga; }

    @Override
    public String obtenerTipo() { return "Industrial"; }

    @Override
    public String obtenerDetalleEspecifico() { return capacidadCarga + " Kg"; }

    @Override
    public String aJson() {
        return String.format("{\"tipo\":\"IND\",\"nombre\":\"%s\",\"energia\":%d,\"serie\":\"%s\",\"extra\":%s}", 
               getNombre(), getNivelEnergia(), getNumeroSerie(), String.valueOf(capacidadCarga));
    }
}