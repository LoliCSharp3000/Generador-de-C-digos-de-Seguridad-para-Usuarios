package generador;

import java.util.Comparator;
import java.util.Map;

public class UsuarioServicio {

    public void crearUsuario(Map<String, Usuario> lista, String nombre, int tipo, String password) {

        Usuario usuario = new Usuario(nombre, tipo, password);

        if (lista.containsKey(usuario.getCodigoSeguridad())) {
            throw new IllegalArgumentException("Ese usuario ya existe");
        }

        lista.put(usuario.getCodigoSeguridad(), usuario);
        UsuarioDAO.insertar(usuario);
    }

    public static Usuario login(Map<String, Usuario> lista, String codigo, String password) {

        Usuario u = lista.get(codigo);

        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if (!u.verificarPassword(password)) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        if (u.getEstadoUsuario() == Usuario.EstadoUsuario.BLOQUEADO) {
            throw new IllegalStateException("Usuario bloqueado");
        }

        u.actualizarActividad();
        UsuarioDAO.actualizarEstadoYActividad(
                u.getCodigoSeguridad(),
                u.getEstadoUsuario(),
                u.getUltimaActividad()
        );

        AuditoriaDAO.registrar(u.getCodigoSeguridad(), "Usuario inició sesión");

        return u;
    }

    public static void verificarInactividad(Map<String, Usuario> lista) {

        lista.values().forEach(u -> {
            Usuario.EstadoUsuario estadoAnterior = u.getEstadoUsuario();
            u.actualizarEstadoPorInactividad();

            if (estadoAnterior != u.getEstadoUsuario()) {
                UsuarioDAO.actualizarEstadoYActividad(
                        u.getCodigoSeguridad(),
                        u.getEstadoUsuario(),
                        u.getUltimaActividad()
                );

                if (u.getEstadoUsuario() == Usuario.EstadoUsuario.BLOQUEADO) {
                    AuditoriaDAO.registrar(
                            u.getCodigoSeguridad(),
                            "Usuario bloqueado por inactividad"
                    );
                }
            }
        });
    }

    public void desbloquearUsuario(Map<String, Usuario> lista, String adminCodigo, String adminPass, String codigoUsuario) {

        Usuario admin = login(lista, adminCodigo, adminPass);

        if (admin.getTipoDeUsuario() != Usuario.TipoDeUsuario.ADMIN) {
            throw new IllegalStateException("Solo ADMIN puede desbloquear");
        }

        Usuario usuario = lista.get(codigoUsuario);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        usuario.desbloquearPorAdmin(admin.getTipoDeUsuario());
        UsuarioDAO.desbloquearUsuario(codigoUsuario);

        AuditoriaDAO.registrar(
                usuario.getCodigoSeguridad(),
                "Usuario desbloqueado por administrador"
        );
    }

    public static void mostrarOrdenadosPorActividad(Map<String, Usuario> lista) {
        lista.values().stream()
                .sorted(Comparator.comparing(
                        Usuario::getUltimaActividad,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .forEach(System.out::println);
    }
}
