package generador;

import java.util.Scanner;

public class Controlador {
    private final UsuarioServicio servicio;

    public Controlador(UsuarioServicio servicio) {
        this.servicio = servicio;
    }

    public void crearUsuario(Scanner sc) {
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

    public void mostrarUsuarios() {
        servicio.mostrarUsuarios();
    }

    public void buscarUsuarioPorCodigo(Scanner sc) {
        System.out.println("Ingrese el código de seguridad del usuario:");
        String codigo = sc.nextLine().trim();
        servicio.buscarUsuarioPorCodigoServicio(codigo);
    }

    public void desbloquearUsuario(Scanner sc) {
        System.out.println("Para desbloquear un usuario, primero debe iniciar sesión como ADMIN.");
        System.out.println("Ingrese su código de seguridad:");
        String adminCodigo = sc.nextLine().trim();
        System.out.println("Ingrese su contraseña:");
        String adminPass = sc.nextLine().trim();
        System.out.println("Ingrese el código de seguridad del usuario a desbloquear:");
        String codigoUsuario = sc.nextLine().trim();
        servicio.desbloquearUsuario(adminCodigo, adminPass, codigoUsuario);
    }

    public void verificarInactividad() {
        servicio.verificarInactividad();
    }

    public void mostrarPorActividad() {
        servicio.mostrarOrdenadosPorActividad();
    }

    public void mostrarMenu() {
        System.out.println("Seleccione acción:");
        for (OpcionMenu op : OpcionMenu.values()) {
            System.out.println(op.getCodigo() + ". " + op.getDescripcion());
        }
    }
}
