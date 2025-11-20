package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelos.*;
import servicios.GestorRobots;
import java.io.File;

public class App extends Application {

    private GestorRobots gestor;
    private TableView<Robot> tablaRobots;
    private ObservableList<Robot> listaObservable;

    // Controles del formulario
    private TextField campoNombre, campoEnergia, campoSerie, campoExtra;
    private ComboBox<String> comboTipo;
    private Label etiquetaExtra; 

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenarioPrincipal) {
        gestor = new GestorRobots();
        listaObservable = FXCollections.observableArrayList(gestor.obtenerTodos());

        BorderPane panelPrincipal = new BorderPane();
        panelPrincipal.setPadding(new Insets(15));

        // --- TOP: Título ---
        Label titulo = new Label("Sistema de Gestión de Robots - Lab. Ramírez");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        panelPrincipal.setTop(titulo);

        // --- CENTER: Tabla ---
        tablaRobots = crearTabla();
        tablaRobots.setItems(listaObservable);
        panelPrincipal.setCenter(tablaRobots);

        // --- RIGHT: Formulario ---
        VBox panelFormulario = crearFormulario();
        panelPrincipal.setRight(panelFormulario);

        // --- BOTTOM: Botonera Extra ---
        HBox barraInferior = new HBox(10);
        barraInferior.setPadding(new Insets(10, 0, 0, 0));
        Button botonBajaEnergia = new Button("⚠️ Ver Robots Baja Energía");
        botonBajaEnergia.setStyle("-fx-base: #ffcccc;"); 
        botonBajaEnergia.setOnAction(e -> abrirVentanaBajaEnergia());
        barraInferior.getChildren().add(botonBajaEnergia);
        panelPrincipal.setBottom(barraInferior);

        Scene escena = new Scene(panelPrincipal, 950, 600);
        escenarioPrincipal.setTitle("Inventario Robots v1.0");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
        
        // Listener para selección en tabla
        tablaRobots.getSelectionModel().selectedItemProperty().addListener((obs, seleccionAnterior, seleccionNueva) -> {
            if (seleccionNueva != null) cargarRobotEnFormulario(seleccionNueva);
        });
    }

    private TableView<Robot> crearTabla() {
        TableView<Robot> tabla = new TableView<>();
        
        TableColumn<Robot, String> colSerie = new TableColumn<>("Serie");
        colSerie.setCellValueFactory(new PropertyValueFactory<>("numeroSerie"));

        TableColumn<Robot, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Robot, Integer> colEnergia = new TableColumn<>("Energía %");
        colEnergia.setCellValueFactory(new PropertyValueFactory<>("nivelEnergia"));

        TableColumn<Robot, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo")); // Llama a obtenerTipo() si usas JavaFX beans o PropertyValueFactory busca "getTipo"
        
        TableColumn<Robot, String> colDetalle = new TableColumn<>("Detalle");
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("detalleEspecifico"));

        tabla.getColumns().addAll(colSerie, colNombre, colEnergia, colTipo, colDetalle);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tabla;
    }

    private VBox crearFormulario() {
        VBox cajaVertical = new VBox(10);
        cajaVertical.setPadding(new Insets(0, 0, 0, 15));
        cajaVertical.setPrefWidth(280);
        cajaVertical.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 0 1; -fx-padding: 10;");

        Label lblTituloForm = new Label("Administrar Robot");
        lblTituloForm.setStyle("-fx-font-weight: bold;");

        comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("Doméstico", "Industrial");
        comboTipo.setValue("Doméstico");
        
        campoSerie = new TextField(); campoSerie.setPromptText("Nro. Serie (Único)");
        campoNombre = new TextField(); campoNombre.setPromptText("Nombre del Robot");
        campoEnergia = new TextField(); campoEnergia.setPromptText("Energía (0-100)");
        
        etiquetaExtra = new Label("Cant. Tareas:");
        campoExtra = new TextField(); campoExtra.setPromptText("Cantidad de tareas");

        // Cambio dinámico de etiqueta
        comboTipo.setOnAction(e -> {
            if (comboTipo.getValue().equals("Doméstico")) {
                etiquetaExtra.setText("Cant. Tareas:");
                campoExtra.setPromptText("Cantidad de tareas");
            } else {
                etiquetaExtra.setText("Carga Máx (Kg):");
                campoExtra.setPromptText("Carga en Kilogramos");
            }
        });

        Button btnAgregar = new Button("Agregar");
        btnAgregar.setMaxWidth(Double.MAX_VALUE);
        btnAgregar.setOnAction(e -> accionAgregar());

        Button btnModificar = new Button("Modificar Seleccionado");
        btnModificar.setMaxWidth(Double.MAX_VALUE);
        btnModificar.setOnAction(e -> accionModificar());

        Button btnEliminar = new Button("Eliminar Seleccionado");
        btnEliminar.setMaxWidth(Double.MAX_VALUE);
        btnEliminar.setStyle("-fx-base: #ff6666;");
        btnEliminar.setOnAction(e -> accionEliminar());

        Button btnLimpiar = new Button("Limpiar Formulario");
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        cajaVertical.getChildren().addAll(
            lblTituloForm, 
            new Label("Tipo:"), comboTipo, 
            new Label("Serie:"), campoSerie, 
            new Label("Nombre:"), campoNombre, 
            new Label("Energía:"), campoEnergia, 
            etiquetaExtra, campoExtra, 
            new Separator(), btnAgregar, btnModificar, btnEliminar, btnLimpiar
        );
            
        return cajaVertical;
    }

    // --- Acciones de Botones ---

    private void accionAgregar() {
        try {
            Robot nuevoRobot = construirRobotDesdeForm();
            gestor.agregarRobot(nuevoRobot);
            listaObservable.add(nuevoRobot);
            limpiarFormulario();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Robot agregado correctamente.");
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    private void accionModificar() {
        Robot seleccionado = tablaRobots.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un robot de la lista.");
            return;
        }
        try {
            Robot modificado = construirRobotDesdeForm();
            
            // Validación de consistencia de tipo
            if (!modificado.obtenerTipo().equals(seleccionado.obtenerTipo())) {
                throw new ExcepcionRobot("No se puede cambiar el tipo de robot una vez creado.");
            }

            gestor.actualizarRobot(seleccionado, modificado);
            
            int indice = listaObservable.indexOf(seleccionado);
            listaObservable.set(indice, modificado);
            limpiarFormulario();
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Robot modificado.");
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al modificar", ex.getMessage());
        }
    }

    private void accionEliminar() {
        Robot seleccionado = tablaRobots.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            gestor.eliminarRobot(seleccionado);
            listaObservable.remove(seleccionado);
            limpiarFormulario();
        }
    }

    // --- Ventana Secundaria (Baja Energía) ---
    
    private void abrirVentanaBajaEnergia() {
        Stage ventanaSecundaria = new Stage();
        VBox panelRaiz = new VBox(10);
        panelRaiz.setPadding(new Insets(10));

        Label etiquetaAlerta = new Label("Robots con Energía Crítica (< 20%)");
        etiquetaAlerta.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");

        TableView<Robot> tablaBajaEnergia = crearTabla();
        ObservableList<Robot> listaFiltrada = FXCollections.observableArrayList(gestor.obtenerRobotsBajaEnergia());
        tablaBajaEnergia.setItems(listaFiltrada);

        Button btnExportarCsv = new Button("Exportar a .CSV");
        btnExportarCsv.setOnAction(e -> {
            FileChooser selectorArchivos = new FileChooser();
            selectorArchivos.setTitle("Guardar Reporte Mantenimiento");
            selectorArchivos.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
            File archivoSeleccionado = selectorArchivos.showSaveDialog(ventanaSecundaria);
            
            if (archivoSeleccionado != null) {
                try {
                    gestor.obtenerServicioArchivo().exportarCsv(listaFiltrada, archivoSeleccionado);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Exportado", "Archivo CSV generado con éxito.");
                } catch (Exception ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el archivo.");
                }
            }
        });

        panelRaiz.getChildren().addAll(etiquetaAlerta, tablaBajaEnergia, btnExportarCsv);
        Scene escenaSecundaria = new Scene(panelRaiz, 600, 400);
        ventanaSecundaria.setScene(escenaSecundaria);
        ventanaSecundaria.setTitle("Alerta de Energía");
        ventanaSecundaria.show();
    }

    // --- Utilidades ---

    private Robot construirRobotDesdeForm() throws ExcepcionRobot {
        try {
            String nombre = campoNombre.getText();
            String serie = campoSerie.getText();
            // Validamos que no esté vacío antes de parsear para evitar error feo
            if(campoEnergia.getText().isEmpty()) throw new NumberFormatException();
            int energia = Integer.parseInt(campoEnergia.getText());
            
            String tipoSeleccionado = comboTipo.getValue();
            
            if (tipoSeleccionado.equals("Doméstico")) {
                int tareas = Integer.parseInt(campoExtra.getText());
                return new RobotDomestico(nombre, energia, serie, tareas);
            } else {
                double carga = Double.parseDouble(campoExtra.getText());
                return new RobotIndustrial(nombre, energia, serie, carga);
            }
        } catch (NumberFormatException e) {
            throw new ExcepcionRobot("Por favor revise que los campos numéricos sean válidos y no estén vacíos.");
        }
    }

    private void cargarRobotEnFormulario(Robot r) {
        campoNombre.setText(r.getNombre());
        campoSerie.setText(r.getNumeroSerie());
        campoEnergia.setText(String.valueOf(r.getNivelEnergia()));
        // Para que el combo seleccione el valor correcto, debe coincidir exactamente con el String que agregamos al item
        comboTipo.setValue(r.obtenerTipo()); 
        
        // Extraemos solo el número del detalle específico
        String extra = r.obtenerDetalleEspecifico().split(" ")[0]; 
        campoExtra.setText(extra);
    }

    private void limpiarFormulario() {
        campoNombre.clear();
        campoSerie.clear();
        campoEnergia.clear();
        campoExtra.clear();
        tablaRobots.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}