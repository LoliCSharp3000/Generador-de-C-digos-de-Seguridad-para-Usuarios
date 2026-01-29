package generador;

import java.sql.SQLException;

public class AuditoriaDAO {
    public static void registrar(String codigoUsuario, String accion) {
        String sql = """
            INSERT INTO auditoria (codigo_usuario, accion)
            VALUES (?, ?)
                """;
        try (var c = Database.conectar();
             var ps = c.prepareStatement(sql)) {

            ps.setString(1, codigoUsuario);
            ps.setString(2, accion);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar auditoría: " + e.getMessage());
        }
    }
}
