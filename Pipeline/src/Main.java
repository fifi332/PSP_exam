public class Main {
    public static void main(String[] args) {
        // 1. Creación de los objetos Runnable (las tareas a realizar)
        Fase tareaCaptura = new Fase("Captura");
        Fase tareaFiltro = new Fase("Filtro");
        Fase tareaRender = new Fase("Render");

        // 2. Creación de los objetos Thread pasando sus respectivas tareas
        Thread hCaptura = new Thread(tareaCaptura);
        Thread hFiltro = new Thread(tareaFiltro);
        Thread hRender = new Thread(tareaRender);

        // 3. Inicio en paralelo: Arrancamos Captura y Filtro al mismo tiempo.
        // Se ejecutan de forma concurrente disputándose los recursos de la CPU.
        hCaptura.start();
        hFiltro.start();

        // 4. Barrera de sincronización mediante join():
        // El hilo main se bloquea y espera a que AMBOS hilos finalicen.
        try {
            hCaptura.join(); // Espera a que termine la fase de Captura
            hFiltro.join();  // Espera a que termine la fase de Filtro
        } catch (InterruptedException e) {
            System.err.println("Interrupción durante la espera de Captura/Filtro: " + e.getMessage());
        }

        // 5. Una vez terminadas Captura y Filtro, arrancamos Render de forma secuencial
        hRender.start();

        try {
            // Esperamos a que Render termine su ejecución antes de dar por concluido el programa
            hRender.join();
        } catch (InterruptedException e) {
            System.err.println("Interrupción durante la espera de Render: " + e.getMessage());
        }

        // 6. Mensaje de cierre: Solo se imprime tras completar todas las fases anteriores
        System.out.println("Procesamiento finalizado con éxito");
    }
}