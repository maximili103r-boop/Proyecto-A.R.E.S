package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class LoggerAres
{
    private static final String LOG_FILE = "ares.log";

    public static void log(String mensaje)
    {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true)))
        {
            out.println(LocalDateTime.now() + " - " + mensaje);
        } catch (IOException e)
        {
            System.err.println("Error al escribir log: " + e.getMessage());
        }
    }
}