package Java;

import java.util.Random;

// Estimación de π mediante el método de Monte Carlo en versión secuencial (sin hilos).
public class MonteCarlo_SinHilos {

    /**
     * Ejecuta la simulación de Monte Carlo
     *
     * @param totalSamples número total de puntos a generar
     * @param rng generador de números aleatorios
     * @return cantidad de puntos dentro del círculo
     */
    private static long contarPuntosEnCirculo(long totalSamples, Random rng) {
        long hits = 0L;
        for (long i = 0; i < totalSamples; i++) {
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            if ((x * x + y * y) <= 1.0) hits++;
        }
        return hits;
    }

    public static void main(String[] args) {
        final long totalSamples = 1_000_000L;   // puntos totales a generar
        final double referencePi = 3.1415926535;
        final int p = 1; // solo 1 "hilo" en versión secuencial

        Random rng = new Random();

        // --- Medir tiempo secuencial (T_s) ---
        long t0 = System.nanoTime();
        long insideCircle = contarPuntosEnCirculo(totalSamples, rng);
        long t1 = System.nanoTime();
        double Ts = (t1 - t0) / 1_000_000.0;

        // --- Resultados principales ---
        double piApprox = (4.0 * insideCircle) / (double) totalSamples;
        double absError = Math.abs(piApprox - referencePi);

        System.out.printf("%nNúmero total de puntos: %d%n", totalSamples);
        System.out.printf("Puntos dentro del círculo: %d%n", insideCircle);
        System.out.printf("Aproximación de pi: %.10f%n", piApprox);
        System.out.printf("Error: %.10f%n", absError);

        // --- Métricas (aquí T_p = T_s porque p=1) ---
        double Tp = Ts;
        double speedup = Ts / Tp;         // siempre 1.0
        double efficiency = speedup / p;  // siempre 1.0
        double overhead = p * Tp - Ts;    // siempre 0.0

        System.out.printf("%nT_s: %.3f ms%n", Ts);
        System.out.printf("T_p (p=%d): %.3f ms%n", p, Tp);
        System.out.printf("Speedup: %.3f x%n", speedup);
        System.out.printf("Eficiencia: %.3f (%.1f %%)%n", efficiency, efficiency * 100.0);
        System.out.printf("Overhead: %.3f ms%n", overhead);
    }
}
