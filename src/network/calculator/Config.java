package network.calculator;
import java.io.*;

public class Config {
    private String serverIP = "localhost";
    private int port = 5678;

    public Config(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            this.serverIP = br.readLine().trim();
            this.port = Integer.parseInt(br.readLine().trim());
        } catch (Exception e) {
            System.out.println("Config file not found. Using default settings (localhost:1234)");
        }
    }

    public String getServerIP() { return serverIP; }
    public int getPort() { return port; }
}
