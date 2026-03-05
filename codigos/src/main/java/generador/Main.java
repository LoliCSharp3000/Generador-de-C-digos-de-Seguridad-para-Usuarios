package generador;

import java.util.Scanner;

/**
 * experimento de java doc
 * @author Loli
 * @version 1.0
 * @since 2026-01-29
 */

public class Main {
    private static UsuarioServicio servicio = new UsuarioServicio();
    public static void main(String[] args) {
        try(Scanner sc = new Scanner(System.in)){
            Contexto ctx = new Contexto(sc);
            while (!ctx.debeSalir()) {
                mostrarMenu(ctx);
                try {
                    int input = Integer.parseInt(sc.nextLine());
                    OpcionMenu opcion = OpcionMenu.fromCodigo(input);
                    if (opcion != null) {
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

    public static void crearUsuario(Scanner sc) {
        try {
            System.out.println("Dime que tipo de usuario quieres: NORMAL:1   PREMIUM:2   ADMIN:3");
            String inp = sc.nextLine();
            int opci = Integer.parseInt(inp);
            System.out.println("Ingrese su nombre");
            String nombre = sc.nextLine();
            System.out.println("Ingrese su contraseña");
            String password = sc.nextLine();
            servicio.crearUsuario(nombre, opci, password);
        } catch (IllegalArgumentException e) {
            System.out.println("Error al crear usuario: " + e.getMessage());
        }
    }

    public static void buscarUsuarioPorCodigo(Scanner sc) {
        System.out.println("Ingrese el código de seguridad del usuario:");
        String codigo = sc.nextLine().trim();
        servicio.buscarUsuarioPorCodigoServicio(codigo);
    }

    public static void desbloquearUsuario(Scanner sc) {
        System.out.println("Para desbloquear un usuario, primero debe iniciar sesión como ADMIN.");
        System.out.println("Ingrese su código de seguridad:");
        String adminCodigo = sc.nextLine().trim();
        System.out.println("Ingrese su contraseña:");
        String adminPass = sc.nextLine().trim();
        System.out.println("Ingrese el código de seguridad del usuario a desbloquear:");
        String codigoUsuario = sc.nextLine().trim();
        servicio.desbloquearUsuario(adminCodigo, adminPass, codigoUsuario);
    }

    private static void mostrarMenu(Contexto ctx) {
        System.out.println("Seleccione acción:");
        for (OpcionMenu op : OpcionMenu.values()) {
            System.out.println(op.getCodigo() + ". " + op.getDescripcion());
        }
    }
}