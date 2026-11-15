package org.example;

import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class AppController
{
    private RepositorioModos repositorio;
    private ProcessLauncher launcher;
    private ComboBox<Modo> comboPerfiles;
    private TextArea areaLogs;

    public AppController(ComboBox<Modo> comboPerfiles, TextArea areaLogs)
    {
        this.comboPerfiles = comboPerfiles;
        this.areaLogs = areaLogs;
        this.repositorio = new RepositorioModos();
        this.launcher = new ProcessLauncher();
    }

    public void cargarPerfiles()
    {
        List<Modo> modos = repositorio.cargarModos();
        comboPerfiles.getItems().clear();
        comboPerfiles.getItems().addAll(modos);
        if (!modos.isEmpty())
        {
            comboPerfiles.getSelectionModel().selectFirst();
        }
        areaLogs.appendText("[INFO] Lista de perfiles actualizada.\n");
    }

    public void abrirPerfil()
    {
        Modo seleccionado = comboPerfiles.getSelectionModel().getSelectedItem();
        if (seleccionado == null)
        {
            areaLogs.appendText("[WARN] No hay perfil seleccionado.\n");
            return;
        }

        // --- VALIDACIÓN PREVIA DE RECURSOS RAM ---
        if (!validarRecursosRam(seleccionado))
        {
            areaLogs.appendText("[CANCELADO] El usuario detuvo el lanzamiento del perfil '" + seleccionado.getNombre() + "' por falta de RAM.\n");
            return;
        }

        areaLogs.appendText(" Abriendo perfil: " + seleccionado.getNombre() + " (" + seleccionado.getAplicaciones().size() + " apps)\n");
        launcher.abrirModo(seleccionado);
    }

    /**
     * Evalúa la RAM disponible y el peso del perfil antes de ejecutar.
     */
    private boolean validarRecursosRam(Modo modo)
    {
        try
        {
            long[] stats = MemoryMonitor.getMemoryStatus();
            long disponibleMB = stats[1];
            int porcentajeUsado = (int) stats[3];

            double libreGB = disponibleMB / 1024.0;
            int cantidadApps = modo.getAplicaciones().size();

            // Condición 1: Más de 4 apps y menos de 2.0 GB de RAM disponible
            boolean riesgoPorCantidadYMemoria = (cantidadApps >= 4 && libreGB < 2.0);

            // Condición 2: RAM crítica global (consumo superior al 85% o menos de 1.0 GB libre)
            boolean riesgoCriticoRAM = (porcentajeUsado >= 85 || libreGB < 1.0);

            if (riesgoPorCantidadYMemoria || riesgoCriticoRAM)
            {
                // Registrar aviso en la terminal de logs
                areaLogs.appendText(String.format("[ALERTA RAM] Intentando abrir %d apps con solo %.2f GB libres (%d%% en uso).\n",
                        cantidadApps, libreGB, porcentajeUsado));

                // Mostrar Ventana Emergente Interactiva
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("⚠️ Alerta de Rendimiento de RAM - A.R.E.S.");
                alert.setHeaderText("Riesgo de Congelamiento por Memoria Insuficiente");

                String mensaje = String.format(
                        "Estás intentando abrir el perfil '%s' (%d aplicaciones).\n\n" +
                                "• RAM Disponible: %.2f GB\n" +
                                "• Uso de RAM actual: %d%%\n\n" +
                                "Abrir múltiples aplicaciones pesadas bajo estas condiciones puede saturar el sistema.\n" +
                                "¿Deseas continuar con el lanzamiento de todos modos?",
                        modo.getNombre(), cantidadApps, libreGB, porcentajeUsado
                );
                alert.setContentText(mensaje);

                ButtonType btnContinuar = new ButtonType("Lanzar de todos modos");
                ButtonType btnCancelar = new ButtonType("Cancelar Ejecución", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(btnContinuar, btnCancelar);
                alert.initOwner(comboPerfiles.getScene().getWindow());

                Optional<ButtonType> respuesta = alert.showAndWait();
                return respuesta.isPresent() && respuesta.get() == btnContinuar;
            }

        } catch (Exception e)
        {
            areaLogs.appendText("[WARN] No se pudo comprobar el estado de la RAM antes de ejecutar: " + e.getMessage() + "\n");
        }

        return true; // Si no hay riesgo o falló la lectura, permite continuar.
    }

    public void cerrarPerfil()
    {
        Modo seleccionado = comboPerfiles.getSelectionModel().getSelectedItem();
        if (seleccionado == null)
        {
            areaLogs.appendText("[WARN] No hay perfil seleccionado.\n");
            return;
        }
        launcher.cerrarModo(seleccionado);
        areaLogs.appendText(" Cerrando perfil: " + seleccionado.getNombre() + "\n");
    }

    public void cerrarTodo()
    {
        launcher.cerrarTodas();
        areaLogs.appendText(" Cerrando todas las aplicaciones.\n");
    }

    public void crearPerfil(Modo nuevo)
    {
        if (nuevo != null)
        {
            List<Modo> modos = repositorio.cargarModos();
            modos.add(nuevo);
            repositorio.guardarModos(modos);
            cargarPerfiles();
            areaLogs.appendText("[EXITO] Perfil '" + nuevo.getNombre() + "' creado.\n");
        }
    }

    public void eliminarPerfil(Modo aEliminar)
    {
        if (aEliminar != null)
        {
            List<Modo> modos = repositorio.cargarModos();
            modos.removeIf(m -> m.getNombre().equalsIgnoreCase(aEliminar.getNombre()));
            repositorio.guardarModos(modos);
            cargarPerfiles();
            areaLogs.appendText("[INFO] Perfil '" + aEliminar.getNombre() + "' eliminado.\n");
        }
    }

    public void guardarCambiosPerfiles()
    {
        List<Modo> modosActuales = comboPerfiles.getItems();
        repositorio.guardarModos(modosActuales);
        cargarPerfiles();
        areaLogs.appendText("[INFO] Cambios en perfiles guardados correctamente.\n");
    }

    public List<Modo> getModos()
    {
        return repositorio.cargarModos();
    }
}