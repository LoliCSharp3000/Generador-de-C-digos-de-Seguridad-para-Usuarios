package generador;

import java.util.Scanner;

public class Contexto {
    private final Scanner scanner;
    private final Controlador controlador;
    private boolean salir;

    public Contexto(Scanner scanner, Controlador controlador) {
        this.scanner = scanner;
        this.controlador = controlador;
        this.salir = false;
    }

    public Scanner scanner() {
        return scanner;
    }

    public Controlador getControlador() {
        return controlador;
    }

    public void salir() {
        this.salir = true;
    }

    public boolean debeSalir() {
        return salir;
    }
}
