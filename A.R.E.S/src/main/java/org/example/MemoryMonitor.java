package org.example;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;

/**
 * Clase para monitorear el uso de memoria RAM en Windows.
 * Utiliza JNA para llamar a la API GlobalMemoryStatusEx.
 */
public class MemoryMonitor
{

    /**
     * Obtiene el estado actual de la memoria RAM.
     *
     * @return un arreglo de 4 valores: [totalMB, disponibleMB, usadoMB, porcentajeUso]
     * @throws RuntimeException si no se puede obtener la información (ej. sistema no Windows)
     */
    public static long[] getMemoryStatus()
    {
        // Verificar que estamos en Windows
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win"))
        {
            throw new RuntimeException("MemoryMonitor solo funciona en Windows.");
        }

        WinBase.MEMORYSTATUSEX memoryStatus = new WinBase.MEMORYSTATUSEX();
        boolean success = Kernel32.INSTANCE.GlobalMemoryStatusEx(memoryStatus);

        if (!success)
        {
            throw new RuntimeException("No se pudo obtener el estado de la memoria.");
        }

        long total = memoryStatus.ullTotalPhys.longValue();
        long disponible = memoryStatus.ullAvailPhys.longValue();
        long usado = total - disponible;
        int porcentaje = memoryStatus.dwMemoryLoad.intValue();

        // Convertir a MB
        long totalMB = total / (1024 * 1024);
        long disponibleMB = disponible / (1024 * 1024);
        long usadoMB = usado / (1024 * 1024);

        return new long[]{totalMB, disponibleMB, usadoMB, porcentaje};
    }
}