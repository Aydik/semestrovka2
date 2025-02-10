package ru.itis.inf301.semestrovka2.client;

import lombok.Getter;
import lombok.Setter;
import ru.itis.inf301.semestrovka2.model.Board;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> steps = new ConcurrentLinkedQueue<>();
    @Getter @Setter
    private int client_index;
    @Getter @Setter
    private Board board;

    public void connect() {
        try {
            clientSocket = new Socket("localhost", 50000);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            System.out.println("Connected to server");

            startReadingMessages();

            board = new Board();
            updateBoard();

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private void startReadingMessages() {
        Thread readerThread = new Thread(() -> {
            try {
                while (true) {
                    String serverMessage = in.readLine();
                    if (serverMessage != null) {
                        if (serverMessage.startsWith("STEP")) {
                            steps.add(serverMessage);
                            System.out.println("\n" + serverMessage);
                        } else if (serverMessage.startsWith("MESSAGE")) {
                            messages.add(serverMessage);
                        }
                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                System.out.println("\nDisconnected from server.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
        return messages.poll();
    }

    public String getStep() {
        return steps.poll();
    }

    public void updateBoard() {
        new Thread(() -> {
            String step;
            while (true) {
                step = getStep();

                if (step == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    continue;
                }

                step = step.replace("STEP ", "");
                board = new Board(step);
            }

        }).start();
    }

    public void move(int finalRow, int finalCol) {
        if (board.move(client_index, finalRow, finalCol)) {
            sendMessage("STEP " + board);
        }
    }

    public void putVerticalWall(int finalRow, int finalCol) {
        if (board.putVerticalWall(client_index, finalRow, finalCol)) {
            sendMessage("STEP " + board);
        }
    }

    public void putHorizontalWall(int finalRow, int finalCol) {
        if (board.putHorizontalWall(client_index, finalRow, finalCol)) {
            sendMessage("STEP " + board);
        }
    }
}
