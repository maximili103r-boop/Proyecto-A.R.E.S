package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class Main extends Application
{

    private ComboBox<Modo> comboPerfiles;
    private TextArea areaLogs;
    private AppController controller;
    private int ultimoPorcentajeAdvertido = -1;

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("A.R.E.S. - Automated Resource & Execution System");
        // --- ENCABEZADO ---
        Label lblLogo = new Label("A.R.E.S.");
        lblLogo.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblSubtitle = new Label("Automated Resource & Execution System");
        lblSubtitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");

        VBox headerTitle = new VBox(2, lblLogo, lblSubtitle);

        Button btnAyuda = new Button("❓ Ayuda");
        btnAyuda.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: #38bdf8; -fx-font-weight: bold; " +
                        "-fx-border-color: #06b6d4; -fx-border-radius: 12; -fx-background-radius: 12; " +
                        "-fx-padding: 4 10; -fx-font-size: 11px; -fx-cursor: hand;"
        );

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        HBox.setHgrow(headerTitle, Priority.ALWAYS);
        header.getChildren().addAll(headerTitle, btnAyuda);
        header.setPadding(new Insets(0, 0, 10, 0));

        // --- SECCIÓN CONTROLES DE MODO ---
        Label lblProfiles = new Label("Profiles");
        lblProfiles.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 14px; -fx-font-weight: bold;");

        comboPerfiles = new ComboBox<>();
        comboPerfiles.setPromptText("Seleccionar perfil activo...");
        comboPerfiles.setMaxWidth(Double.MAX_VALUE);
        comboPerfiles.setStyle(
                "-fx-background-color: #1e293b; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-border-color: #334155; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 4;"
        );

        comboPerfiles.setConverter(new StringConverter<>()
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

        comboPerfiles.setCellFactory(param -> new ListCell<>()
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

        Button btnAbrir = new Button("🚀 Launch Profile");
        btnAbrir.setMaxWidth(Double.MAX_VALUE);
        btnAbrir.setStyle(
                "-fx-background-color: #06b6d4; -fx-text-fill: #ffffff; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand;"
        );

        Button btnCerrar = new Button("✕ Stop Profile");
        btnCerrar.setMaxWidth(Double.MAX_VALUE);
        btnCerrar.setStyle(
                "-fx-background-color: #991b1b; -fx-text-fill: #fca5a5; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand;"
        );

        HBox profileActions = new HBox(10, btnAbrir, btnCerrar);
        HBox.setHgrow(btnAbrir, Priority.ALWAYS);
        HBox.setHgrow(btnCerrar, Priority.ALWAYS);

        VBox profileCard = new VBox(10, comboPerfiles, profileActions);
        profileCard.setStyle(
                "-fx-background-color: #1e293b; -fx-padding: 12; " +
                        "-fx-background-radius: 12; -fx-border-color: #334155; -fx-border-radius: 12;"
        );

        // --- SECCIÓN ACCIONES SECUNDARIAS (SIN BOTÓN DE ACTUALIZAR) ---
        Button btnNuevoPerfil = new Button("+ Nuevo Perfil");
        btnNuevoPerfil.setMaxWidth(Double.MAX_VALUE);
        btnNuevoPerfil.setStyle(
                "-fx-background-color: #0891b2; -fx-text-fill: #ffffff; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand;"
        );

        Button btnEditarPerfil = new Button("⚙ Editar Perfil");
        btnEditarPerfil.setMaxWidth(Double.MAX_VALUE);
        btnEditarPerfil.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: #38bdf8; -fx-font-weight: bold; " +
                        "-fx-border-color: #0891b2; -fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-padding: 8; -fx-cursor: hand;"
        );

        HBox bottomActions = new HBox(8, btnNuevoPerfil, btnEditarPerfil);
        HBox.setHgrow(btnNuevoPerfil, Priority.ALWAYS);
        HBox.setHgrow(btnEditarPerfil, Priority.ALWAYS);

        // --- SECCIÓN LOGS ---
        Label lblLogs = new Label("Logs & Terminal Output");
        lblLogs.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold;");

        areaLogs = new TextArea();
        areaLogs.setEditable(false);
        areaLogs.setPrefHeight(100);
        areaLogs.setStyle(
                "-fx-control-inner-background: #0f172a; " +
                        "-fx-text-box-border: #334155; " +
                        "-fx-text-fill: #38bdf8; " +
                        "-fx-font-family: 'Consolas', 'Courier New', monospace; " +
                        "-fx-font-size: 11px; -fx-background-radius: 8;"
        );

        // --- INDICADOR DE RAM ---
        ProgressBar barRam = new ProgressBar(0);
        barRam.setPrefWidth(Double.MAX_VALUE);
        barRam.setMaxWidth(Double.MAX_VALUE);
        barRam.setStyle("-fx-accent: #10b981; -fx-background-radius: 6; -fx-padding: 2;");

        Label lblRamTexto = new Label("RAM: --");
        lblRamTexto.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px; -fx-padding: 2 0;");
        lblRamTexto.setMaxWidth(Double.MAX_VALUE);
        lblRamTexto.setAlignment(Pos.CENTER);

        VBox ramContainer = new VBox(4, barRam, lblRamTexto);
        ramContainer.setPadding(new Insets(8, 0, 0, 0));
        ramContainer.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 8; -fx-padding: 8;");

        Tooltip ramTooltip = new Tooltip("Cargando...");
        Tooltip.install(ramContainer, ramTooltip);

        // --- CONTROLLER ---
        controller = new AppController(comboPerfiles, areaLogs);
        controller.cargarPerfiles();

        // --- EVENT HANDLERS ---
        btnAbrir.setOnAction(e -> controller.abrirPerfil());
        btnCerrar.setOnAction(e -> controller.cerrarPerfil());
        btnEditarPerfil.setOnAction(e -> abrirDialogoEditarPerfil(primaryStage));
        btnNuevoPerfil.setOnAction(e -> abrirDialogoNuevoPerfil(primaryStage));
        btnAyuda.setOnAction(e -> mostrarAyuda());

        // --- LAYOUT PRINCIPAL ---
        VBox root = new VBox(12,
                header,
                lblProfiles,
                profileCard,
                bottomActions,
                lblLogs,
                areaLogs,
                ramContainer
        );
        root.setStyle("-fx-background-color: #0f172a; -fx-padding: 16;");

        Scene scene = new Scene(root, 360, 500);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        areaLogs.appendText("[SYSTEM] A.R.E.S. Sistema online.\n");

        Timeline ramTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> actualizarInfoRam(barRam, lblRamTexto, ramTooltip))
        );
        ramTimeline.setCycleCount(Timeline.INDEFINITE);
        ramTimeline.play();

        actualizarInfoRam(barRam, lblRamTexto, ramTooltip);
    }

    private void abrirDialogoNuevoPerfil(Stage owner)
    {
        DialogoNuevoModo dialogo = new DialogoNuevoModo(owner);
        dialogo.showAndWait();
        Modo nuevo = dialogo.getModoResultado();
        if (nuevo != null)
        {
            controller.crearPerfil(nuevo);
        }
    }

    private void abrirDialogoEditarPerfil(Stage owner)
    {
        DialogoEditarModo dialogo = new DialogoEditarModo(owner, comboPerfiles.getItems());
        dialogo.showAndWait();

        if (dialogo.isPerfilEliminado())
        {
            Modo perfilABorrar = dialogo.getPerfilActual();
            if (perfilABorrar != null)
            {
                controller.eliminarPerfil(perfilABorrar);
            }
        } else if (dialogo.isCambioRealizado())
        {
            controller.guardarCambiosPerfiles();
        }
    }

    private void mostrarAyuda()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guía de Uso - A.R.E.S.");
        alert.setHeaderText("A.R.E.S. — Automated Resource & Execution System");

        // Texto estructurado y detallado para la ventana emergente
        String textoAyuda =
                "¡Bienvenido a A.R.E.S.! Esta herramienta te permite automatizar y gestionar tus entornos de trabajo de forma eficiente.\n\n" +
                        "1. CREACIÓN Y GESTIÓN DE PERFILES\n" +
                        "   • Ingresa a 'Crear nuevo perfil' para agrupar tus aplicaciones.\n" +
                        "   • Es obligatorio asignar un nombre al perfil para habilitar la adición de archivos ejecutable (.exe).\n\n" +
                        "2. EJECUCIÓN Y CIERRE ESCALONADO\n" +
                        "   • 'Iniciar Perfil': Abre las aplicaciones asociadas con un retardo de 1.5 segundos entre cada una para evitar el congelamiento del sistema.\n" +
                        "   • 'Detener Perfil': Cierra únicamente los procesos y aplicaciones pertenecientes al perfil seleccionado.\n\n" +
                        "3. MONITOREO DE HARDWARE Y RECURSOS\n" +
                        "   • La barra de estado supervisa el uso de la memoria RAM en tiempo real.\n" +
                        "   • Si el consumo de RAM es crítico o la memoria disponible es baja, el sistema emitirá alertas de advertencia para proteger el rendimiento de tu equipo.\n\n" +
                        "4. VALIDACIÓN DE RUTAS\n" +
                        "   • A.R.E.S. verifica que las rutas .exe existan antes de ejecutarlas. Si un archivo fue movido o eliminado, el sistema te notificará mediante una alerta.";

        alert.setContentText(textoAyuda);

        // Ajustar el tamaño del diálogo para que el texto se lea cómodamente
        alert.getDialogPane().setPrefSize(520, 380);
        alert.initOwner(comboPerfiles.getScene().getWindow());
        alert.showAndWait();
    }

    private void actualizarInfoRam(ProgressBar barRam, Label lblRamTexto, Tooltip tooltip)
    {
        try
        {
            long[] stats = MemoryMonitor.getMemoryStatus();
            long totalMB = stats[0];
            long disponibleMB = stats[1];
            int porcentaje = (int) stats[3];

            double totalGB = totalMB / 1024.0;
            double libreGB = disponibleMB / 1024.0;

            String texto = String.format("RAM: %.1f GB libre / %.1f GB (%d%% usado)", libreGB, totalGB, porcentaje);
            lblRamTexto.setText(texto);

            double progreso = porcentaje / 100.0;
            barRam.setProgress(progreso);

            // Cambios de color y avisos periódicos en logs si se alcanza umbral alto
            String color;
            if (porcentaje < 70)
            {
                color = "#10b981";
                ultimoPorcentajeAdvertido = -1;
            } else if (porcentaje < 85)
            {
                color = "#f59e0b";
            } else
            {
                color = "#ef4444";
                if (ultimoPorcentajeAdvertido != porcentaje)
                {
                    areaLogs.appendText(String.format("[ALERTA SISTEMA] Consumo de RAM crítico (%d%% usado / %.1f GB libre).\n", porcentaje, libreGB));
                    ultimoPorcentajeAdvertido = porcentaje;
                }
            }
            barRam.setStyle("-fx-accent: " + color + "; -fx-background-radius: 6; -fx-padding: 2;");

            tooltip.setText(String.format("Total: %d MB\nDisponible: %d MB\nUsado: %d MB\nUso: %d%%",
                    totalMB, disponibleMB, totalMB - disponibleMB, porcentaje));

        } catch (Exception e)
        {
            lblRamTexto.setText("RAM: No disponible");
            barRam.setProgress(0);
            barRam.setStyle("-fx-accent: #64748b; -fx-background-radius: 6; -fx-padding: 2;");
            tooltip.setText("No se pudo obtener la información de RAM.");
            LoggerAres.log("Error al obtener RAM: " + e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}