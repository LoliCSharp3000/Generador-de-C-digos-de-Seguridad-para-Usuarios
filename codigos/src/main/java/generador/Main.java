package generador;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Usuario> lista = new ArrayList<>();
        boolean fun = true;
        
        while (fun) {
            try {
                System.out.println("Quiere hacer: 1.crear usuario  2.ver mis datos  3.Salir");
                int opc = sc.nextInt();
                sc.nextLine();
                switch (opc) {
                    case 1:
                        System.out.println("Ingrese su nombre");
                        Usuario usuario = new Usuario(sc.nextLine());
                        lista.add(usuario);
                        break;
                    
                    case 2:
                        if(lista.isEmpty()){
                            System.out.println("No hay usuarios creados aún");
                        } else {
                            for (Usuario u : lista) {
                                System.out.println(u.mostrarInfo());
                            }
                        }
                        break;


                    case 3:
                        fun = false;
                        break;
                    default:
                        System.out.println("Porfavor pon el numero correcto");
                        break;
                }
            } catch(IllegalArgumentException e){
                System.out.println(e);
            } catch(Exception e){
                System.out.println(e);
            }
        }
        sc.close();
    }
}