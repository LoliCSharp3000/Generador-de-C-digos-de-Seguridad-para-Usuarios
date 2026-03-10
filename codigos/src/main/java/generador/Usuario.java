package generador;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;

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
    private static final int max_dias_inactivo = 30;
    private static final int max_dias_bloqueado = 60;
    private String passwordHash;

    private static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value, T defaultValue) {
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private Usuario(String nombre, TipoDeUsuario tipo, EstadoUsuario estado, String codigo, LocalDate fechaCreacion, LocalDate ultimaActividad, String passwordHash) {
        this.nombre = nombre;
        this.tipoDeUsuario = tipo;
        this.estadoUsuario = estado;
        this.codigoSeguridad = codigo;
        this.fechaDeCreacion = fechaCreacion;
        this.ultimaActividad = ultimaActividad;
        this.passwordHash = passwordHash;
    }

    public static Usuario fromDB(
        String nombre, 
        String tipoStr, 
        String estadoStr, 
        String codigoSeguridad, 
        LocalDate fechaCreacion, 
        LocalDate ultimaActividad, 
        String passwordHash) {
            TipoDeUsuario tipo = parseEnum(TipoDeUsuario.class, tipoStr, TipoDeUsuario.NORMAL);
            EstadoUsuario estado = parseEnum(EstadoUsuario.class, estadoStr, EstadoUsuario.ACTIVO);
            return new Usuario(
                nombre,
                tipo,
            estado, 
            codigoSeguridad, 
            fechaCreacion != null ? fechaCreacion : LocalDate.now(), 
            ultimaActividad != null ? ultimaActividad : LocalDate.now(),
            passwordHash
        );
    }

    public static Usuario crearUsuario(String nombre, TipoDeUsuario tipo, String password) {
        String codigo = RandomStringUtils.random(tipo.getLongitudDeCodigo(), true, true).toUpperCase();
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        return new Usuario(nombre, tipo, EstadoUsuario.ACTIVO, codigo, LocalDate.now(), LocalDate.now(), hash);
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
        return String.format("Usuario{nombre='%s', codigoSeguridad='%s', tipoDeUsuario=%s, estadoUsuario=%s, fechaDeCreacion=%s, ultimaActividad=%s}", 
            nombre, codigoSeguridad, tipoDeUsuario, estadoUsuario, fechaDeCreacion, ultimaActividad);
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

    public boolean verificarPassword(String password) {
        return BCrypt.checkpw(password, this.passwordHash);
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
