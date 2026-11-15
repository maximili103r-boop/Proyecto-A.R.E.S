package org.example;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RepositorioModos
{
    //El nombre fijo del archivo donde se guardan los datos
    private static final String ARCHIVO_Json = "modos.json";
    /**
     * Herramienta de Gson. "setPrettyPrinting()" hace que el archivo .json
     * tenga saltos de línea y sangrías para que sea fácil de leer por humanos.
     */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Método para cargar la lista de perfiles desde el disco duro.
     * Si el archivo no existe, crea uno nuevo con un perfil de ejemplo.
     */
    public static List<Modo> cargarModos()
    {
        File archivo = new File(ARCHIVO_Json);
        //SI EL ARCHIVO NO EXISTE:
        if (!archivo.exists())
        {
            List<Modo> ejemplo = crearEjemploInicial();
            guardarModos(ejemplo);
            return ejemplo;
        }
        //si el archivo si existe leemos y lo convertimos en objeto
        try (Reader reader = new FileReader(archivo))
        {
            //TypeToken es una tecnica de Gson para decir ejemplo "Esto es una lista de objetos Modo"
            Type tipoLista = new TypeToken<List<Modo>>()
            {
            }.getType();
            return gson.fromJson(reader, tipoLista);
        } catch (IOException e)
        {
            e.printStackTrace();// Sí hay error al leer imprimir error
            return new ArrayList<>(); //Devolver una lista vacia paraque el programa no se rompa
        }
    }

    /**
     * Método para guardar los cambios en el disco duro.
     * Recibe la lista actualizada de perfiles y la escribe en el archivo modos.json
     */
    public static void guardarModos(List<Modo> modos)
    {
        try (Writer writer = new FileWriter(ARCHIVO_Json))
        {
            gson.toJson(modos, writer); //convierte objetos a texto y los escribe
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Método privado (solo se usa aquí dentro) que crea un perfil "Trabajo"
     * con el Bloc de notas y la Calculadora. Sirve para que el usuario tenga
     * algo con qué probar el programa desde el primer arranque.
     */
    private static List<Modo> crearEjemploInicial()
    {
        List<Modo> lista = new ArrayList<>();
        Modo trabajo = new Modo("Trabajo");
        trabajo.agregarAplicacion(new Aplicacion(1, "bloc de notas", "notepad.exe", "texto , notas"));
        trabajo.agregarAplicacion(new Aplicacion(2, "Calculadora", "calc.exe", "matematicas , numeros"));
        lista.add(trabajo);
        return lista;
    }
}
