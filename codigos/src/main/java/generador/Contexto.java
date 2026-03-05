package generador;

import java.util.Scanner;

public class Contexto {

    private final Scanner scanner;
    private boolean salir;

    public Contexto(Scanner scanner) {
        this.scanner = scanner;
    }

    public Scanner scanner() {
        return scanner;
    }

    public void salir() {
        this.salir = true;
    }

    public boolean debeSalir() {
        return salir;
    }
}
