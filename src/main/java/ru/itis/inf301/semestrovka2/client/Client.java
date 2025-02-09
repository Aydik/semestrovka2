package ru.itis.inf301.semestrovka2.client;

import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    @Getter
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    private int lobbyId;

    public void connect() {
        try {
            clientSocket = new Socket("localhost", 50000);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            System.out.println("Connected to server");
//            startReadingMessages();
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            closeResources();
        }
    }

    public void startReadingMessages() {
        Thread readerThread = new Thread(() -> {
            try {
                while (true) {
                    String serverMessage = in.readLine();
                    if (serverMessage == null) {
                        break;
                    }
                    System.out.println("\n[Server]: " + serverMessage);
                }
            } catch (IOException e) {
                System.out.println("\nDisconnected from server.");
            } finally {
                closeResources();
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void sendMessage(String message) {
        try {
            if (out != null && !clientSocket.isClosed() && !clientSocket.isOutputShutdown()) {
                out.write(message + "\n");
                out.flush();
            } else {
                System.out.println("Cannot send message. Connection is closed.");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            closeResources();
        }
    }

    public String readMessage() {
        try {
            if (in == null) {
                System.err.println("BufferedReader is not initialized.");
                return null;
            }
            String message = in.readLine();
            if (message != null) {
                message = message.replace("[Server]: ", ""); // Убираем префикс "[Server]: "
            }
            return message;
        } catch (IOException e) {
            System.err.println("Error reading message: " + e.getMessage());
            return null;
        }
    }

    public void closeResources() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }
}