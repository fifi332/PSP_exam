class TareaDescarga implements Runnable {
    private String nombreArchivo;

    public TareaDescarga(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }


    @Override
    public void run() {
        String nombreHilo = Thread.currentThread().getName();
        System.out.println("Iniciando descarga de "+nombreHilo);

        for(int i=1;i<=5;i++){
            int porcentaje = i *20;
            System.out.println("Descarga: "+porcentaje+"%");

            try{
                Thread.sleep(500);
            }   catch (InterruptedException e){
            System.err.println("Hilo interrumpido "+ e.getMessage());
            }
        }
        System.out.println("Descarga completada");
    }

}