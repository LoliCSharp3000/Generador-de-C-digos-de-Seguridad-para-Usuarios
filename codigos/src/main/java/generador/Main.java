package generador;

import java.util.Scanner;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;

public class Main {
    private static final String FILE_PATH = "usuarios.json";
    private static Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .setPrettyPrinting()
        .create();


    public static void main(String[] args) {
        HashSet<Usuario> lista = cargarUsuarios();
        boolean fun = true;
        try(Scanner sc = new Scanner(System.in)){
            while (fun) {
                try {
                    System.out.println("Seleccione acción: 1. Crear usuario  2. Ver usuarios  3. Salir");
                    String input = sc.nextLine();
                    int opc = Integer.parseInt(input);
                    switch (opc) {
                        case 1:
                            System.out.println("Dime un que tipo de usuario quieres: NORMAL:1   PREMIUM:2   ADMIN:3");
                            String inp = sc.nextLine();
                            int opci = Integer.parseInt(inp);
                            System.out.println("Ingrese su nombre");
                            Usuario usuario = new Usuario(sc.nextLine(), opci);
                            if (lista.add(usuario)) {
                                guardarUsuario(lista);
                                System.out.println("Total usuarios: " + Usuario.getTotalUsuarios());
                            }else{
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
        }
    }

    private static void guardarUsuario(HashSet<Usuario> lista){
        try(FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    private static HashSet<Usuario> cargarUsuarios(){
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            try (FileWriter writer = new FileWriter(file)){
                writer.write("[]");
            } catch (IOException e) {
                System.out.println("No se pudo inicializar JSON: " + e.getMessage());
            }
            return new HashSet<>();
        }
        try(FileReader reader = new FileReader(file)) {
            HashSet<Usuario> usuarios = gson.fromJson(reader, new TypeToken<HashSet<Usuario>>(){}.getType());
            if (usuarios == null) usuarios = new HashSet<>();
            Usuario.setTotalUsuarios(usuarios.size());
            return usuarios;
        } catch (IOException e) {
            System.out.println("No se pudo cargar usuarios: " + e.getMessage()); 
        } catch(com.google.gson.JsonSyntaxException | com.google.gson.JsonIOException e){
            System.out.println("JSON inválido o vacío, creando lista vacía...");
        }

        return new HashSet<>();
    }
}