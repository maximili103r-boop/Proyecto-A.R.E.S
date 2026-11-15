package org.example;

import java.util.ArrayList;
import java.util.List;

public class Modo
{
    private String nombre; //NOmbre del modo
    //cONTENEDOR:
    private List<Aplicacion> aplicaciones;

    public Modo()
    {
        this.aplicaciones = new ArrayList<>(); //iniciar Lista vacia
    }

    public Modo(String nombre)
    {
        this.nombre = nombre;
        this.aplicaciones = new ArrayList<>();
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public List<Aplicacion> getAplicaciones()
    {
        return aplicaciones;
    }

    public void setAplicaciones(List<Aplicacion> aplicaciones)
    {
        this.aplicaciones = aplicaciones;
    }

    //metodo AYUDANTE (agregar una aplicacion a la lista de forma facil)
    public void agregarAplicacion(Aplicacion app)
    {
        this.aplicaciones.add(app);
    }
}
