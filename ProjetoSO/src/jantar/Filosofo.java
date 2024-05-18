package jantar;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Filosofo extends Thread {
    private int identificador;
    private Lock garfoEsquerdo, garfoDireito;
    private Random aleatorio;
    private static int[] contadorComida = new int[5]; // Quantidade de comida consumida por cada filósofo
    private static long[] tempoDeAlimentacao = new long[5]; // Tempo total de alimentação de cada filósofo

    public Filosofo(int identificador, Lock garfoEsquerdo, Lock garfoDireito) {
        this.identificador = identificador;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
        this.aleatorio = new Random();
    }

    @Override
    public void run() {
        try {
            while (true) {
                // O filósofo está ponderando
                System.out.println("Filósofo " + identificador + " está pensando.");
                Thread.sleep(aleatorio.nextInt(1000));
                
                // Registra o tempo de início da refeição
                long tempoInicioRefeicao = System.currentTimeMillis();
                
                // Tenta pegar os dois garfos
                if (garfoEsquerdo.tryLock() && garfoDireito.tryLock()) {
                    // O filósofo pegou ambos os garfos e está se alimentando
                    System.out.println("Filósofo " + identificador + " conseguiu os garfos e está saboreando sua refeição.");
                    Thread.sleep(aleatorio.nextInt(1000));
                    contadorComida[identificador]++; // Incrementa a quantidade de comida consumida
                    
                    // Libera os garfos após terminar de comer
                    garfoEsquerdo.unlock();
                    garfoDireito.unlock();
                } else {
                    // O filósofo não conseguiu pegar ambos os garfos
                    System.out.println("Filósofo " + identificador + " não conseguiu pegar ambos os garfos.");
                }
                
                // Registra o tempo de fim da refeição e calcula o tempo total de alimentação
                long tempoFimRefeicao = System.currentTimeMillis();
                long tempoAlimentacao = tempoFimRefeicao - tempoInicioRefeicao;
                tempoDeAlimentacao[identificador] += tempoAlimentacao;
            }
        } catch (InterruptedException e) {
            // Tratamento de interrupção
        }
    }

    public static void main(String[] args) {
        int n = 5; // Número de filósofos
        Filosofo[] filosofos = new Filosofo[n];
        Lock[] garfos = new ReentrantLock[n];

        // Inicialização dos garfos
        for (int i = 0; i < n; i++) {
            garfos[i] = new ReentrantLock();
        }

        // Inicialização dos filósofos e execução das threads
        for (int i = 0; i < n; i++) {
            filosofos[i] = new Filosofo(i, garfos[i], garfos[(i + 1) % n]);
            filosofos[i].start();
        }

        try {
            Thread.sleep(5000); // Tempo limite do jantar
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Interrupção das threads dos filósofos
        for (Filosofo filosofo : filosofos) {
            filosofo.interrupt();
        }
        System.out.println("------------------------------------------------------------------------");

        // Exibição dos resultados
        System.out.println("Tempo de alimentação de cada filósofo:");
        for (int i = 0; i < n; i++) {
            System.out.println("Filósofo " + i + ": " + tempoDeAlimentacao[i] + " ms");
        }

        for (int i = 0; i < n; i++) {
            System.out.println("Filósofo " + i + " comeu " + contadorComida[i] + " pedaços de comida.");
        }
        System.out.println("------------------------------------------------------------------------");

        System.out.println("Resumo:");
        for (int i = 0; i < n; i++) {
            System.out.println("Filósofo " + i + ": Tempo de alimentação - " + tempoDeAlimentacao[i] + " ms, Comeu - " + contadorComida[i] + " vezes");
        }
    }
}

