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

public class Main { // EIV9DASMIZKL
    public static void main(String[] args) {
        Map<String, Usuario> lista = UsuarioDAO.cargarTodos();
        boolean fun = true;
        try(Scanner sc = new Scanner(System.in)){
            while (fun) {
                try {
                    System.out.println("""
                        Seleccione acción: 
                        1. Crear usuario  
                        2. Ver usuarios  
                        3. Salir    
                        4. Encontrar usuario por código de seguridad  
                        5. Verificar inactividad
                        6. Desbloquear usuario (admin)
                        7. mostrar usuarios por ultima actividad
                        """);
                    String input = sc.nextLine();
                    int opc = Integer.parseInt(input);
                    switch (opc) {
                        case 1:
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
                                }else{
                                    System.out.println("Ese usuario ya existe");
                                }
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error al crear usuario: " + e.getMessage());
                                break;
                            }
                            
                            break;
                        case 2:
                            Usuario us = login(lista, sc);
                            if (us == null) {
                                System.out.println("No se pudo iniciar sesión.");
                            }
                            lista = UsuarioDAO.cargarTodos();
                            if(lista.isEmpty()){
                                System.out.println("No hay usuarios creados aún");
                            } else {
                                lista.values().forEach(System.out::println);
                            }
                            break;
                        case 3:
                            fun = false;
                            break;
                        case 4:
                            Usuario usu = login(lista, sc);
                            if (usu == null) {
                                System.out.println("No se pudo iniciar sesión.");
                                break;
                            }
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
                            break;
                        case 5:
                            System.out.println("Verificando inactividad...");
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
                            break;
                        case 6:
                            Usuario usua = login(lista, sc);
                            if (usua == null) {
                                System.out.println("No se pudo iniciar sesión.");
                                break;
                            }
                            if(usua.getTipoDeUsuario() != Usuario.TipoDeUsuario.ADMIN){
                                System.out.println("Solo un usuario ADMIN puede desbloquear usuarios.");
                                break;
                            }
                            System.out.println("Ingrese el código de seguridad del usuario a desbloquear:");
                            String codigoDesbloqueo = sc.nextLine().trim();
                            Usuario usuarioABloquear = lista.get(codigoDesbloqueo);
                            if (usuarioABloquear == null) {
                                System.out.println("No se encontró ningún usuario con ese código de seguridad.");
                                break;
                            } 
                            try {
                                usuarioABloquear.desbloquearPorAdmin(usua.getTipoDeUsuario());
                                UsuarioDAO.desbloquearUsuario(codigoDesbloqueo);
                                AuditoriaDAO.registrar(usuarioABloquear.getCodigoSeguridad(), "Usuario desbloqueado por administrador");
                                System.out.println("Usuario desbloqueado exitosamente.");
                            } catch (Exception e) {
                                System.out.println("Error al desbloquear usuario: " + e.getMessage());
                            }
                            break;
                        case 7:
                            System.out.println("Mostrando usuarios por última actividad...");
                            lista.values().stream()
                                .sorted(Comparator.comparing(Usuario::getUltimaActividad).reversed())
                                .forEach(System.out::println);
                            break;
                        default:
                            System.out.println("Porfavor pon el numero correcto");
                            break;
                    }
                } catch(NumberFormatException e){
                    System.out.println("Error: " + e.getMessage());
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
}