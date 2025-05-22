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
            System.out.println("[4] Listar registros");
            System.out.println("[0] Sair do menu do dispositivo");
            System.out.print("Escolha: ");

            String opcao = scanner.nextLine();
            switch (opcao) {
                case "1": inserir5(); break;
                case "2": alterar(4);
                          alterar(5);
                          alterar(6);
                          alterar(7);
                          alterar(8);break;

                case "3": remover(11);
                          remover(12);
                          remover(13);
                          remover(14);
                          remover(15); break;
                case "4": lista.listar(); break;
                case "0": System.out.println("Encerrando menu do dispositivo..."); return;
                default: System.out.println("Opção inválida");
            }
        }
    }

    private static void inserir5() {
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            String dispositivo = "D" + (10 + i);
            double temp = 15 + rand.nextDouble() * 20;
            double umi = 40 + rand.nextDouble() * 50;
            double pres = 980 + rand.nextDouble() * 40;

            RegistroClimatico novo = new RegistroClimatico(idSequencial++, dispositivo, temp, umi, pres);
            lista.inserir(novo);
            arvore.inserir(novo.getIdRegistro(), novo);
            registrarLog("INSERIR_TESTE", novo.getIdRegistro(), arvore.getUltimaRotacao());
            System.out.println("[DISPOSITIVO] Registro inserido: " + novo);
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
                    case "BUSCAR": //Está usando arvore
                        if (partes.length != 2) {
                            out.println("Uso: BUSCAR <id>");
                        } else {
                            int id = Integer.parseInt(partes[1]);
                            RegistroClimatico reg = arvore.buscar(id);
                            out.println(reg != null ? reg : "Registro não encontrado.");
                        }
                        break;

                    case "REMOVER": //Está usando arvore
                        if (partes.length != 2) {
                            out.println("Uso: REMOVER <id>");
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
                        }
                        break;

                    case "BUSCAR_DISPOSITIVO":
                        if (partes.length != 2) {
                            out.println("Uso: BUSCAR_DISPOSITIVO <idDispositivo>");
                        } else {
                            String dispositivo = partes[1];
                            RegistroClimatico atual = lista.getInicio();
                            boolean encontrou = false;
                            while (atual != null) {
                                if (atual.getIdDispositivo().equals(dispositivo)) {
                                    out.println(atual);
                                    encontrou = true;
                                }
                                atual = atual.getProximo();
                            }
                            if (!encontrou) out.println("Nenhum registro encontrado para o dispositivo.");
                        }
                        break;

                    case "BUSCAR_PASSOS":
                        if (partes.length != 2) {
                            out.println("Uso: BUSCAR_PASSOS <id>");
                        } else {
                            int id = Integer.parseInt(partes[1]);
                            int passos = lista.buscarContandoPassos(id);
                            if (passos == -1) {
                                out.println("Registro não encontrado na lista.");
                            } else {
                                out.println("Registro encontrado em " + passos + " passos na lista ligada.");
                            }
                        }
                        break;

                    case "CONTAR":
                        out.println("Total de registros: " + lista.contar());
                        break;

                    case "LISTAR":
                        StringWriter buf = new StringWriter();
                        PrintWriter w = new PrintWriter(buf);
                        arvore.emOrdem(w);
                        w.flush();
                        out.println(buf.toString().trim());
                        break;

                    case "SAIR":
                        out.println("Conexão encerrada.");
                        return;

                    default:
                        out.println("Comando desconhecido.");
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
        Random rand = new Random();
        List<RegistroClimatico> tempList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            String dispositivo = "D" + (i % 10);
            double temp = 15 + rand.nextDouble() * 20;
            double umi = 40 + rand.nextDouble() * 50;
            double pres = 980 + rand.nextDouble() * 40;

            RegistroClimatico reg = new RegistroClimatico(idSequencial++, dispositivo, temp, umi, pres);
            tempList.add(reg);
        }

        Collections.shuffle(tempList);

        for (RegistroClimatico reg : tempList) {
            lista.inserir(reg);
            arvore.inserir(reg.getIdRegistro(), reg);
            registrarLog("AUTOINSERCAO", reg.getIdRegistro(), arvore.getUltimaRotacao());
        }

        System.out.println("[SERVIDOR] 100 registros simulados (ordem aleatória) adicionados.");
    }
}
