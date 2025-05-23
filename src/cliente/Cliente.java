package cliente;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            String menu = """
                === CLIENTE ===
                1 - Buscar por ID
                2 - Remover registro
                3 - Buscar por ID e mostrar passos na lista
                4 - Listar registros
                5 - Contar registros
                0 - Sair
                Escolha:
                """;

            while (true) {
                System.out.print(menu);
                String opcao = scanner.nextLine();
                switch (opcao) {
                    case "1":
                        System.out.print("ID do registro: ");
                        String id = scanner.nextLine();
                        out.println("BUSCAR " + id);
                        break;
//                    case "2":
//                        System.out.print("ID do dispositivo: ");
//                        String disp = scanner.nextLine();
//                        out.println("BUSCAR_DISPOSITIVO " + disp);
//                        break;
                    case "2":
                        System.out.print("ID do registro a remover: ");
                        String idRem = scanner.nextLine();
                        out.println("REMOVER " + idRem);
                        break;
                    case "3":
                        System.out.print("ID do registro: ");
                        String idBusca = scanner.nextLine();
                        out.println("BUSCAR_PASSOS " + idBusca);
                        break;
                    case "4":
                        out.println("LISTAR");
                        break;
                    case "5":
                        out.println("CONTAR");
                        break;
                    case "0":
                        out.println("SAIR");
                        return;
                    default:
                        System.out.println("Opção inválida.");
                        continue;
                }

                String linha;
                while ((linha = in.readLine()) != null) {
                    if (linha.trim().isEmpty()) break; // fim da resposta
                    System.out.println(linha);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao conectar com o servidor: " + e.getMessage());
        }
    }
}
