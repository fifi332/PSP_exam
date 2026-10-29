class Corredor implements Runnable {
    private ContadorPasos contadorCompartido;

    public Corredor(ContadorPasos contadorCompartido) {
        this.contadorCompartido = contadorCompartido;
    }

    @Override
    public void run() {
        // Obtenemos el nombre asignado al hilo actual
        String nombreHilo = Thread.currentThread().getName();

        for (int i = 1; i <= 5; i++) {
            // Cada corredor registra su paso en el contador compartido
            contadorCompartido.incrementar(nombreHilo);

            try {
                // Pausa aleatoria para simular la velocidad del corredor
                Thread.sleep((long) (Math.random() * 300 + 100));
            } catch (InterruptedException e) {
                System.err.println("El hilo fue interrumpido: " + e.getMessage());
            }
        }
        System.out.println("--> ¡" + nombreHilo + " HA LLEGADO A LA META!");
    }
}