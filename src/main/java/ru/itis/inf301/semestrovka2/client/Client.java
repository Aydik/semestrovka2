package ru.itis.inf301.semestrovka2.client;

import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class Client {
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    // Поле для хранения ID лобби, если нужно
    private int lobbyId;
    // Очередь для входящих сообщений
    private ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();

    // Конструктор по умолчанию (позже можно вызвать connect())
    public Client() { }

    // Конструктор с Socket (используется на серверной стороне при подключении)
    public Client(Socket socket) {
        this.clientSocket = socket;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            System.out.println("Connected to server: " + socket.getInetAddress());
        } catch (IOException e) {
            System.err.println("Error initializing streams: " + e.getMessage());
            closeResources();
        }
    }
    public void startReadingMessages() {
        Thread readerThread = new Thread(() -> {
            try {
                String serverMessage;
                while (true) {
                    serverMessage = in.readLine();
                    if (serverMessage == null) {
                        continue;
                    }
                    // Добавляем полученное сообщение в очередь
                    addMessage(serverMessage);
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


    // Метод для подключения (для клиентской стороны, если используется конструктор без Socket)
    public void connect() {
        try {
            clientSocket = new Socket("localhost", 50000);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            System.out.println("Connected to server");
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            closeResources();
        }
    }

    // Отправка сообщения на сервер
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

    // Чтение сообщения из очереди (сообщения должны добавляться извне, например, сервером)
    public String readMessage() {
        String message = messageQueue.poll();
        if (message != null) {
            message = message.replace("[Server]: ", "");

        }
        return message;
    }

    // Метод для добавления входящего сообщения (вызывается, например, сервером через ClientHandler)
    public void addMessage(String message) {
        messageQueue.add(message);
    }

    public void closeResources() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
