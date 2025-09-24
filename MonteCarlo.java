import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonteCarlo {
    // Memoria compartida (paralelo)
    private static long sharedHits = 0L;
    private static final Lock lock = new ReentrantLock();

    // Tarea de cada hilo
    private static class PiTask implements Runnable {
        private final long samples;
        private final int id;
        PiTask(long samples, int id) { this.samples = samples; this.id = id; }

        @Override public void run() {
            long local = 0L;
            for (long i = 0; i < samples; i++) {
                double x = ThreadLocalRandom.current().nextDouble();
                double y = ThreadLocalRandom.current().nextDouble();
                if (x * x + y * y <= 1.0) local++;
            }
            lock.lock();
            try {
                System.out.printf("Hilo %d: añadiendo %d puntos al total.%n", id, local);
                sharedHits += local;
            } finally { lock.unlock(); }
        }
    }

    // Versión secuencial (para T_s)
    private static long runSequential(long n) {
        long hits = 0L;
        for (long i = 0; i < n; i++) {
            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();
            if (x * x + y * y <= 1.0) hits++;
        }
        return hits;
    }

    public static void main(String[] args) {
        final long totalSamples = 1_000_000L;
        final int threads = 4;
        final double PI = 3.1415926535;

        // ---- T_s (secuencial) ----
        long t0s = System.nanoTime();
        long hitsSeq = runSequential(totalSamples);
        long t1s = System.nanoTime();
        double Ts = (t1s - t0s) / 1_000_000.0;
        double piSeq = (4.0 * hitsSeq) / (double) totalSamples;         // usa hitsSeq
        double errSeq = Math.abs(piSeq - PI);                           // usa hitsSeq
        System.out.printf("Aproximación (secuencial): %.10f | Error: %.10f%n", piSeq, errSeq);

        // ---- T_p (paralelo) ----
        long base = totalSamples / threads;
        int rem = (int)(totalSamples % threads);

        sharedHits = 0L;
        List<Thread> workers = new ArrayList<>(threads);
        long t0p = System.nanoTime();
        for (int i = 0; i < threads; i++) {
            long chunk = base + (i < rem ? 1 : 0); // reparto exacto
            Thread t = new Thread(new PiTask(chunk, i));
            workers.add(t);
            t.start();
        }
        for (Thread t : workers) {
            try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        long t1p = System.nanoTime();
        double Tp = (t1p - t0p) / 1_000_000.0;

        // ---- Resultados ----
        double pi = (4.0 * sharedHits) / (double) totalSamples;
        System.out.printf("%nNúmero total de puntos: %d%n", totalSamples);
        System.out.printf("Puntos dentro del círculo: %d%n", sharedHits);
        System.out.printf("Aproximación de pi: %.10f%n", pi);
        System.out.printf("Error: %.10f%n", Math.abs(pi - PI));

        // ---- Métricas ----
        double speedup = Ts / Tp;
        double efficiency = speedup / threads;
        double overhead = threads * Tp - Ts;

        System.out.printf("%nT_s: %.3f ms%n", Ts);
        System.out.printf("T_p (p=%d): %.3f ms%n", threads, Tp);
        System.out.printf("Speedup: %.3f x%n", speedup);
        System.out.printf("Eficiencia: %.3f (%.1f %%)%n", efficiency, efficiency * 100.0); // %% para imprimir %
        System.out.printf("Overhead: %.3f ms%n", overhead);
    }
}