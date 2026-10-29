class ContadorStock {
    private int total = 0;

    // Sincronizamos solo la suma final acumulada para máxima eficiencia
    public synchronized void sumarCajas(int cantidad) {
        this.total += cantidad;
    }

    public synchronized int getTotalCajas() {
        return total;
    }
}