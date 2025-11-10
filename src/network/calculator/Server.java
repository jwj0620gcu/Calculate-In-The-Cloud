package network.calculator;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * Server.java
 * 
 * - 클라이언트의 산술 연산 요청을 처리하는 서버 프로그램
 * - ThreadPool을 이용해 여러 클라이언트 요청을 동시에 처리
 * - 프로토콜 형식: CALC <OPERATION> <A> <B>
 */
public class Server {

    public static void main(String[] args) throws IOException {
        int port = 5678; // 서버 포트 설정
        ServerSocket serverSocket = new ServerSocket(port);

        // 다중 클라이언트 처리를 위한 스레드 풀 (최대 5개 동시 접속)
        ExecutorService pool = Executors.newFixedThreadPool(5);

        System.out.println("Server listening on port " + port);

        // 클라이언트 연결을 무한히 수락
        while (true) {
            Socket clientSocket = serverSocket.accept();
            pool.execute(new ClientHandler(clientSocket)); // 각 클라이언트 연결을 개별 스레드로 처리
        }
    }
}

/**
 * ClientHandler 클래스
 * - Runnable 인터페이스를 구현하여 클라이언트 요청을 병렬로 처리
 */
class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Handling request from " + socket.getInetAddress()
                + " in thread " + Thread.currentThread().getName());

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String request;

            // 클라이언트로부터 지속적으로 요청 수신
            while ((request = in.readLine()) != null) {
                System.out.println("Received: " + request);

                String[] tokens = request.split(" ");

                // 명령이 CALC로 시작하지 않는 경우
                if (tokens.length == 0 || !tokens[0].equals("CALC")) {
                    out.println("Error message:");
                    out.println("invalid command (must start with CALC)");
                    out.flush();
                    continue;
                }

                // 인자 개수 검사 (CALC + OP + A + B → 4개 필요)
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

                // 피연산자 숫자 변환 시도
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

                // 연산 수행
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

                out.flush(); // 결과 즉시 전송
            }

            System.out.println("Client " + socket.getInetAddress() + " disconnected.");

        } catch (IOException e) {
            System.err.println("Connection lost with " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
