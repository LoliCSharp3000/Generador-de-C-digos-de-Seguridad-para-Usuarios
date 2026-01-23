package generador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UsuarioDAO {
    public static void insertar(Usuario u){
        String sql = """
            INSERT INTO usuarios (codigo_seguridad, nombre_usuario, tipo, estado, fecha_creacion, ultima_actividad)
            VALUES (?, ?, ?, ?, ?, ?)
                """;
        try(Connection c = Database.conectar();
            PreparedStatement ps = c.prepareStatement(sql)){ 
            
            ps.setString(1, u.getCodigoSeguridad());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getTipoDeUsuario().toString());
            ps.setString(4, u.getEstadoUsuario().toString());
            ps.setObject(5, u.getFechaDeCreacion());
            ps.setObject(6, u.getUltimaActividad());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar usuario: " + e.getMessage());
        }
    }

    public static Map<String, Usuario> cargarTodos() {
        String sql = "SELECT * FROM usuarios WHERE estado IN ('ACTIVO','INACTIVO','BLOQUEADO')";
        Map<String, Usuario> usuarios = new HashMap<>();
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = Usuario.fromDB(
                    rs.getString("nombre_usuario"),
                    rs.getString("tipo"),
                    rs.getString("estado"),
                    rs.getString("codigo_seguridad"),
                    rs.getObject("fecha_creacion", LocalDate.class),
                    rs.getObject("ultima_actividad", LocalDate.class)
                );
                usuarios.put(u.getCodigoSeguridad(), u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar usuarios: " + e.getMessage());
        }
        Usuario.setTotalUsuarios(usuarios.size());
        return usuarios;
    }

    public static void updateActividad(String codigoSeguridad) {
        String sql = "UPDATE usuarios SET ultima_actividad = ?, estado = ? WHERE codigo_seguridad = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, LocalDate.now());
            ps.setString(2, Usuario.EstadoUsuario.ACTIVO.toString());
            ps.setString(3, codigoSeguridad);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar actividad: " + e.getMessage());
        }
    }

    public static void updateEstado(String codigoSeguridad, Usuario.EstadoUsuario estado) {
        String sql = "UPDATE usuarios SET estado = ? WHERE codigo_seguridad = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, estado.toString());
            ps.setString(2, codigoSeguridad);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar estado: " + e.getMessage());
        }
    }
}
