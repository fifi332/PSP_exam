// Clase que define la tarea que ejecutará cada hilo (implementa Runnable)
class Fase implements Runnable {
    private String nombre;

    // Constructor para asignar el nombre de la fase a ejecutar
    public Fase(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        // Ejecutamos los 4 pasos indicados en el enunciado
        for (int i = 1; i <= 4; i++) {
            System.out.println(nombre + " paso " + i);
            try {
                // Simula el tiempo de procesamiento variable entre 100ms y 300ms
                Thread.sleep((long) (Math.random() * 200 + 100));
            } catch (InterruptedException e) {
                System.err.println("El hilo " + nombre + " fue interrumpido: " + e.getMessage());
            }
        }
    }
}