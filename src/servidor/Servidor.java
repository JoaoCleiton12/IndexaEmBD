package servidor;

import estrutura.Lista;
import estrutura.ArvoreAVL;
import model.RegistroClimatico;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

public class Servidor {
    private static Lista lista = new Lista();
    private static ArvoreAVL arvore = new ArvoreAVL();
    private static int idSequencial = 1;
    private static PrintWriter logWriter;
    private static int id = 101;
    private static int[] ids11, ids12, ids13, ids14, ids15;

    public static void main(String[] args) {
        try {
            logWriter = new PrintWriter(new FileWriter("log_operacoes.txt", true), true);
        } catch (IOException e) {
            System.out.println("[SERVIDOR] Erro ao abrir arquivo de log.");
        }

        System.out.println("[SERVIDOR] Iniciando...");
        popularRegistrosIniciais();

        // Thread para interação local via terminal (simulando dispositivos)
        new Thread(() -> menuDispositivo()).start();

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("[SERVIDOR] Aguardando conexões na porta 5000...");
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("[SERVIDOR] Cliente conectado: " + clienteSocket.getInetAddress());
                new Thread(() -> atenderCliente(clienteSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void menuDispositivo() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== MENU DO DISPOSITIVO (TERMINAL DO SERVIDOR) ===");
            System.out.println("[1] Inserir 5 registros (5 dispositivos)");
            System.out.println("[2] Alterar 5 registros");
            System.out.println("[3] Remover 5 registros");
            System.out.println("[4] Listar registros (Conforme esta na lista)");
            System.out.println("[0] Sair do menu do dispositivo");
            System.out.print("Escolha: ");

            String opcao = scanner.nextLine();
            switch (opcao) {
                case "1": ids11 = inserirMultiplos("D11",5);
                          ids12 = inserirMultiplos("D12",5);
                          ids13 = inserirMultiplos("D13",5);
                          ids14 = inserirMultiplos("D14",5);
                          ids15 = inserirMultiplos("D15",5); break;
                case "2": alterarMultiplos(ids11);
                          alterarMultiplos(ids12);
                          alterarMultiplos(ids13);
                          alterarMultiplos(ids14);
                          alterarMultiplos(ids15);
                          break;

                case "3": removerMultiples(ids11);
                          removerMultiples(ids12);
                          removerMultiples(ids13);
                          removerMultiples(ids14);
                          removerMultiples(ids15); break;
                case "4": lista.listar(); break;//---------------------------------------VER ISSO
                case "0": System.out.println("Encerrando menu do dispositivo..."); return;
                default: System.out.println("Opção inválida");
            }
        }
    }

    private static int[] inserirMultiplos(String dispositivo, int quantidade) {
        int[] ids = new int[quantidade];
        for (int i = 0; i < quantidade; i++) {
            ids[i] = inserir(dispositivo);
        }
        return ids;
    }

    private static int inserir(String dispositivo) {
        Random rand = new Random();
        double temp = 15 + rand.nextDouble() * 20;
        double umi = 40 + rand.nextDouble() * 50;
        double pres = 980 + rand.nextDouble() * 40;

        int novoId = id++;
        RegistroClimatico novo = new RegistroClimatico(novoId, dispositivo, temp, umi, pres);
        lista.inserir(novo);
        arvore.inserir(novo.getIdRegistro(), novo);
        registrarLog("INSERIR_TESTE", novo.getIdRegistro(), arvore.getUltimaRotacao());
        System.out.println("[DISPOSITIVO] Registro inserido: " + novo);
        return novoId;
    }

    private static void alterarMultiplos(int[] ids) {
        for (int id : ids) {
            alterar(id);
        }
    }

    private static void alterar(int id) {
        RegistroClimatico reg = arvore.buscar(id);
        if (reg != null) {
            Random rand = new Random();
            reg.setTemperatura(20 + rand.nextDouble() * 10);
            reg.setUmidade(50 + rand.nextDouble() * 20);
            reg.setPressao(1000 + rand.nextDouble() * 15);
            System.out.println("[DISPOSITIVO] Registro alterado: " + reg);
        } else {
            System.out.println("[DISPOSITIVO] ID " + id + " não encontrado na árvore.");
        }
    }


    private static void removerMultiples(int[] ids) {
        for (int id : ids) {
            remover(id);
        }
    }


    private static void remover(int id) {
        RegistroClimatico reg = arvore.buscar(id);
        if (reg != null) {
            boolean ok = lista.remover(id);
            arvore.remover(id);
            registrarLog("REMOVER_TESTE", id, arvore.getUltimaRotacao());
            if (ok) {
                System.out.println("[DISPOSITIVO] Registro removido: ID " + id);
            } else {
                System.out.println("[DISPOSITIVO] Falha ao remover ID " + id + " da lista.");
            }
        } else {
            System.out.println("[DISPOSITIVO] ID " + id + " não encontrado na árvore.");
        }
    }

    private static void atenderCliente(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String linha;
            while ((linha = in.readLine()) != null) {
                String[] partes = linha.split(" ");
                String comando = partes[0];


                switch (comando) {
                    case "BUSCAR":
                        if (partes.length != 2) {
                            out.println("Uso: BUSCAR <id>");
                            out.println();
                        } else {
                            int id = Integer.parseInt(partes[1]);
                            RegistroClimatico reg = arvore.buscar(id);
                            int passos = arvore.buscarContandoPassos(id);
                            if (reg != null) {
                                out.println(reg);
                                out.println("→ Encontrado em " + passos + " passos na árvore AVL.");
                            } else {
                                out.println("Registro não encontrado.");
                            }
                            out.println();
                        }
                        break;

                    case "REMOVER":
                        if (partes.length != 2) {
                            out.println("Uso: REMOVER <id>");
                            out.println();
                        } else {
                            int id = Integer.parseInt(partes[1]);
                            RegistroClimatico reg = arvore.buscar(id);
                            if (reg != null) {
                                boolean ok = lista.remover(id);
                                arvore.remover(id);
                                registrarLog("REMOVER", id, arvore.getUltimaRotacao());
                                out.println(ok ? "Registro removido." : "Erro ao remover.");
                            } else {
                                out.println("Registro não encontrado.");
                            }
                            out.println();
                        }
                        break;

                    case "BUSCAR_PASSOS":
                        if (partes.length != 2) {
                            out.println("Uso: BUSCAR_PASSOS <id>");
                            out.println();
                        } else {
                            int id = Integer.parseInt(partes[1]);
                            int passos = lista.buscarContandoPassos(id);
                            if (passos == -1) {
                                out.println("Registro não encontrado na lista.");
                            } else {
                                out.println("Registro encontrado em " + passos + " passos na lista ligada.");
                            }
                            out.println();
                        }
                        break;

                    case "CONTAR":
                        out.println("Total de registros: " + lista.contar());
                        out.println();
                        break;

                    case "LISTAR":
                        StringWriter buf = new StringWriter();
                        PrintWriter w = new PrintWriter(buf);
                        arvore.emOrdem(w);
                        w.flush();
                        out.println(buf.toString().trim());
                        out.println();
                        break;

                    case "SAIR":
                        out.println("Conexão encerrada.");
                        break;

                    default:
                        out.println("Comando desconhecido.");
                        out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("[SERVIDOR] Erro: " + e.getMessage());
        }
    }

    private static void registrarLog(String operacao, int id, String rotacao) {
        if (logWriter != null) {
            String linha = String.format("[%s] OP: %s ID: %d | Altura AVL: %d | Rotação: %s",
                    LocalDateTime.now(), operacao, id, arvore.getAltura(), rotacao);
            logWriter.println(linha);
        }
    }

    private static void popularRegistrosIniciais() {
        int total = 100;
        Random rand = new Random();
        // Marca quais IDs já foram usados
        boolean[] usado = new boolean[total + 1]; // índices 1..100

        for (int i = 0; i < total; i++) {
            // Gera um ID aleatório entre 1 e 100, sem repetição
            int id;
            do {
                id = rand.nextInt(total) + 1; // [1,100]
            } while (usado[id]);
            usado[id] = true;

            // Cria o registro com esse ID único
            String dispositivo = "D" + (id % 10);
            double temp       = 15  + rand.nextDouble() * 20;
            double umi        = 40  + rand.nextDouble() * 50;
            double pres       = 980 + rand.nextDouble() * 40;

            RegistroClimatico reg = new RegistroClimatico(id, dispositivo, temp, umi, pres);

            // Insere na lista e na AVL normalmente
            lista.inserir(reg);
            arvore.inserir(reg.getIdRegistro(), reg);
            registrarLog("AUTOINSERCAO", reg.getIdRegistro(), arvore.getUltimaRotacao());
        }

        System.out.println("[SERVIDOR] 100 registros simulados (IDs aleatórios) adicionados.");
    }

}
