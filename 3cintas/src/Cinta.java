class Cinta implements Runnable {
    private final ContadorStock contadorCompartido;
    private int cajasContadasPorEstaCinta = 0; // Variable propia del hilo (sin sincronización necesaria)

    public Cinta(ContadorStock contadorCompartido) {
        this.contadorCompartido = contadorCompartido;
    }

    @Override
    public void run() {
        String nombreHilo = Thread.currentThread().getName();

        // 1. Generamos un número aleatorio entre 100 y 300 cajas para esta cinta
        int totalAProcesar = (int) (Math.random() * 201) + 100;

        // 2. Procesa sus cajas localmente (rápido y sin bloquear a otros hilos)
        for (int i = 1; i <= totalAProcesar; i++) {
            cajasContadasPorEstaCinta++;
            try {
                // Simula el tiempo que tarda en pasar cada caja
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.err.println("El hilo fue interrumpido: " + e.getMessage());
            }
        }

        // 3. Entra AL CANDADO UNA SOLA VEZ al final para actualizar el stock global
        contadorCompartido.sumarCajas(cajasContadasPorEstaCinta);

        System.out.println(nombreHilo + ": " + cajasContadasPorEstaCinta + " entradas");
    }

    // Getter para que el main pueda verificar las cajas individuales de esta cinta
    public int getCajasContadasPorEstaCinta() {
        return cajasContadasPorEstaCinta;
    }
}