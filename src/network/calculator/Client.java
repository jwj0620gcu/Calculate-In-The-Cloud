package network.calculator;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        Config config = new Config("server_info.dat");

        try (
            Socket socket = new Socket(config.getServerIP(), config.getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("Connected to server (" + config.getServerIP() + ":" + config.getPort() + ")");
            System.out.println("Type 'exit' to quit.");

            while (true) {
                System.out.print("Enter operation (e.g. ADD 10 20): ");
                String input = userInput.readLine();
                if (input == null || input.equalsIgnoreCase("exit")) {
                    System.out.println("Disconnected from server.");
                    break;
                }

                out.println("CALC " + input); // 서버에 전송

                // ✅ 서버 응답을 여러 줄 받을 수 있도록 수정
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    response.append(line).append("\n");
                    if (!in.ready()) break; // 서버가 더 이상 보낼 게 없으면 중단
                }

                System.out.print("Server: " + response.toString());
            }

        } catch (IOException e) {
            System.err.println("❌ Connection error: " + e.getMessage());
        }
    }
}
