package parte3;

/**
 * Deadlock clássico: T1 usa A->B e T2 usa B->A.
 * Ao detectar deadlock, encerra com System.exit(0) para a demo não ficar presa.
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

        // espera um pouco e checa se ambos ficaram presos
        Thread.sleep(2000);
        if (t1.isAlive() && t2.isAlive()) {
            System.out.println("[DEADLOCK] As duas threads ficaram esperando.");
            System.exit(0);
        }

        t1.join();
        t2.join();
        System.out.println("[OK] Sem deadlock nesta execução.");
    }

    static void dormir(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
