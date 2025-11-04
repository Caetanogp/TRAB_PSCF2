package parte2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Com semáforo binário: garante que apenas uma thread atualize o contador por vez.
 */
public class CorridaComSemaphore {
    static int count = 0;
    static final Semaphore sem = new Semaphore(1, true);

    public static void main(String[] args) throws Exception {
        int T = 8, M = 250_000;

        ExecutorService pool = Executors.newFixedThreadPool(T);
        Runnable r = () -> {
            for (int i = 0; i < M; i++) {
                try {
                    sem.acquire();
                    count++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    sem.release();
                }
            }
        };

        long t0 = System.nanoTime();
        for (int i = 0; i < T; i++) pool.submit(r);
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
        long t1 = System.nanoTime();

        System.out.printf("Com semáforo | Esperado=%d, Obtido=%d, Tempo=%.2fs%n",
                T * M, count, (t1 - t0) / 1e9);
    }
}
