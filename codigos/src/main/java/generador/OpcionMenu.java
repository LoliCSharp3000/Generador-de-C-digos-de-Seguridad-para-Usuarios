package generador;

public enum OpcionMenu {
    
    CREAR_USUARIO(1, "Crear usuario", ctx -> ctx.getControlador().crearUsuario(ctx.scanner())),
    VER_USUARIOS(2, "Ver usuarios", ctx -> ctx.getControlador().mostrarUsuarios()),
    SALIR(3, "Salir", ctx -> ctx.salir()),
    BUSCAR_USUARIO(4, "Buscar usuario por código", ctx -> ctx.getControlador().buscarUsuarioPorCodigo(ctx.scanner())),
    VERIFICAR_INACTIVIDAD(5, "Verificar inactividad", ctx -> ctx.getControlador().verificarInactividad()),
    DESBLOQUEAR(6, "Desbloquear usuario (admin)", ctx -> ctx.getControlador().desbloquearUsuario(ctx.scanner())),
    MOSTRAR_POR_ACTIVIDAD(7, "Mostrar usuarios por última actividad", ctx -> ctx.getControlador().mostrarPorActividad());

    private final int codigo;
    private final String descripcion;
    private final AccionConContexto accion;

    OpcionMenu(int codigo, String descripcion, AccionConContexto accion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.accion = accion;
    }

    public AccionConContexto getAccion() {
        return accion;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void ejecutar(Contexto ctx){
        accion.ejecutar(ctx);
    }

    public static OpcionMenu fromCodigo(int codigo) {
        for (OpcionMenu op : values()) {
            if (op.codigo == codigo) return op;
        }
        return null;
    }
}
