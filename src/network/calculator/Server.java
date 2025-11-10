package network.calculator;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {

    public static void main(String[] args) throws IOException {
        int port = 5678; // 서버 포트 설정
        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService pool = Executors.newFixedThreadPool(5); // 다중 클라이언트 처리

        System.out.println("✅ Server listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            pool.execute(new ClientHandler(clientSocket));
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("🧵 Handling request from " + socket.getInetAddress()
                + " in thread " + Thread.currentThread().getName());

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String request;

            while ((request = in.readLine()) != null) {
                System.out.println("📩 Received: " + request);

                String[] tokens = request.split(" ");

                // ⚙️ CALC로 시작 안 하면 잘못된 명령
                if (tokens.length == 0 || !tokens[0].equals("CALC")) {
                    out.println("Error message:");
                    out.println("invalid command (must start with CALC)");
                    out.flush();
                    continue;
                }

                // ⚙️ 인자 개수 검사 (명령 + 연산자 + 피연산자 2개 = 4)
                if (tokens.length < 4) {
                    out.println("Error message:");
                    out.println("too few arguments");
                    out.flush();
                    continue;
                } else if (tokens.length > 4) {
                    out.println(request + " Error message:");
                    out.println("too many arguments");
                    out.flush();
                    continue;
                }

                // ⚙️ 숫자 변환
                String op = tokens[1];
                double a, b;
                try {
                    a = Double.parseDouble(tokens[2]);
                    b = Double.parseDouble(tokens[3]);
                } catch (NumberFormatException e) {
                    out.println("Error message:");
                    out.println("invalid number format");
                    out.flush();
                    continue;
                }

                // ⚙️ 연산 수행
                switch (op) {
                    case "ADD":
                        out.println("RESPONSE OK VALUE " + (a + b));
                        break;
                    case "SUB":
                        out.println("RESPONSE OK VALUE " + (a - b));
                        break;
                    case "MUL":
                        out.println("RESPONSE OK VALUE " + (a * b));
                        break;
                    case "DIV":
                        if (b == 0) {
                            out.println("Error message:");
                            out.println("divided by zero");
                        } else {
                            out.println("RESPONSE OK VALUE " + (a / b));
                        }
                        break;
                    default:
                        out.println(request + " Error message:");
                        out.println("unknown operator");
                        break;
                }

                out.flush(); // ✅ 항상 즉시 전송
            }

            System.out.println("🚪 Client " + socket.getInetAddress() + " disconnected.");

        } catch (IOException e) {
            System.err.println("❌ Connection lost with " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
