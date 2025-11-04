package parte2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Sem sincronização: count++ acontece em várias threads e pode perder incrementos.
 */
public class CorridaSemControle {
    static int count = 0;

    public static void main(String[] args) throws Exception {
        int T = 8, M = 250_000;

        ExecutorService pool = Executors.newFixedThreadPool(T);
        Runnable r = () -> {
            for (int i = 0; i < M; i++) {
                count++; // não é atômico
            }
        };

        long t0 = System.nanoTime();
        for (int i = 0; i < T; i++) pool.submit(r);
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
        long t1 = System.nanoTime();

        System.out.printf("Sem controle | Esperado=%d, Obtido=%d, Tempo=%.2fs%n",
                T * M, count, (t1 - t0) / 1e9);
    }
}
