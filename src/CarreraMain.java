public class CarreraMain {
    public static void main(String[] args) {
        System.out.println("=== INICIO DE LA CARRERA POR EQUIPOS ===\n");

        // 1. Objeto compartido que contendrá el estado
        ContadorPasos contadorGlobal = new ContadorPasos();

        // 2. Tarea común que ejecutarán los hilos
        Corredor tareaCorredor = new Corredor(contadorGlobal);

        // 3. Creación de los hilos con nombres personalizadas
        Thread h1 = new Thread(tareaCorredor, "Tortuga");
        Thread h2 = new Thread(tareaCorredor, "Liebre");
        Thread h3 = new Thread(tareaCorredor, "Guepardo");

        // 4. Arrancamos los hilos (se ejecutan de forma asíncrona)
        h1.start();
        h2.start();
        h3.start();

        try {
            // 5. El hilo principal 'main' espera a que finalicen los 3 hilos
            h1.join();
            h2.join();
            h3.join();
        } catch (InterruptedException e) {
            System.err.println("Error en la espera de hilos: " + e.getMessage());
        }

        // 6. Resultado final de la carrera
        System.out.println("\n===========================================");
        System.out.println("CARRERA FINALIZADA");
        System.out.println("Pasos registrados en total: " + contadorGlobal.getTotalPasos());
        System.out.println("===========================================");
    }
}