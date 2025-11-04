package parte1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Versão ingênua: pega esquerda depois direita.
 * Registra "pensando", "com fome" e "comendo" (limitado para evitar flood).
 * Pode travar; encerra após alguns segundos.
 */
public class FilosofosIngenuo {
    static final int N = 5;
    static final ReentrantLock[] forks = new ReentrantLock[N];
    static volatile boolean someoneAte = false;

    // registra estados só nas 2 primeiras refeições de cada filósofo
    static final int STATE_LOG_LIMIT = 2;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < N; i++) forks[i] = new ReentrantLock();

        ExecutorService pool = Executors.newFixedThreadPool(N);
        for (int p = 0; p < N; p++) {
            final int id = p;
            pool.submit(() -> philosopher(id));
        }

        // roda por alguns segundos e encerra
        Thread.sleep(6_000);
        pool.shutdownNow(); // sinaliza interrupção
        // não precisa await aqui; as threads saem do loop ao acordarem do sleep

        if (!someoneAte) {
            System.out.println("[IMPASSE] Ninguém conseguiu comer neste intervalo.");
        } else {
            System.out.println("[OBS] Alguém comeu, mas o protocolo pode travar em outras execuções.");
        }
    }

    static void philosopher(int id) {
        int left = id;
        int right = (id + 1) % N;
        int meals = 0;

        while (!Thread.currentThread().isInterrupted()) {
            if (meals < STATE_LOG_LIMIT) System.out.println("P" + id + " pensando");
            dormir(150);

            if (meals < STATE_LOG_LIMIT) System.out.println("P" + id + " com fome");
            forks[left].lock(); // pega o garfo da esquerda
            try {
                // tenta pegar o garfo da direita sem bloquear (não lança InterruptedException)
                if (!forks[right].tryLock()) {
                    // falhou: solta o esquerdo no finally e tenta de novo depois de um backoff
                    backoff();
                    continue;
                }
                try {
                    someoneAte = true;
                    System.out.println("P" + id + " comendo");
                    dormir(150);
                    meals++;
                } finally {
                    forks[right].unlock();
                }
            } finally {
                forks[left].unlock();
            }
        }
    }

    static void backoff() { dormir(40); }

    static void dormir(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
