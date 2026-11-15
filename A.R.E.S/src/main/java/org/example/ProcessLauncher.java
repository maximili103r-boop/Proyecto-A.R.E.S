package org.example;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessLauncher
{

    private final Map<Modo, List<Process>> procesosPorModo = new HashMap<>();
    private final Set<String> rutasAbiertas = new HashSet<>();

    /**
     * Abre todas las aplicaciones de un modo de forma escalonada (1.5s entre cada una).
     * Evita duplicados y valida que el archivo exista en la ruta especificada.
     */
    public void abrirModo(Modo modo)
    {
        List<Process> procesosDelModo = new ArrayList<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        List<Aplicacion> apps = modo.getAplicaciones();

        for (int i = 0; i < apps.size(); i++)
        {
            String ruta = apps.get(i).getRuta();
            long delay = i * 1500L; // conversión explícita a long
            scheduler.schedule(() ->
            {
                if (rutasAbiertas.contains(ruta))
                {
                    LoggerAres.log("!! " + ruta + " ya está abierta.");
                    return;
                }

                // --- VALIDACIÓN DE EXISTENCIA DE RUTA ---
                File archivoExe = new File(ruta);
                if (!archivoExe.exists())
                {
                    LoggerAres.log(" Error: No se encontró la aplicación en la ruta: " + ruta);

                    // Alerta emergente en la interfaz JavaFX
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Aplicación no encontrada");
                        alert.setHeaderText("No se pudo iniciar la aplicación");
                        alert.setContentText("El archivo ejecutable no existe en la ruta:\n\n" + ruta);
                        alert.showAndWait();
                    });
                    return; // Cancela el intento de abrir este ejecutable en particular
                }

                try
                {
                    ProcessBuilder pb = new ProcessBuilder(ruta);
                    Process proceso = pb.start();
                    procesosDelModo.add(proceso);
                    rutasAbiertas.add(ruta);
                    LoggerAres.log("Abierto: " + ruta);
                } catch (IOException e)
                {
                    LoggerAres.log("Error al abrir " + ruta + ": " + e.getMessage());
                }
            }, delay, TimeUnit.MILLISECONDS);
        }

        long tiempoTotal = apps.size() * 1500L + 500L;
        scheduler.schedule(() ->
        {
            scheduler.shutdown();
            procesosPorModo.put(modo, procesosDelModo);
        }, tiempoTotal, TimeUnit.MILLISECONDS);
    }

    /**
     * Cierra SOLO las aplicaciones del modo indicado.
     * Combina la destrucción de procesos por referencia con 'taskkill' nativo de Windows
     * para asegurar el cierre de navegadores o programas con confirmaciones pendientes.
     */
    public void cerrarModo(Modo modo)
    {
        List<Process> procesos = procesosPorModo.remove(modo);

        // 1. Intentar cierre por referencia de objeto Process
        if (procesos != null)
        {
            for (Process p : procesos)
            {
                if (p.isAlive())
                {
                    p.destroy();
                    try
                    {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored)
                    {
                    }
                    if (p.isAlive())
                    {
                        p.destroyForcibly();
                    }
                }
            }
        }

        // 2. Cierre forzoso de respaldo por nombre ejecutable (taskkill)
        for (Aplicacion app : modo.getAplicaciones())
        {
            String ruta = app.getRuta();
            rutasAbiertas.remove(ruta);

            File archivo = new File(ruta);
            String nombreExe = archivo.getName();

            if (nombreExe.toLowerCase().endsWith(".exe"))
            {
                try
                {
                    // Cierra el proceso y todos sus subprocesos hijos (-T) de manera forzada (-F)
                    ProcessBuilder pbTaskkill = new ProcessBuilder("taskkill", "/F", "/T", "/IM", nombreExe);
                    pbTaskkill.start();
                    LoggerAres.log("Cierre forzoso enviado a: " + nombreExe);
                } catch (IOException e)
                {
                    LoggerAres.log("Error en taskkill para " + nombreExe + ": " + e.getMessage());
                }
            }
        }

        LoggerAres.log(" Perfil '" + modo.getNombre() + "' cerrado completamente.");
    }

    /**
     * Cierra TODAS las aplicaciones lanzadas por A.R.E.S.
     */
    public void cerrarTodas()
    {
        for (List<Process> lista : procesosPorModo.values())
        {
            for (Process p : lista)
            {
                if (p.isAlive())
                {
                    p.destroy();
                    try
                    {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored)
                    {
                    }
                    if (p.isAlive())
                    {
                        p.destroyForcibly();
                    }
                }
            }
        }

        // Aplicar taskkill a todas las rutas registradas
        for (String ruta : new HashSet<>(rutasAbiertas))
        {
            File archivo = new File(ruta);
            String nombreExe = archivo.getName();
            if (nombreExe.toLowerCase().endsWith(".exe"))
            {
                try
                {
                    ProcessBuilder pbTaskkill = new ProcessBuilder("taskkill", "/F", "/T", "/IM", nombreExe);
                    pbTaskkill.start();
                } catch (IOException ignored)
                {
                }
            }
        }

        procesosPorModo.clear();
        rutasAbiertas.clear();
        LoggerAres.log(" Todas las aplicaciones cerradas.");
    }
}