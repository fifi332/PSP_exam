public class Main {
    public static void main(String[] args) {
        System.out.println("Inicio de las cintas...\n");

        ContadorStock contadorGlobal = new ContadorStock();

        // Creamos una instancia de Cinta (Runnable) por cada hilo
        Cinta cinta1 = new Cinta(contadorGlobal);
        Cinta cinta2 = new Cinta(contadorGlobal);
        Cinta cinta3 = new Cinta(contadorGlobal);

        Thread h1 = new Thread(cinta1, "Torno-1");
        Thread h2 = new Thread(cinta2, "Torno-2");
        Thread h3 = new Thread(cinta3, "Torno-3");

        // 1. Arrancamos TODOS los hilos primero para que ejecuten en paralelo
        h1.start();
        h2.start();
        h3.start();

        // 2. Esperamos a que TODOS terminen antes de pedir resultados
        try {
            h1.join();
            h2.join();
            h3.join();
        } catch (InterruptedException e) {
            System.err.println("Error en la espera de hilos: " + e.getMessage());
        }

        // 3. Comprobamos e imprimimos resultados
        int sumaIndividuales = cinta1.getCajasContadasPorEstaCinta() +
                cinta2.getCajasContadasPorEstaCinta() +
                cinta3.getCajasContadasPorEstaCinta();

        System.out.println("\nMarcador del almacén: " + contadorGlobal.getTotalCajas());
        System.out.println("Suma de las cintas: " + sumaIndividuales);

        if (contadorGlobal.getTotalCajas() == sumaIndividuales) {
            System.out.println("¡Coinciden correctamente!");
        } else {
            System.out.println("¡Error de sincronización!");
        }
    }
}