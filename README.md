# 📚 Solucionario y Resumen de Examen: UD2 Programación Multihilo

**Asignatura:** Programación de Servicios y Procesos (PSP)  
**Curso:** 2026-2027  
**Centro:** CPR Daniel Castelao (CFGS DAM2)  
**Tema:** Unidad Didáctica 2 - Programación Multihilo en Java

---

## 📋 Índice

1. [R1. Tornos del Estadio / Cinta Transportadora](#r1-tornos-del-estadio--cinta-transportadora)
2. [R2. Pipeline de Procesamiento / Cadena de Montaje](#r2-pipeline-de-procesamiento--cadena-de-montaje)
3. [R3. Análisis de Secuencia y Salida Concurrente](#r3-análisis-de-secuencia-y-salida-concurrente)
4. [R4. Monitores, `wait()` y `notifyAll()`](#r4-monitores-wait-y-notifyall)
5. [R5. Traza de Estados de los Hilos (`Thread.State`)](#r5-traza-de-estados-de-los-hilos-threadstate)

---

## R1. Tornos del Estadio / Cinta Transportadora

### 📌 Descripción del Ejercicio
Se simula el acceso por varios tornos/cintas a un recito compartido. Cada hilo calcula un número aleatorio de entradas/cajas (entre 100 y 300) y las suma a un contador global protegido frente a condiciones de carrera (*race conditions*). Al finalizar, el proceso principal verifica que la suma de los valores individuales coincide con el total del marcador global.

### 💻 Código Java
```java
class ContadorStock {
    private int total = 0;

    // Se entra al bloque sincronizado para actualizar el total de forma segura
    public synchronized void sumarCajas(int cantidad) {
        this.total += cantidad;
    }

    public synchronized int getTotalCajas() {
        return total;
    }
}

class Cinta implements Runnable {
    private final ContadorStock contadorCompartido;
    private int cajasContadasPorEstaCinta = 0;

    public Cinta(ContadorStock contadorCompartido) {
        this.contadorCompartido = contadorCompartido;
    }

    @Override
    public void run() {
        String nombreHilo = Thread.currentThread().getName();
        // Número aleatorio entre 100 y 300 cajas
        int totalAProcesar = (int) (Math.random() * 201) + 100;

        for (int i = 1; i <= totalAProcesar; i++) {
            cajasContadasPorEstaCinta++;
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.err.println("Hilo interrumpido: " + e.getMessage());
            }
        }

        // Se actualiza el marcador común UNA SOLA VEZ al final para maximizar la eficiencia
        contadorCompartido.sumarCajas(cajasContadasPorEstaCinta);
        System.out.println(nombreHilo + ": " + cajasContadasPorEstaCinta + " entradas");
    }

    public int getCajasContadasPorEstaCinta() {
        return cajasContadasPorEstaCinta;
    }
}

public class MainR1 {
    public static void main(String[] args) {
        System.out.println("Inicio de las cintas...\n");

        ContadorStock contadorGlobal = new ContadorStock();

        Cinta cinta1 = new Cinta(contadorGlobal);
        Cinta cinta2 = new Cinta(contadorGlobal);
        Cinta cinta3 = new Cinta(contadorGlobal);

        Thread h1 = new Thread(cinta1, "Torno-1");
        Thread h2 = new Thread(cinta2, "Torno-2");
        Thread h3 = new Thread(cinta3, "Torno-3");

        // 1. Arrancar todos los hilos
        h1.start();
        h2.start();
        h3.start();

        // 2. Esperar a que terminen todos los hilos
        try {
            h1.join();
            h2.join();
            h3.join();
        } catch (InterruptedException e) {
            System.err.println("Error en la espera de hilos: " + e.getMessage());
        }

        // 3. Comprobación de resultados
        int sumaIndividuales = cinta1.getCajasContadasPorEstaCinta() +
                               cinta2.getCajasContadasPorEstaCinta() +
                               cinta3.getCajasContadasPorEstaCinta();

        System.out.println("\nMarcador del almacén: " + contadorGlobal.getTotalCajas());
        System.out.println("Suma de los tornos:   " + sumaIndividuales);
    }
}
```

### 💡 Preguntas de Razonamiento Teórico

1. **¿Qué ocurre si haces `start()` y `join()` dentro del mismo bucle?**
   * **Respuesta:** La ejecución se vuelve estrictamente **secuencial**. El hilo principal arranca un hilo e inmediatamente se bloquea a esperarlo antes de pasar a la siguiente iteración. Aunque el total dará un valor correcto, se pierde completamente la concurrencia y el paralelismo.

2. **Diferencia entre sincronizar caja por caja vs. sincronizar solo la suma final acumulada:**
   * **Respuesta:** Sincronizar solo la suma final es **mucho más rápido e eficiente**. Entrar en un bloque `synchronized` en cada caja genera un alto consumo de CPU debido a los continuos bloqueos/desbloqueos del cerrojo y los cambios de contexto (*context switching*). Al contar localmente en una variable propia del hilo y sincronizar únicamente 1 vez al final, la sobrecarga del candado se reduce al mínimo.

---

## R2. Pipeline de Procesamiento / Cadena de Montaje

### 📌 Descripción del Ejercicio
Modelado de un flujo de trabajo concurrente por fases (`Captura`, `Filtro` y `Render`). Las fases `Captura` y `Filtro` se ejecutan al mismo tiempo en paralelo. La fase `Render` depende de las dos anteriores y no puede iniciar hasta que ambas hayan finalizado.

### 💻 Código Java
```java
class Fase implements Runnable {
    private final String nombre;

    public Fase(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 4; i++) {
            System.out.println(nombre + " paso " + i);
            try {
                Thread.sleep((long) (Math.random() * 200 + 100));
            } catch (InterruptedException e) {
                System.err.println("Hilo interrumpido: " + e.getMessage());
            }
        }
    }
}

public class MainR2 {
    public static void main(String[] args) {
        Thread hCaptura = new Thread(new Fase("Captura"));
        Thread hFiltro = new Thread(new Fase("Filtro"));
        Thread hRender = new Thread(new Fase("Render"));

        // 1. Iniciar las dos primeras fases en paralelo
        hCaptura.start();
        hFiltro.start();

        // 2. Barrera de sincronización: esperar a que terminen ambas
        try {
            hCaptura.join();
            hFiltro.join();
        } catch (InterruptedException e) {
            System.err.println("Error en la espera: " + e.getMessage());
        }

        // 3. Iniciar la fase dependiente únicamente tras completar la barrera
        hRender.start();
        try {
            hRender.join();
        } catch (InterruptedException e) {
            System.err.println("Error en la espera de Render: " + e.getMessage());
        }

        System.out.println("Procesamiento finalizado con éxito");
    }
}
```

---

## R3. Análisis de Secuencia y Salida Concurrente

### 📌 Código Analizado
```java
public class SecuenciaProcesos {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Proceso(), "P1");
        Thread t2 = new Thread(new Proceso(), "P2");

        t1.start();
        t1.join();

        t2.start();
        System.out.println("Cierre intermedio del main");
        t2.join();
        System.out.println("Fin del main");
    }

    static class Proceso implements Runnable {
        public void run() {
            String nombre = Thread.currentThread().getName();
            for (int i = 1; i <= 2; i++) {
                System.out.println(nombre + " - iteracion " + i);
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) { }
            }
        }
    }
}
```

### 📝 Salida Posible en Consola
```text
P1 - iteracion 1
P1 - iteracion 2
Cierre intermedio del main
P2 - iteracion 1
P2 - iteracion 2
Fin del main
```

### 🔍 Justificación
1. **`P1` es secuencial al inicio:** Debido a `t1.start()` seguido inmediatamente de `t1.join()`, el hilo `main` se bloquea hasta que `P1` finaliza completamente.
2. **Concurrencia entre `main` y `P2`:** Tras ejecutar `t2.start()`, tanto el hilo `main` como `P2` compiten concurrentemente por la CPU. La línea `"Cierre intermedio del main"` puede imprimirse **antes or después** de que `P2` empiece a imprimir en función de las decisiones del planificador (*scheduler*) del sistema operativo.
3. **Cierre garantizado:** `"Fin del main"` siempre será la última instrucción en ejecutarse porque `t2.join()` obliga a esperar la finalización de `P2`.

---

## R4. Monitores, `wait()` y `notifyAll()`

### 📌 Caso A: Uso incorrecto de `Thread.sleep()` dentro de `synchronized`
```java
class SistemaAlarma {
    private boolean activada = false;

    public synchronized void esperarActivacion() throws InterruptedException {
        while (!activada) {
            Thread.sleep(100); // MANTIENE EL CANDADO (LOCK)
        }
    }

    public synchronized void activar() {
        activada = true;
    }
}
```
* **Problema:** El hilo que llama a `esperarActivacion()` adquiere el cerrojo del objeto. Como `Thread.sleep()` **no libera el candado**, ningún otro hilo puede entrar al método `activar()` (también `synchronized`). Se produce un **bloqueo mutuo / interbloqueo (*deadlock*)**.

### 📌 Caso B: Uso de `wait()` sin `synchronized`
```java
class SistemaAlarma {
    private boolean activada = false;

    public void esperarActivacion() throws InterruptedException {
        while (!activada) {
            wait(); // LANZA EXCEPCIÓN
        }
    }

    public void activar() {
        activada = true;
        notifyAll();
    }
}
```
* **Problema:** Los métodos `wait()`, `notify()` y `notifyAll()` requieren obligatoriamente poseer el monitor del objeto. Al eliminar la palabra reservada `synchronized`, Java lanza en tiempo de ejecución la excepción **`IllegalMonitorStateException`**.

### 🛠️ Solución Correcta (Monitor Válido)
```java
class SistemaAlarma {
    private boolean activada = false;

    public synchronized void esperarActivacion() throws InterruptedException {
        while (!activada) {
            wait(); // Libera el candado y pasa a estado WAITING
        }
        System.out.println("¡Alarma detectada! Notificando emergencias.");
    }

    public synchronized void activar() {
        activada = true;
        System.out.println("Alarma activada por el sensor.");
        notifyAll(); // Despierta a los hilos esperando sobre la instancia
    }
}
```

---

## R5. Traza de Estados de los Hilos (`Thread.State`)

### 📌 Código Analizado
```java
class Dormilon extends Thread {
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { }
    }
}

class Esperador extends Thread {
    private final Thread otro;
    public Esperador(Thread otro) { this.otro = otro; }

    public void run() {
        try {
            otro.join();
        } catch (InterruptedException e) { }
    }
}

public class Estados {
    public static void main(String[] args) throws InterruptedException {
        Dormilon d = new Dormilon();
        Esperador e = new Esperador(d);

        System.out.println("(1) " + d.getState());

        d.start();
        e.start();

        Thread.sleep(500);

        System.out.println("(2) " + d.getState());
        System.out.println("(3) " + e.getState());

        e.join();

        System.out.println("(4) " + d.getState() + " - " + e.getState());
    }
}
```

### 📋 Estados Resultantes y Justificación

| Línea | Estado Devuelto | Justificación Técnica |
| :--- | :--- | :--- |
| **(1)** | `NEW` | El objeto `d` ha sido instanciado pero no se ha invocado `d.start()`. |
| **(2)** | `TIMED_WAITING` | Transcurridos 500 ms, el hilo `d` se encuentra ejecutando su `Thread.sleep(2000)`. |
| **(3)** | `WAITING` | El hilo `e` se encuentra bloqueado en la llamada `d.join()` sin tiempo límite, esperando a que el hilo `d` muera. |
| **(4)** | `TERMINATED - TERMINATED` | Tras superar el `e.join()`, ambos hilos han completado sus respectivos métodos `run()`. |
