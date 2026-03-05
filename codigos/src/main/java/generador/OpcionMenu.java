package generador;

public enum OpcionMenu {

    CREAR_USUARIO(1, "Crear usuario") {
        @Override
        public void ejecutar(Contexto ctx) {
            Controlador.crearUsuario(ctx.scanner());
        }
    },

    VER_USUARIOS(2, "Ver usuarios") {
        @Override
        public void ejecutar(Contexto ctx) {
            UsuarioServicio.mostrarUsuarios();
        }
    },

    SALIR(3, "Salir") {
        @Override
        public void ejecutar(Contexto ctx) {
            ctx.salir();
        }
    },

    BUSCAR_USUARIO(4, "Buscar usuario por código") {
        @Override
        public void ejecutar(Contexto ctx) {
            Controlador.buscarUsuarioPorCodigo(ctx.scanner());
        }
    },

    VERIFICAR_INACTIVIDAD(5, "Verificar inactividad") {
        @Override
        public void ejecutar(Contexto ctx) {
            UsuarioServicio.verificarInactividadServicio();
        }
    },

    DESBLOQUEAR(6, "Desbloquear usuario (admin)") {
        @Override
        public void ejecutar(Contexto ctx) {
            Controlador.desbloquearUsuario(ctx.scanner());
        }
    },

    MOSTRAR_POR_ACTIVIDAD(7, "Mostrar usuarios por última actividad") {
        @Override
        public void ejecutar(Contexto ctx) {
            UsuarioServicio.mostrarOrdenadosPorActividad();
        }
    };

    private final int codigo;
    private final String descripcion;

    OpcionMenu(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public abstract void ejecutar(Contexto ctx);

    public static OpcionMenu fromCodigo(int codigo) {
        for (OpcionMenu op : values()) {
            if (op.codigo == codigo) return op;
        }
        return null;
    }
}
