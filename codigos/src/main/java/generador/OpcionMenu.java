package generador;

public enum OpcionMenu {
    
    CREAR_USUARIO(1, "Crear usuario", null),
    VER_USUARIOS(2, "Ver usuarios", null),
    SALIR(3, "Salir", null),
    BUSCAR_USUARIO(4, "Buscar usuario por código", null),
    VERIFICAR_INACTIVIDAD(5, "Verificar inactividad", null),
    DESBLOQUEAR(6, "Desbloquear usuario (admin)", null),
    MOSTRAR_POR_ACTIVIDAD(7, "Mostrar usuarios por última actividad", null);

    private final int codigo;
    private final String descripcion;
    private Controlador controlador;

    OpcionMenu(int codigo, String descripcion, Controlador controlador) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.controlador = controlador;
    }

    public void setControlador(Controlador controlador) {
        // Este método se usará para inyectar el controlador después de su creación
        // debido a la dependencia circular entre Controlador y OpcionMenu
        // (Controlador necesita OpcionMenu para mostrar el menú, y OpcionMenu necesita Controlador para ejecutar acciones)
        this.controlador = controlador;
    }

    public Controlador getControlador() {
        return controlador;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void ejecutar(Contexto ctx){
        switch(this){
            case CREAR_USUARIO -> controlador.crearUsuario(ctx.scanner());
            case VER_USUARIOS -> controlador.mostrarUsuarios();
            case SALIR -> ctx.salir();
            case BUSCAR_USUARIO -> controlador.buscarUsuarioPorCodigo(ctx.scanner());
            case DESBLOQUEAR -> controlador.desbloquearUsuario(ctx.scanner());
            case VERIFICAR_INACTIVIDAD -> controlador.verificarInactividad();
            case MOSTRAR_POR_ACTIVIDAD -> controlador.mostrarPorActividad();
            default -> throw new UnsupportedOperationException("Opción no implementada");
        }
    }

    public static OpcionMenu fromCodigo(int codigo) {
        for (OpcionMenu op : values()) {
            if (op.codigo == codigo) return op;
        }
        return null;
    }
}
