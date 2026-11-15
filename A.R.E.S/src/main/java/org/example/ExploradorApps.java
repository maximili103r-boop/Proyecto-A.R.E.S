package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExploradorApps
{

    /**
     * Obtiene una lista de nombres de aplicaciones instaladas (desde el registro de Windows).
     * Ejecuta PowerShell y filtra resultados vacíos.
     *
     * @return Lista de nombres de aplicaciones (puede ser vacía si hay error).
     */
    public static List<String> obtenerAppsInstaladas()
    {
        List<String> apps = new ArrayList<>();
        try
        {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-command",
                    "Get-ItemProperty HKLM:\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | " +
                            "Where-Object { $_.DisplayName -ne $null } | Select-Object -ExpandProperty DisplayName"
            );
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (!line.trim().isEmpty())
                {
                    apps.add(line.trim());
                }
            }
            process.waitFor();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return apps;
    }

    /**
     * también escanea directorios comunes (Program Files) para .exe.
     * Útil si algunas apps no aparecen en el registro.
     */
    public static List<String> obtenerAppsCompletas()
    {
        List<String> apps = obtenerAppsInstaladas();
        // Podrías añadir lógica para escanear carpetas como "C:\\Program Files" y "C:\\Program Files (x86)"
        // y buscar archivos .exe. Por simplicidad, solo devolvemos lo del registro.
        return apps;
    }
}