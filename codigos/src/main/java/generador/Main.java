package generador;

import java.util.Scanner;

/**
 * experimento de java doc
 * @author Loli
 * @version 1.0
 * @since 2026-01-29
 */

public class Main {
    
    public static void main(String[] args) {
        try(Scanner sc = new Scanner(System.in)){
            Contexto ctx = new Contexto(sc);
            while (!ctx.debeSalir()) {
                Controlador.mostrarMenu(ctx);
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
}