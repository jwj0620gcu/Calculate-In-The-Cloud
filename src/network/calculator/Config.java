package network.calculator;

import java.io.*;

/**
 * Config.java
 * 
 * - 서버 접속 정보(IP, 포트 번호)를 파일(server_info.dat)에서 불러옴
 * - 파일이 없거나 읽기 실패 시 기본값으로 localhost:5678 사용
 */
public class Config {
    private String serverIP = "localhost";
    private int port = 5678;

    public Config(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            this.serverIP = br.readLine().trim();
            this.port = Integer.parseInt(br.readLine().trim());
        } catch (Exception e) {
            System.out.println("Config file not found. Using default settings (localhost:5678)");
        }
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getPort() {
        return port;
    }
}
