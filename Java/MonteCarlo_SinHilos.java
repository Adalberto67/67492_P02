package Java;

import java.util.Random;

public class MonteCarlo_SinHilos {

    public static void main(String[] args) {
        long totalSamples = 1_000_000L;
        long globalCount = 0;

        Random rng = new Random(); 

        // Generar puntos aleatorios y contar cuántos caen dentro del círculo
        for (long i = 0; i < totalSamples; i++) {
            double x = rng.nextDouble(); 
            double y = rng.nextDouble(); 
            if (x * x + y * y <= 1.0) {
                globalCount++;
            }
        }

        // Cálculo final
        double piApprox = (4.0 * globalCount) / (double) totalSamples;
        double truePi = 3.1415926535;
        double error = Math.abs(piApprox - truePi);

        // Resultados
        System.out.println("\nNúmero total de puntos: " + totalSamples);
        System.out.println("Puntos dentro del círculo: " + globalCount);
        System.out.println("Aproximación de pi: " + piApprox);
        System.out.println("Error: " + error);
    }
}


