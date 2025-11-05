# SO — Filósofos, Semáforos e Deadlock

Aluno: Caetano Goulart Padoin  
Instituição: Pontifícia Universidade Católica do Paraná  
Período: Quarto A  
Disciplina: Performance em Sistemas Ciber-Físicos

## O que foi implementado
- **Parte 1 — Jantar dos Filósofos**
  - `FilosofosIngenuo.java`: protocolo ingênuo (pode travar).
  - `FilosofosOrdenados.java`: solução com ordem global dos garfos (não trava).
- **Parte 2 — Condição de corrida**
  - `CorridaSemControle.java`: contador sem sincronização (perde incrementos).
  - `CorridaComSemaphore.java`: contador com `Semaphore(1, true)` (valor correto).
- **Parte 3 — Deadlock**
  - `DeadlockDemo.java`: dois locks (A/B) travando por ordem oposta.
  - `DeadlockFixed.java`: todos obedecem A→B e roda sem deadlock.

## Ideia em poucas linhas
- **Filósofos**: se todos pegam um garfo e esperam o outro, pode formar ciclo. Com ordem fixa (pegar sempre o menor índice antes), não fecha ciclo.
- **Corrida**: `count++` não é atômico em várias threads e dá valor menor que o esperado. Com semáforo binário, só um thread por vez entra na seção crítica.
- **Deadlock**: T1 usa A→B e T2 usa B→A. Se todos seguirem A→B, não ocorre espera circular.

## Como compilar e rodar (JDK 17+)
Em terminal na raiz do projeto:
```bash
cd src
javac -d ../out parte1/*.java parte2/*.java parte3/*.java
cd ..
# Parte 1
java -cp out parte1.FilosofosIngenuo
java -cp out parte1.FilosofosOrdenados
# Parte 2
java -cp out parte2.CorridaSemControle
java -cp out parte2.CorridaComSemaphore
# Parte 3
java -cp out parte3.DeadlockDemo
java -cp out parte3.DeadlockFixed
Pseudocódigo — hierarquia de recursos (sem Java)
css
Copiar código
Dados:
- N = 5 filósofos
- Garfos 0..N-1 (garfo i entre filósofos i e (i+1) mod N)

Para cada filósofo p:
  left  = min(garfo_esquerda(p), garfo_direita(p))
  right = max(garfo_esquerda(p), garfo_direita(p))

Loop:
  pensar()
  estado[p] <- "com fome"
  adquirir(left)   // bloqueia até o garfo estar livre
  adquirir(right)  // idem
  estado[p] <- "comendo"
  comer()
  liberar(right)
  liberar(left)
  estado[p] <- "pensando"
```
Justiça/progresso: locks “justos” (fila) ou um árbitro (“garçom”) ajudam a reduzir inanição quando há muita competição.

Relação com Coffman (por que não trava)
No ingênuo, as 4 condições podem aparecer juntas: exclusão mútua, manter-e-esperar, não-preempção e espera circular.
A solução impõe ordem global nos garfos (sempre pegar o de menor índice antes do maior), então quebra a espera circular. Assim, o impasse não ocorre.

Mapa das condições de Coffman (DeadlockDemo)
Exclusão mútua: cada lock (A ou B) só pode ser segurado por uma thread por vez.

Manter-e-esperar: T1 segura A e espera por B; T2 segura B e espera por A.

Não-preempção: locks não podem ser tomados à força.

Espera circular: T1 → B e T2 → A formam um ciclo (A→B e B→A).
A correção A→B remove a espera circular.

Observação de memória (Semaphore)
Em Java, release() do semáforo estabelece um happens-before com o próximo acquire().
Isso garante visibilidade das atualizações feitas na seção crítica entre threads.

Resultados esperados (resumo rápido)
parte1.FilosofosIngenuo → imprime estados (pensando/com fome/comendo) e termina com:

[OBS] Alguém comeu, mas o protocolo pode travar em outras execuções. ou

[IMPASSE] Ninguém conseguiu comer neste intervalo.

parte1.FilosofosOrdenados → cada filósofo come algumas vezes e finaliza com:

[FINAL] Rodou com ordem global e não travou.

parte2.CorridaSemControle → Obtido < Esperado (perda de incrementos).

parte2.CorridaComSemaphore → Obtido == Esperado (tempo pode ser maior; modo justo evita favorecer sempre as mesmas threads).

parte3.DeadlockDemo → mostra travamento e encerra a execução (mensagem de deadlock).

parte3.DeadlockFixed → [OK] Rodou sem deadlock com ordem A->B.

Link do vídeo
https://www.youtube.com/watch?v=ro2PzaoErrs
