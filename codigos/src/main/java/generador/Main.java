package generador;

import java.util.Scanner;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashSet<Usuario> lista = new HashSet<>();
        boolean fun = true;
        
        while (fun) {
            try {
                System.out.println("Quiere hacer: 1.crear usuario  2.ver mis datos  3.Salir");
                String input = sc.nextLine();
                int opc = Integer.parseInt(input);
                switch (opc) {
                    case 1:
                        System.out.println("Ingrese su nombre");
                        Usuario usuario = new Usuario(sc.nextLine());
                        lista.add(usuario);
                        if (!lista.add(usuario)) {
                            System.out.println("Ese usuario ya existe");
                        }
                        break;
                    
                    case 2:
                        if(lista.isEmpty()){
                            System.out.println("No hay usuarios creados aún");
                        } else {
                            for (Usuario u : lista) {
                                System.out.println(u.mostrarInfo());
                            }
                            System.out.println("Total usuarios: " + Usuario.getTotalUsuarios());
                        }
                        break;


                    case 3:
                        fun = false;
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
            }
        }
        sc.close();
    }
}