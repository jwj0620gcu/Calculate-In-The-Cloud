package network.calculator;

import java.io.*;
import java.net.*;

/**
 * Client.java
 * 
 * - 사용자 입력을 받아 서버로 요청을 전송하고, 서버의 응답을 출력
 * - Config 클래스를 이용하여 서버 IP와 포트 정보를 불러옴
 */
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

                // 서버로 요청 전송
                out.println("CALC " + input);

                // 서버 응답 수신 (여러 줄 가능)
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    response.append(line).append("\n");
                    if (!in.ready()) break; // 더 이상 읽을 데이터가 없으면 종료
                }

                System.out.print("Server: " + response.toString());
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
