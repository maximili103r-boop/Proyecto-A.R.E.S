package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;

public class DialogoEditarModo extends Stage
{

    private final ComboBox<Modo> comboSeleccionPerfil;
    private final TextField txtNombre;
    private final ListView<String> listaApps;
    private Modo perfilActual;

    private boolean cambioRealizado = false;
    private boolean perfilEliminado = false;

    public DialogoEditarModo(Stage owner, List<Modo> perfilesExistentes)
    {
        setTitle("Editar Perfiles - A.R.E.S.");
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        // --- ENCABEZADO ---
        Label lblTitulo = new Label("Gestión y Edición de Perfiles");
        lblTitulo.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-font-weight: bold;");

        // --- SELECCIONAR PERFIL ---
        Label lblSeleccionar = new Label("Seleccionar Perfil:");
        lblSeleccionar.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");

        comboSeleccionPerfil = new ComboBox<>();
        comboSeleccionPerfil.getItems().addAll(perfilesExistentes);
        comboSeleccionPerfil.setPromptText("Elige un perfil para editar...");
        comboSeleccionPerfil.setMaxWidth(Double.MAX_VALUE);
        comboSeleccionPerfil.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: #ffffff; " +
                        "-fx-border-color: #06b6d4; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4;"
        );

        comboSeleccionPerfil.setConverter(new StringConverter<>()
        {
            @Override
            public String toString(Modo modo)
            {
                return (modo != null) ? modo.getNombre() : "";
            }

            @Override
            public Modo fromString(String string)
            {
                return null;
            }
        });

        comboSeleccionPerfil.setCellFactory(param -> new ListCell<>()
        {
            @Override
            protected void updateItem(Modo item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty || item == null)
                {
                    setText(null);
                } else
                {
                    setText(item.getNombre());
                    setStyle("-fx-text-fill: #ffffff; -fx-background-color: #1e293b;");
                }
            }
        });

        // --- EDITAR NOMBRE ---
        Label lblNombre = new Label("Nombre del perfil:");
        lblNombre.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");

        txtNombre = new TextField();
        txtNombre.setPromptText("Nuevo nombre de perfil...");
        txtNombre.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: #ffffff; " +
                        "-fx-prompt-text-fill: #64748b; -fx-border-color: #334155; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8;"
        );

        // --- LISTA DE APLICACIONES ---
        Label lblApps = new Label("Aplicaciones vinculadas:");
        lblApps.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");

        listaApps = new ListView<>();
        listaApps.setPrefHeight(130);
        listaApps.setStyle(
                "-fx-control-inner-background: #1e293b; -fx-background-color: #1e293b; " +
                        "-fx-text-fill: #38bdf8; -fx-border-color: #334155; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        listaApps.setCellFactory(param -> new ListCell<>()
        {
            private final Button btnRemove = new Button("✕");
            private final Label lblText = new Label();
            private final HBox container = new HBox(8, lblText, btnRemove);

            {
                lblText.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 11px;");
                HBox.setHgrow(lblText, Priority.ALWAYS);
                container.setAlignment(Pos.CENTER_LEFT);
                btnRemove.setStyle(
                        "-fx-background-color: #7f1d1d; -fx-text-fill: #fecaca; " +
                                "-fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 2 6; -fx-cursor: hand;"
                );
                btnRemove.setOnAction(e ->
                {
                    String item = getItem();
                    if (item != null)
                    {
                        getListView().getItems().remove(item);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty || item == null)
                {
                    setGraphic(null);
                } else
                {
                    lblText.setText(item);
                    setGraphic(container);
                }
            }
        });

        // --- BOTÓN AGREGAR APLICACIÓN ---
        Button btnAgregarApp = new Button(" Agregar aplicación (.exe)");
        btnAgregarApp.setMaxWidth(Double.MAX_VALUE);
        btnAgregarApp.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: #38bdf8; -fx-font-weight: bold; " +
                        "-fx-border-color: #06b6d4; -fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-padding: 8; -fx-cursor: hand;"
        );
        btnAgregarApp.setOnAction(e -> agregarAplicacion());

        // --- BOTONES DE ACCIÓN ---
        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);
        btnGuardar.setStyle(
                "-fx-background-color: #06b6d4; -fx-text-fill: #ffffff; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand;"
        );

        Button btnEliminarPerfil = new Button(" Eliminar Perfil");
        btnEliminarPerfil.setMaxWidth(Double.MAX_VALUE);
        btnEliminarPerfil.setStyle(
                "-fx-background-color: #991b1b; -fx-text-fill: #fca5a5; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand;"
        );

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: #cbd5e1; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand;"
        );

        HBox actionButtons = new HBox(8, btnGuardar, btnEliminarPerfil, btnCancelar);
        HBox.setHgrow(btnGuardar, Priority.ALWAYS);
        HBox.setHgrow(btnEliminarPerfil, Priority.ALWAYS);
        HBox.setHgrow(btnCancelar, Priority.ALWAYS);

        // --- EVENTOS ---
        comboSeleccionPerfil.setOnAction(e -> cargarDatosPerfil());
        btnGuardar.setOnAction(e -> guardarCambios());
        btnEliminarPerfil.setOnAction(e -> eliminarPerfilCompleto());
        btnCancelar.setOnAction(e -> close());

        VBox root = new VBox(10,
                lblTitulo,
                lblSeleccionar, comboSeleccionPerfil,
                lblNombre, txtNombre,
                lblApps, listaApps, btnAgregarApp,
                actionButtons
        );
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #0f172a;");

        Scene scene = new Scene(root, 360, 480);
        setScene(scene);
        setResizable(false);
    }

    private void cargarDatosPerfil()
    {
        perfilActual = comboSeleccionPerfil.getValue();
        if (perfilActual != null)
        {
            txtNombre.setText(perfilActual.getNombre());
            listaApps.getItems().clear();
            for (Aplicacion app : perfilActual.getAplicaciones())
            {
                listaApps.getItems().add(app.getRuta());
            }
        }
    }

    private void agregarAplicacion()
    {
        if (perfilActual == null)
        {
            mostrarAlerta("Atención", "Selecciona primero un perfil para editar.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar aplicación");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ejecutables (*.exe)", "*.exe"));
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

    private void guardarCambios()
    {
        if (perfilActual == null)
        {
            mostrarAlerta("Error", "Debes seleccionar un perfil.");
            return;
        }
        String nuevoNombre = txtNombre.getText().trim();
        if (nuevoNombre.isEmpty())
        {
            mostrarAlerta("Error", "El nombre del perfil no puede estar vacío.");
            return;
        }
        if (listaApps.getItems().isEmpty())
        {
            mostrarAlerta("Error", "El perfil debe tener al menos una aplicación.");
            return;
        }

        perfilActual.setNombre(nuevoNombre);
        perfilActual.getAplicaciones().clear();
        for (String ruta : listaApps.getItems())
        {
            perfilActual.agregarAplicacion(new Aplicacion(0, "App", ruta, ""));
        }

        cambioRealizado = true;
        close();
    }

    private void eliminarPerfilCompleto()
    {
        if (perfilActual == null)
        {
            mostrarAlerta("Error", "Selecciona un perfil a eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro de que deseas eliminar el perfil '" + perfilActual.getNombre() + "'?");
        confirm.initOwner(this);

        confirm.showAndWait().ifPresent(response ->
        {
            if (response == ButtonType.OK)
            {
                perfilEliminado = true;
                close();
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.initOwner(this);
        alert.showAndWait();
    }

    public boolean isCambioRealizado()
    {
        return cambioRealizado;
    }

    public boolean isPerfilEliminado()
    {
        return perfilEliminado;
    }

    public Modo getPerfilActual()
    {
        return perfilActual;
    }
}