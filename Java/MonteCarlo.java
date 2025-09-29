package Java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class MonteCarlo {

    // Variables en Memoria Compartida
    private static long globalCount = 0;        // Variable compartida que todos los hilos modifican
    private static final ReentrantLock lock = new ReentrantLock(); // Cerrojo para sincronizar el acceso

    // Función que ejecuta cada hilo.
    private static class MonteCarloPi implements Runnable {
        private final int numSamples;
        private final int threadId;

        MonteCarloPi(int numSamples, int threadId) {
            this.numSamples = numSamples;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            long localCount = 0;
            for (int i = 0; i < numSamples; i++) {
                // Generar un punto aleatorio (x, y) en el rango [0, 1)
                double x = ThreadLocalRandom.current().nextDouble();
                double y = ThreadLocalRandom.current().nextDouble();
                // Verificar si el punto está dentro del círculo (x² + y² <= 1)
                if (x * x + y * y <= 1.0) {
                    localCount++;
                }
            }

            // ¡SECCIÓN CRÍTICA!
            lock.lock();
            try {
                System.out.println("Hilo " + threadId + ": añadiendo " + localCount + " puntos al total.");
                globalCount += localCount;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long totalSamples = 1_000_000L;
        int numThreads = 4;
        int samplesPerThread = (int)(totalSamples / numThreads); 

        List<Thread> threads = new ArrayList<>();

        // Crear y lanzar los hilos
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(new MonteCarloPi(samplesPerThread, i));
            threads.add(t);
            t.start();
        }

        // Esperar a que todos los hilos terminen
        for (Thread t : threads) {
            t.join();
        }
 
        double piApprox = (4.0 * globalCount) / (double) totalSamples;
        double truePi = 3.1415926535; 
        double error = Math.abs(piApprox - truePi);

        // Impresión de resultados
        System.out.println("\nNúmero total de puntos: " + totalSamples);
        System.out.println("Puntos dentro del círculo: " + globalCount);
        System.out.println("Aproximación de pi: " + piApprox);
        System.out.println("Error: " + error);
    }
}

