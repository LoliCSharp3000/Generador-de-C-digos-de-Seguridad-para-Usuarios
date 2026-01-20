package generador;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    public enum EstadoUsuario{
        ACTIVO,
        INACTIVO,
        BLOQUEADO
    }
    private final TipoDeUsuario tipoDeUsuario;
    private EstadoUsuario estadoUsuario;
    private final LocalDate fechaDeCreacion;
    private LocalDate ultimaActividad;
    private final int min_nombre = 3;
    private final int max_dias_inactivo = 30;

    public Usuario(String nombre, int opc){
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes poner el nombre correctamente");
        }
        if (nombre.trim().length() < min_nombre) {
            throw new IllegalArgumentException("El nombre es demasiado corto");
        }
        if (nombre.matches(".*\\d.*")) {
            throw new IllegalArgumentException("El nombre no puede contener números");
        }
        this.nombre = nombre;
        TipoDeUsuario tipo;
        switch (opc) {
            case 1 -> tipo = TipoDeUsuario.NORMAL;
            case 2 -> tipo = TipoDeUsuario.PREMIUM;
            case 3 -> tipo = TipoDeUsuario.ADMIN;
            default -> throw new IllegalArgumentException("Pon el numero correcto");
        };
        this.tipoDeUsuario = tipo;
        this.estadoUsuario = EstadoUsuario.ACTIVO;
        this.codigoSeguridad = RandomStringUtils.randomAlphanumeric(tipoDeUsuario.longitudDeCodigo);
        this.fechaDeCreacion = LocalDate.now();
        this.ultimaActividad = LocalDate.now();
        totalUsuarios++;
    }
    public static void setTotalUsuarios(int totalUsuarios) {
        Usuario.totalUsuarios = totalUsuarios;
    }
    public LocalDate getFechaDeCreacion() {
        return fechaDeCreacion;
    }
    public TipoDeUsuario getTipoDeUsuario() {
        return tipoDeUsuario;
    }
    public EstadoUsuario getEstadoUsuario() {
        return estadoUsuario;
    }
    public LocalDate getUltimaActividad() {
        return ultimaActividad;
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
    public void actualizarActividad() {
        this.ultimaActividad = LocalDate.now();
        this.estadoUsuario = EstadoUsuario.ACTIVO;
    }
    public boolean esInactivo() {
        return ChronoUnit.DAYS.between(ultimaActividad, LocalDate.now()) > max_dias_inactivo;
    }
    public void marcarInactivoSiNecesario() {
        if (esInactivo()) {
            estadoUsuario = EstadoUsuario.INACTIVO;
        }
    }
    public String mostrarInfo() {
        return  "Nombre: " + nombre +
                " | Tipo: " + tipoDeUsuario +
                " | Código: " + codigoSeguridad +
                " | Estado: " + estadoUsuario +
                " | Fecha de creacion: " + fechaDeCreacion +
                " | Última actividad: " + ultimaActividad +
                " | Días desde creación: " + diasDespuesDeCreacion();
    }


    public long diasDespuesDeCreacion(){
        return ChronoUnit.DAYS.between(fechaDeCreacion, LocalDate.now());
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
