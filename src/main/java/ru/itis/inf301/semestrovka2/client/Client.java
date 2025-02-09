package ru.itis.inf301.semestrovka2.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> steps = new ConcurrentLinkedQueue<>();

    public void connect() {
        try {
            clientSocket = new Socket("localhost", 50000);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            System.out.println("Connected to server");

            startReadingMessages();
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private void startReadingMessages() {
        Thread readerThread = new Thread(() -> {
            try {
                while (true) {
                    String serverMessage = in.readLine();
                    if (serverMessage.startsWith("STEP")) {
                        steps.add(serverMessage);
                    }
                    else if (serverMessage.startsWith("MESSAGE")) {
                        messages.add(serverMessage);
                    }
                    System.out.println("\n" + serverMessage);
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
                System.out.println("Sending message: " + message);
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
    public String getMessage() {
        String message = messages.poll();
        if (message != null) {
            return message;
        }
        return null;
    }
}
