package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class DialogoNuevoModo extends Stage
{

    private final TextField txtNombre;
    private final ListView<String> listaApps;
    private final Button btnAgregarApp; // Declarado como campo para poder controlarlo
    private final Button btnGuardar;
    private final Button btnCancelar;

    private Modo modoResultado;

    public DialogoNuevoModo(Stage owner)
    {
        setTitle("Crear nuevo perfil - A.R.E.S.");
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        // --- ENCABEZADO Y TÍTULO ---
        Label lblTitulo = new Label("Crear nuevo perfil");
        lblTitulo.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-font-weight: bold;");

        // --- CAMPO NOMBRE ---
        Label lblNombre = new Label("Nombre:");
        lblNombre.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");

        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del perfil (ej. Estudio, Trabajo...)");
        txtNombre.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: #ffffff; " +
                        "-fx-prompt-text-fill: #64748b; -fx-border-color: #06b6d4; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8;"
        );

        // --- LISTA DE APLICACIONES ---
        Label lblApps = new Label("Aplicaciones:");
        lblApps.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");

        listaApps = new ListView<>();
        listaApps.setPrefHeight(130);
        listaApps.setStyle(
                "-fx-control-inner-background: #1e293b; -fx-background-color: #1e293b; " +
                        "-fx-text-fill: #38bdf8; -fx-border-color: #334155; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        // --- BOTÓN ÚNICO AGREGAR APLICACIÓN ---
        btnAgregarApp = new Button(" Agregar aplicación (.exe)");
        btnAgregarApp.setMaxWidth(Double.MAX_VALUE);
        btnAgregarApp.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: #38bdf8; -fx-font-weight: bold; " +
                        "-fx-border-color: #06b6d4; -fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-padding: 10; -fx-cursor: hand;"
        );

        // --- PASO 1: BLOQUEO/DESBLOQUEO SEGÚN EL NOMBRE ---
        btnAgregarApp.setDisable(true); // Deshabilitado por defecto

        txtNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean nombreValido = newValue != null && !newValue.trim().isEmpty();
            btnAgregarApp.setDisable(!nombreValido);
        });

        // --- BOTONES GUARDAR Y CANCELAR ---
        btnGuardar = new Button("Guardar perfil");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);
        btnGuardar.setStyle(
                "-fx-background-color: #06b6d4; -fx-text-fill: #ffffff; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;"
        );

        btnCancelar = new Button("Cancelar");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: #cbd5e1; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;"
        );

        HBox actionButtons = new HBox(10, btnGuardar, btnCancelar);
        HBox.setHgrow(btnGuardar, Priority.ALWAYS);
        HBox.setHgrow(btnCancelar, Priority.ALWAYS);

        // --- EVENTOS ---
        btnAgregarApp.setOnAction(e -> agregarAplicacion());
        btnGuardar.setOnAction(e -> guardarPerfil());
        btnCancelar.setOnAction(e -> cerrarDialogo());

        // --- CONTENEDOR PRINCIPAL ---
        VBox root = new VBox(12,
                lblTitulo,
                lblNombre, txtNombre,
                lblApps, listaApps,
                btnAgregarApp,
                actionButtons
        );
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #0f172a;");

        Scene scene = new Scene(root, 340, 430);
        setScene(scene);
        setResizable(false);
    }

    private void agregarAplicacion()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar aplicación");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Ejecutables (*.exe)", "*.exe")
        );
        File archivo = fileChooser.showOpenDialog(this);
        if (archivo != null)
        {
            String ruta = archivo.getAbsolutePath();
            if (!listaApps.getItems().contains(ruta))
            {
                listaApps.getItems().add(ruta);
            }
        }
    }

    private void guardarPerfil()
    {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("El nombre del perfil no puede estar vacío.");
            alert.initOwner(this);
            alert.showAndWait();
            return;
        }
        if (listaApps.getItems().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Debes agregar al menos una aplicación.");
            alert.initOwner(this);
            alert.showAndWait();
            return;
        }

        modoResultado = new Modo(nombre);
        for (String ruta : listaApps.getItems())
        {
            Aplicacion app = new Aplicacion(0, "App", ruta, "");
            modoResultado.agregarAplicacion(app);
        }

        close();
    }

    private void cerrarDialogo()
    {
        modoResultado = null;
        close();
    }

    public Modo getModoResultado()
    {
        return modoResultado;
    }
}