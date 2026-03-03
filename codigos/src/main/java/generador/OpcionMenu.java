package generador;

public enum OpcionMenu {

    CREAR_USUARIO(1, "Crear usuario") {
        @Override
        public void ejecutar(Contexto ctx) {
            Main.crearUsuario(ctx.lista(), ctx.scanner());
        }
    },

    VER_USUARIOS(2, "Ver usuarios") {
        @Override
        public void ejecutar(Contexto ctx) {
            Main.mostrarUsuarios(ctx.lista());
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
            Main.buscarUsuarioPorCodigo(ctx.lista(), ctx.scanner());
        }
    },

    VERIFICAR_INACTIVIDAD(5, "Verificar inactividad") {
        @Override
        public void ejecutar(Contexto ctx) {
            Main.verificarInactividad(ctx.lista());
        }
    },

    DESBLOQUEAR(6, "Desbloquear usuario (admin)") {
        @Override
        public void ejecutar(Contexto ctx) {
            Main.desbloquearUsuario(ctx.lista(), ctx.scanner());
        }
    },

    MOSTRAR_POR_ACTIVIDAD(7, "Mostrar usuarios por última actividad") {
        @Override
        public void ejecutar(Contexto ctx) {
            Main.mostrarUsuariosPorUltimaActividad(ctx.lista());
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
