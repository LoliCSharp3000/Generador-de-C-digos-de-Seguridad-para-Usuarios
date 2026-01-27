package generador;

import java.util.Scanner;
import java.util.Map;

public class Main {
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
                        4.Encontrar usuario por código de seguridad  
                        5. Verificar inactividad
                        6. Desbloquear usuario (admin)
                        """);
                    String input = sc.nextLine();
                    int opc = Integer.parseInt(input);
                    switch (opc) {
                        case 1:
                            System.out.println("Dime un que tipo de usuario quieres: NORMAL:1   PREMIUM:2   ADMIN:3");
                            String inp = sc.nextLine();
                            int opci = Integer.parseInt(inp);
                            System.out.println("Ingrese su nombre");
                            Usuario usuario = new Usuario(sc.nextLine(), opci);
                            if (!lista.containsKey(usuario.getCodigoSeguridad())) {
                                lista.put(usuario.getCodigoSeguridad(), usuario);
                                UsuarioDAO.insertar(usuario);
                                System.out.println("Clave de Seguridad del usuario: " + usuario.getCodigoSeguridad());
                            }else{
                                System.out.println("Ese usuario ya existe");
                            }
                            break;
                        case 2:
                            lista = UsuarioDAO.cargarTodos();
                            if(lista.isEmpty()){
                                System.out.println("No hay usuarios creados aún");
                            } else {
                                for (Usuario u : lista.values()) {
                                    System.out.println(u.toString());
                                }
                            }
                            break;
                        case 3:
                            fun = false;
                            break;
                        case 4:
                            System.out.println("Ingrese el código de seguridad del usuario:");
                            String codigo = sc.nextLine().trim();
                            Usuario encontrado = lista.get(codigo);
                            if (encontrado != null) {
                                encontrado.actualizarActividad();
                                UsuarioDAO.actualizarEstadoYActividad(encontrado.getCodigoSeguridad(), encontrado.getEstadoUsuario(), encontrado.getUltimaActividad());
                                System.out.println("Usuario encontrado: " + encontrado.toString());
                            } else {
                                System.out.println("No se encontró ningún usuario con ese código de seguridad.");
                            }
                            break;
                        case 5:
                            System.out.println("Verificando inactividad...");
                            for (Usuario u : lista.values()) {
                                Usuario.EstadoUsuario estadoAnterior = u.getEstadoUsuario();
                                u.actualizarEstadoPorInactividad();
                                if (estadoAnterior != u.getEstadoUsuario()) {
                                    UsuarioDAO.actualizarEstadoYActividad(u.getCodigoSeguridad(), u.getEstadoUsuario(), u.getUltimaActividad());
                                }
                            }
                            System.out.println("Verificación completada. Usuarios inactivos marcados.");
                            break;
                        case 6:
                            System.out.println("Ingrese el código de seguridad del usuario a desbloquear:");
                            String codigoDesbloqueo = sc.nextLine().trim();
                            Usuario usuarioABloquear = lista.get(codigoDesbloqueo);
                            if (usuarioABloquear == null) {
                                System.out.println("No se encontró ningún usuario con ese código de seguridad.");
                                break;
                            } 

                            System.out.println("Confirme el tipo del solicitante (debe ser ADMIN): NORMAL:1   PREMIUM:2   ADMIN:3");
                            int tipoSolicitante = Integer.parseInt(sc.nextLine().trim());
                            Usuario.TipoDeUsuario solicitante = Usuario.TipoDeUsuario.values()[tipoSolicitante - 1];
                            try {
                                usuarioABloquear.desbloquearPorAdmin(solicitante);
                                UsuarioDAO.desbloquearUsuario(codigoDesbloqueo);
                                System.out.println("Usuario desbloqueado exitosamente.");
                            } catch (Exception e) {
                                System.out.println("Error al desbloquear usuario: " + e.getMessage());
                            }
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
}