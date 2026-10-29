
class ContadorPasos {
    private int totalPasos = 0;

    // 'synchronized' asegura que solo UN hilo pueda ejecutar este método a la vez
    public synchronized void incrementar(String corredor) {
        totalPasos++;
        System.out.println(corredor + " sumó un paso. Total acumulado: " + totalPasos);
    }

    public synchronized int getTotalPasos() {
        return totalPasos;
    }
}
