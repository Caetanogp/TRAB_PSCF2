package parte3;

/**
 * Deadlock clássico: T1 usa A->B e T2 usa B->A.
 * Agora, ao detectar deadlock, o programa encerra com System.exit(0).
 */
public class DeadlockDemo {
    static final Object LOCK_A = new Object();
    static final Object LOCK_B = new Object();

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            synchronized (LOCK_A) {
                System.out.println("T1 pegou A, tentando B...");
                dormir(50);
                synchronized (LOCK_B) {
                    System.out.println("T1 concluiu");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (LOCK_B) {
                System.out.println("T2 pegou B, tentando A...");
                dormir(50);
                synchronized (LOCK_A) {
                    System.out.println("T2 concluiu");
                }
            }
        });

        t1.start();
        t2.start();

        // espera um pouco e verifica se ambos continuam vivos (presos)
        Thread.sleep(2000);
        if (t1.isAlive() && t2.isAlive()) {
            System.out.println("[DEADLOCK] As duas threads ficaram esperando.");
            // não adianta interromper: synchronized não libera.
            // encerra explicitamente o processo para a demo não ficar presa.
            System.exit(0); // use 0 (sucesso) ou 1 se preferir sinalizar erro
        }

        // se por algum motivo não travou, termina normalmente
        t1.join();
        t2.join();
        System.out.println("[OK] Sem deadlock nesta execução.");
    }

    static void dormir(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
