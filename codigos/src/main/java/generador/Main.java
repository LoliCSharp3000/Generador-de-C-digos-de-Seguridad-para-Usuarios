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
        UsuarioServicio servicio = new UsuarioServicio();
        Controlador controlador = new Controlador(servicio);
        try(Scanner sc = new Scanner(System.in)){
            Contexto ctx = new Contexto(sc, controlador);
            while (!ctx.debeSalir()) {
                controlador.mostrarMenu();
                try {
                    int input = Integer.parseInt(sc.nextLine());
                    OpcionMenu opcion = OpcionMenu.fromCodigo(input);
                    if (opcion == null) {
                        System.out.println("Error: opción no encontrada. Por favor seleccione una opción válida.");
                    } else {
                        opcion.ejecutar(ctx);
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
