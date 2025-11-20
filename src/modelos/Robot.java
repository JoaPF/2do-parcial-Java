package modelos;

public abstract class Robot {
    private String nombre;
    private int nivelEnergia;
    private String numeroSerie;

    public Robot(String nombre, int nivelEnergia, String numeroSerie) throws ExcepcionRobot {
        if (nivelEnergia < 0 || nivelEnergia > 100) {
            throw new ExcepcionRobot("El nivel de energía debe estar entre 0 y 100.");
        }
        // Validación: solo números positivos
        if (!numeroSerie.matches("\\d+") || Integer.parseInt(numeroSerie) < 0) {
             throw new ExcepcionRobot("El número de serie debe ser un valor positivo.");
        }
        
        this.nombre = nombre;
        this.nivelEnergia = nivelEnergia;
        this.numeroSerie = numeroSerie;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getNivelEnergia() { return nivelEnergia; }
    public void setNivelEnergia(int nivelEnergia) throws ExcepcionRobot {
        if (nivelEnergia < 0 || nivelEnergia > 100) 
            throw new ExcepcionRobot("Energía fuera de rango (0-100).");
        this.nivelEnergia = nivelEnergia;
    }

    public String getNumeroSerie() { return numeroSerie; }
    
    // Métodos abstractos
    public abstract String obtenerTipo();
    public abstract String obtenerDetalleEspecifico();
    
    // Método para serializar a JSON manualmente
    public abstract String aJson();
}