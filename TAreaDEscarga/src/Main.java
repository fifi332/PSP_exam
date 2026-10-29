public class Main {
    static void main(String[] args) {
        System.out.println("Iniciando Descargas");

        TareaDescarga tarea1 = new TareaDescarga("Video.mp4");
        TareaDescarga tarea2 = new TareaDescarga("Musica.mp3");
        TareaDescarga tarea3 = new TareaDescarga("Documento.pdf");

        Thread hilo1 = new Thread(tarea1, "Hilo-Video");
        Thread hilo2 = new Thread(tarea2, "Hilo-Musica");
        Thread hilo3 = new Thread(tarea3, "Hilo-Documento");

        hilo1.start();
        hilo2.start();
        hilo3.start();

        try {
            hilo1.join();
            hilo2.join();
            hilo3.join();
        }catch (InterruptedException e){
            System.err.println("Error esperando hilos "+ e.getMessage());
        }
        System.out.println("\nTodas las descargas terminaron");
    }
}