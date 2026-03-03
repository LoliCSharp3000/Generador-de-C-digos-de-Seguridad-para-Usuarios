package generador;

import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * experimento de java doc
 * @author Loli
 * @version 1.0
 * @since 2026-01-29
 */

public class Main {
    public static void main(String[] args) {
        Map<String, Usuario> lista = UsuarioDAO.cargarTodos();
        try(Scanner sc = new Scanner(System.in)){
            Contexto ctx = new Contexto(lista, sc);
            while (!ctx.debeSalir()) {
                mostrarMenu(ctx);
                try {
                    int input = Integer.parseInt(sc.nextLine());
                    OpcionMenu opcion = OpcionMenu.fromCodigo(input);
                    if (opcion != null) {
                        lista.clear();
                        lista.putAll(UsuarioDAO.cargarTodos());
                        opcion.ejecutar(ctx);
                    } else {
                        System.out.println("Opción no válida. Intente nuevamente.");
                        
                    }
                } catch(NumberFormatException e){
                    System.out.println("Error: entrada no válida. Por favor ingrese un número.");
                } catch(IllegalArgumentException e){
                    System.out.println("Error: " + e.getMessage());
                } catch(Exception e){
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static Usuario login(Map<String, Usuario> lista, Scanner sc) {
        System.out.println("Ingrese su código de seguridad:");
        String codigoSeguridad = sc.nextLine().trim();
        Usuario u = lista.get(codigoSeguridad);
        if (u == null) {
            System.out.println("No se encontró ningún usuario con ese código de seguridad.");
            return null;
        }
        System.out.println("Ingrese la contraseña:");
        String password = sc.nextLine();
        Predicate<String> verificarPassword = pass -> u.verificarPassword(pass);
        if (!verificarPassword.test(password)) {
            System.out.println("Contraseña incorrecta.");
            return null;
        }
        if (u.getEstadoUsuario() == Usuario.EstadoUsuario.BLOQUEADO) {
            System.out.println("El usuario está bloqueado.");
            return null;
        }
        u.actualizarActividad();
        UsuarioDAO.actualizarEstadoYActividad(u.getCodigoSeguridad(), u.getEstadoUsuario(), u.getUltimaActividad());
        AuditoriaDAO.registrar(u.getCodigoSeguridad(), "Usuario inició sesión");
        return u;
    }

    public static void crearUsuario(Map<String, Usuario> lista, Scanner sc) {
        try {
            System.out.println("Dime que tipo de usuario quieres: NORMAL:1   PREMIUM:2   ADMIN:3");
            String inp = sc.nextLine();
            int opci = Integer.parseInt(inp);
            System.out.println("Ingrese su nombre");
            String nombre = sc.nextLine();
            System.out.println("Ingrese su contraseña");
            String password = sc.nextLine();
            Usuario usuario = new Usuario(nombre, opci, password);
            if (!lista.containsKey(usuario.getCodigoSeguridad())) {
                lista.put(usuario.getCodigoSeguridad(), usuario);
                UsuarioDAO.insertar(usuario);
                System.out.println("Clave de Seguridad del usuario: " + usuario.getCodigoSeguridad());
            } else {
                System.out.println("Ese usuario ya existe");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error al crear usuario: " + e.getMessage());
        }
    }

    public static void mostrarUsuarios(Map<String, Usuario> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios creados aún");
        } else {
            lista.values().forEach(System.out::println);
        }
    }

    public static void buscarUsuarioPorCodigo(Map<String, Usuario> lista, Scanner sc) {
        System.out.println("Ingrese el código de seguridad del usuario:");
        String codigo = sc.nextLine().trim();
        Usuario encontrado = lista.get(codigo);
        if (encontrado != null) {
            encontrado.actualizarActividad();
            UsuarioDAO.actualizarEstadoYActividad(encontrado.getCodigoSeguridad(), encontrado.getEstadoUsuario(), encontrado.getUltimaActividad());
            AuditoriaDAO.registrar(encontrado.getCodigoSeguridad(), "Usuario actualizo su actividad");
            System.out.println("Usuario encontrado: " + encontrado.toString());
        } else {
            System.out.println("No se encontró ningún usuario con ese código de seguridad.");
        }
    }

    public static void verificarInactividad(Map<String, Usuario> lista) {
        Consumer<Usuario> verificarUsuario = u ->{
            Usuario.EstadoUsuario estadoAnterior = u.getEstadoUsuario();
            u.actualizarEstadoPorInactividad();
            if (estadoAnterior != u.getEstadoUsuario()) {
                UsuarioDAO.actualizarEstadoYActividad(u.getCodigoSeguridad(), u.getEstadoUsuario(), u.getUltimaActividad());
                if (u.getEstadoUsuario() == Usuario.EstadoUsuario.BLOQUEADO) {
                    AuditoriaDAO.registrar(u.getCodigoSeguridad(), "Usuario bloqueado por inactividad");
                }
            }
        };
        lista.values().forEach(verificarUsuario);
    }

    public static void desbloquearUsuario(Map<String, Usuario> lista, Scanner sc) {

        Usuario admin = login(lista, sc);
        if (admin == null) return;

        if (admin.getTipoDeUsuario() != Usuario.TipoDeUsuario.ADMIN) {
            System.out.println("Solo un ADMIN puede desbloquear usuarios.");
            return;
        }

        System.out.println("Ingrese el código de seguridad del usuario a desbloquear:");
        String codigo = sc.nextLine().trim();

        Usuario usuario = lista.get(codigo);
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }

        usuario.desbloquearPorAdmin(admin.getTipoDeUsuario());
        UsuarioDAO.desbloquearUsuario(codigo);
        AuditoriaDAO.registrar(usuario.getCodigoSeguridad(),
            "Usuario desbloqueado por administrador");

        System.out.println("Usuario desbloqueado exitosamente.");
    }


    public static void mostrarUsuariosPorUltimaActividad(Map<String, Usuario> lista) {
        System.out.println("Mostrando usuarios por última actividad...");
        lista.values().stream()
            .sorted(Comparator.comparing(Usuario::getUltimaActividad, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
            .forEach(System.out::println);
    }

    private static void mostrarMenu(Contexto ctx) {
        System.out.println("Seleccione acción:");
        for (OpcionMenu op : OpcionMenu.values()) {
            System.out.println(op.getCodigo() + ". " + op.getDescripcion());
        }
    }
}