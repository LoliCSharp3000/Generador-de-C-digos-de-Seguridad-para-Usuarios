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
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String FILE_PATH = "usuarios.json";
    private static Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .setPrettyPrinting()
        .create();


    public static void main(String[] args) {
        Map<String, Usuario> lista = cargarUsuarios();
        boolean fun = true;
        try(Scanner sc = new Scanner(System.in)){
            while (fun) {
                try {
                    System.out.println("Seleccione acción: 1. Crear usuario  2. Ver usuarios  3. Salir    4.Encontrar usuario por código de seguridad");
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
                                guardarUsuario(lista);
                                System.out.println("Clave de Seguridad del usuario: " + usuario.getCodigoSeguridad() + " Total usuarios: " + Usuario.getTotalUsuarios());
                            }else{
                                System.out.println("Ese usuario ya existe");
                            }
                            break;
                        case 2:
                            if(lista.isEmpty()){
                                System.out.println("No hay usuarios creados aún");
                            } else {
                                for (Usuario u : lista.values()) {
                                    System.out.println(u.mostrarInfo());
                                }
                                System.out.println("Total usuarios: " + Usuario.getTotalUsuarios());
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
                                System.out.println("Usuario encontrado: " + encontrado.mostrarInfo());
                            } else {
                                System.out.println("No se encontró ningún usuario con ese código de seguridad.");
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
                }
            }
        }
    }

    private static void guardarUsuario(Map<String, Usuario> lista){
        try(FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    private static Map<String, Usuario> cargarUsuarios(){
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) return new HashMap<>();

        try(FileReader reader = new FileReader(file)) {
            Map<String, Usuario> usuarios = gson.fromJson(reader, new TypeToken<HashMap<String, Usuario>>(){}.getType());
            if (usuarios == null) usuarios = new HashMap<>();
            Usuario.setTotalUsuarios(usuarios.size());
            return usuarios;
        } catch (IOException e) {
            System.out.println("No se pudo cargar usuarios: " + e.getMessage()); 
        } catch(com.google.gson.JsonSyntaxException | com.google.gson.JsonIOException e){
            System.out.println("JSON inválido o vacío, creando lista vacía...");
        }

        return new HashMap<>();
    }
}