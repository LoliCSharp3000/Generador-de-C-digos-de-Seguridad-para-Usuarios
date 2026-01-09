package generador;

import org.apache.commons.lang3.RandomStringUtils;

public final class Usuario {
    private final String nombre;
    private final String codigoSeguridad;
    private static int totalUsuarios;

    public Usuario(String nombre){
        if (nombre.length() < 3) {
            throw new IllegalArgumentException("El nombre es demasiado corto");
        }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Usuario)) return false;
        Usuario usuario = (Usuario) obj;
        return codigoSeguridad.equals(usuario.codigoSeguridad);
    }

    @Override
    public int hashCode() {
        return codigoSeguridad.hashCode();
    }
}
