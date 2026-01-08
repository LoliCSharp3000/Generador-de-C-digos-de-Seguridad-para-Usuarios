package generador;

import org.apache.commons.lang3.RandomStringUtils;

public class Usuario {
    private final String nombre;
    private final String codigoSeguridad;
    public static int totalUsuarios;

    public Usuario(String nombre){
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes poner el nombre correctamente");
        } else{
            this.nombre = nombre;
        }
        this.codigoSeguridad = RandomStringUtils.randomAlphanumeric(8);
        totalUsuarios++;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    public static int getTotalUsuarios() {
        return totalUsuarios;
    }
    public String mostrarInfo() {
        return "Nombre: " + nombre + " | Código: " + codigoSeguridad;
    }

}
