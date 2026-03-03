package generador;

import java.util.Map;
import java.util.Scanner;

public class Contexto {

    private final Map<String, Usuario> lista;
    private final Scanner scanner;
    private boolean salir;

    public Contexto(Map<String, Usuario> lista, Scanner scanner) {
        this.lista = lista;
        this.scanner = scanner;
    }

    public Map<String, Usuario> lista() {
        return lista;
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
