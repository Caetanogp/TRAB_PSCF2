package parte1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ordem global: pega primeiro o garfo de menor índice e depois o de maior.
 * Assim não fecha ciclo e não trava.
 */
public class FilosofosOrdenados {
    static final int N = 5;
    static final ReentrantLock[] forks = new ReentrantLock[N];
    static final int MAX_MEALS_PER_PHIL = 6;
    static final int STATE_LOG_LIMIT = 2; // mostra estados nas 2 primeiras refeições

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < N; i++) forks[i] = new ReentrantLock(true); // justo ajuda a não favorecer sempre os mesmos

        ExecutorService pool = Executors.newFixedThreadPool(N);
        for (int p = 0; p < N; p++) {
            final int id = p;
            pool.submit(() -> philosopher(id));
        }

        pool.shutdown();
        if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("[AVISO] Ainda executando, encerrando forçado.");
            pool.shutdownNow();
        }
        System.out.println("[FINAL] Rodou com ordem global e não travou.");
    }

    static void philosopher(int id) {
        int a = id, b = (id + 1) % N;
        int first = Math.min(a, b), second = Math.max(a, b);

        int meals = 0;
        while (meals < MAX_MEALS_PER_PHIL) {
            if (meals < STATE_LOG_LIMIT) System.out.println("P" + id + " pensando");
            dormir(100);

            if (meals < STATE_LOG_LIMIT) System.out.println("P" + id + " com fome");
            forks[first].lock();
            try {
                forks[second].lock();
                try {
                    System.out.println("P" + id + " comendo (" + (meals + 1) + ")");
                    dormir(100);
                    meals++;
                } finally {
                    forks[second].unlock();
                }
            } finally {
                forks[first].unlock();
            }
            if (meals < STATE_LOG_LIMIT) System.out.println("P" + id + " pensando");
        }
    }

    static void dormir(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
