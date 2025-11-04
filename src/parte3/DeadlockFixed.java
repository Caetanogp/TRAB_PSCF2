package parte3;

/**
 * Correção: todos obedecem a mesma ordem (A depois B).
 * Sem ordem cruzada, não fecha ciclo de espera.
 */
public class DeadlockFixed {
    static final Object LOCK_A = new Object();
    static final Object LOCK_B = new Object();

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(DeadlockFixed::trabalho);
        Thread t2 = new Thread(DeadlockFixed::trabalho);

        t1.start(); t2.start();
        t1.join();  t2.join();
        System.out.println("[OK] Rodou sem deadlock com ordem A->B.");
    }

    static void trabalho() {
        synchronized (LOCK_A) {
            synchronized (LOCK_B) {
                // seção crítica
            }
        }
    }
}
