package org.example;

public class Aplicacion
{
    //representa programa instalado en la computadora
    // convierte un ejecutable en la PC en un objeto manipulable por el codigo de java.
    private int id;
    private String nombre;
    private String ruta;
    private String palabrasClave;

    public Aplicacion()
    {
    }

    public Aplicacion(int id, String nombre, String ruta, String palabrasClave)
    {
        this.id = id;
        this.nombre = nombre;
        this.ruta = ruta;
        this.palabrasClave = palabrasClave;
    }

    // GETTERS Y SETTERSpuestas para leer GET o cambiar SET los atributos privados
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getRuta()
    {
        return ruta;
    }

    public void setRuta(String ruta)
    {
        this.ruta = ruta;
    }

    public String getPalabrasClave()
    {
        return palabrasClave;
    }

    public void setPalabrasClave(String palabrasClave)
    {
        this.palabrasClave = palabrasClave;
    }
}
