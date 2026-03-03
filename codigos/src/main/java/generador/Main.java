package generador;

import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * experimento de java doc
 * @author Loli
 * @version 1.0
 * @since 2026-01-29
 */

public class Main {
    private static UsuarioServicio servicio = new UsuarioServicio();
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

    public static void crearUsuario(Map<String, Usuario> lista, Scanner sc) {
        try {
            System.out.println("Dime que tipo de usuario quieres: NORMAL:1   PREMIUM:2   ADMIN:3");
            String inp = sc.nextLine();
            int opci = Integer.parseInt(inp);
            System.out.println("Ingrese su nombre");
            String nombre = sc.nextLine();
            System.out.println("Ingrese su contraseña");
            String password = sc.nextLine();
            servicio.crearUsuario(lista, nombre, opci, password);
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
        System.out.println("Para desbloquear un usuario, primero debe iniciar sesión como ADMIN.");
        System.out.println("Ingrese su código de seguridad:");
        String adminCodigo = sc.nextLine().trim();
        System.out.println("Ingrese su contraseña:");
        String adminPass = sc.nextLine().trim();
        servicio.desbloquearUsuario(lista, adminCodigo, adminPass, null);
    }

    private static void mostrarMenu(Contexto ctx) {
        System.out.println("Seleccione acción:");
        for (OpcionMenu op : OpcionMenu.values()) {
            System.out.println(op.getCodigo() + ". " + op.getDescripcion());
        }
    }
}