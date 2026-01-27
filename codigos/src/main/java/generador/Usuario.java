package generador;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.apache.commons.lang3.RandomStringUtils;

public class Usuario {
    private final String nombre;
    private final String codigoSeguridad;
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
    private static final int min_nombre = 3;
    private static final int max_dias_inactivo = 30;
    private static final int max_dias_bloqueado = 60;

    private static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value, T defaultValue) {
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (Exception e) {
            return defaultValue;
        }
    }

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
        this.codigoSeguridad = generarCodigoSeguro(tipoDeUsuario.longitudDeCodigo);
        this.fechaDeCreacion = LocalDate.now();
        this.ultimaActividad = LocalDate.now();
    }

    private Usuario(String nombre, TipoDeUsuario tipo, EstadoUsuario estado, String codigo, LocalDate fechaCreacion, LocalDate ultimaActividad) {
        this.nombre = nombre;
        this.tipoDeUsuario = tipo;
        this.estadoUsuario = estado;
        this.codigoSeguridad = codigo;
        this.fechaDeCreacion = fechaCreacion;
        this.ultimaActividad = ultimaActividad;
    }

    public static Usuario fromDB(String nombre, String tipoStr, String estadoStr, String codigoSeguridad, LocalDate fechaCreacion, LocalDate ultimaActividad) {
        TipoDeUsuario tipo = parseEnum(TipoDeUsuario.class, tipoStr, TipoDeUsuario.NORMAL);
        EstadoUsuario estado = parseEnum(EstadoUsuario.class, estadoStr, EstadoUsuario.ACTIVO);
        return new Usuario(
            nombre,
            tipo, 
            estado, 
            codigoSeguridad, 
            fechaCreacion != null ? fechaCreacion : LocalDate.now(), 
            ultimaActividad != null ? ultimaActividad : LocalDate.now()
        );
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

    public void actualizarActividad() {
        if (estadoUsuario == EstadoUsuario.BLOQUEADO) {
            throw new IllegalStateException("No se puede actualizar la actividad de un usuario bloqueado");
        }
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
    @Override
    public String toString() {
        return """
           Usuario {
             nombre='%s',
             tipo=%s,
             estado=%s,
             codigo='%s',
             creado=%s,
             diasActivo=%d
           }
           """.formatted(
                nombre,
                tipoDeUsuario,
                estadoUsuario,
                codigoSeguridad,
                fechaDeCreacion,
                diasDespuesDeCreacion()
           );
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

    private static String generarCodigoSeguro(int longitud){
        return RandomStringUtils.random(longitud, true, true).toUpperCase();
    }

    public void actualizarEstadoPorInactividad() {
        if (estadoUsuario == EstadoUsuario.BLOQUEADO) return;

        long diasInactivo = ChronoUnit.DAYS.between(ultimaActividad, LocalDate.now());
        if (diasInactivo > max_dias_bloqueado) {
            estadoUsuario = EstadoUsuario.BLOQUEADO;
        } else if (diasInactivo > max_dias_inactivo) {
            estadoUsuario = EstadoUsuario.INACTIVO;
        } else {
            estadoUsuario = EstadoUsuario.ACTIVO;
        }
    }

    public void desbloquearPorAdmin(TipoDeUsuario solicitante) {
        if (solicitante != TipoDeUsuario.ADMIN) {
            throw new IllegalArgumentException("Solo un usuario ADMIN puede desbloquear usuarios.");
        }
        if (estadoUsuario != EstadoUsuario.BLOQUEADO) {
            throw new IllegalStateException("El usuario no está bloqueado.");
        }
        this.estadoUsuario = EstadoUsuario.ACTIVO;
        this.ultimaActividad = LocalDate.now();
    }
}
