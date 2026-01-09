package generador;

import org.apache.commons.lang3.RandomStringUtils;

public final class Usuario {
    private final String nombre;
    private final String codigoSeguridad;
    private static int totalUsuarios;
    public enum TipoDeUsuario{
        NORMAL(6),
        PREMIUM(8),
        ADMIN(12);

        private final int longitudDeCodigo;

        TipoDeUsuario(int longitudDeCodigo){
            this.longitudDeCodigo = longitudDeCodigo;
        }

        public int getLongitudDeCodigo() {
            return longitudDeCodigo;
        }
    }
    private final TipoDeUsuario tipoDeUsuario;

    public Usuario(String nombre, int opc){
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes poner el nombre correctamente");
        }
        if (nombre.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre es demasiado corto");
        }
        this.nombre = nombre;
        TipoDeUsuario tipoDeUsuario = switch (opc) {
            case 1 -> tipoDeUsuario = TipoDeUsuario.NORMAL;
            case 2 -> tipoDeUsuario = TipoDeUsuario.PREMIUM;
            case 3 -> tipoDeUsuario = TipoDeUsuario.ADMIN;
            default -> throw new IllegalArgumentException("Pon el numero correcto");
        };
        this.tipoDeUsuario = tipoDeUsuario;
        this.codigoSeguridad = RandomStringUtils.randomAlphanumeric(tipoDeUsuario.longitudDeCodigo);
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
        return "Nombre: " + nombre + " | Tipo: " + tipoDeUsuario + " | Código: " + codigoSeguridad;
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
