package generador;

import java.util.Comparator;
import java.util.Map;

public class UsuarioServicio {

    private Map<String, Usuario> cargarUsuarios() {
        return UsuarioDAO.cargarTodos();
    }

    public void crearUsuario(String nombre, int tipo, String password) {
        Map<String, Usuario> lista = cargarUsuarios();
        Usuario usuario = new Usuario(nombre, tipo, password);

        if (lista.containsKey(usuario.getCodigoSeguridad())) {
            throw new IllegalArgumentException("Ese usuario ya existe");
        }

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

    public void desbloquearUsuario(String adminCodigo, String adminPass, String codigoUsuario) {
        Map<String, Usuario> lista = cargarUsuarios();
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

    public static void mostrarOrdenadosPorActividad() {
        Map<String, Usuario> lista = UsuarioDAO.cargarTodos();
        lista.values().stream()
                .sorted(Comparator.comparing(
                        Usuario::getUltimaActividad,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .forEach(System.out::println);
    }

    public static void mostrarUsuarios() {
        Map<String, Usuario> lista = UsuarioDAO.cargarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios creados aún");
        } else {
            lista.values().forEach(System.out::println);
        }
    }

    public void buscarUsuarioPorCodigoServicio(String codigo) {
        Map<String, Usuario> lista = UsuarioDAO.cargarTodos();
        Usuario encontrado = lista.get(codigo);
        if (encontrado != null) {
            encontrado.actualizarActividad();
            UsuarioDAO.actualizarEstadoYActividad(encontrado.getCodigoSeguridad(), encontrado.getEstadoUsuario(), encontrado.getUltimaActividad());
            AuditoriaDAO.registrar(encontrado.getCodigoSeguridad(), "Usuario actualizo su actividad");
            System.out.println("Usuario encontrado: " + encontrado.toString());
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
    }

    public static void verificarInactividadServicio() {
        Map<String, Usuario> lista = UsuarioDAO.cargarTodos();
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
}
