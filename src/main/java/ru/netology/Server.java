package ru.netology;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static ru.netology.Main.PORT;

public class Server extends Thread{

    @Override
    public void run() {
        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress("localhost", PORT));
            System.out.println("Сервер запущен. Порт " + PORT);

            while (true) {
                //  Ждем подключения клиента и получаем потоки для дальнейшей работы
                try (SocketChannel socketChannel = serverChannel.accept()) {
                    //  Определяем буфер для получения данных
                    final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);

                    while (socketChannel.isConnected()) {
                        //  читаем данные из канала в буфер
                        int bytesCount = socketChannel.read(inputBuffer);

                        //  если из потока читать нельзя, перестаем работать с этим клиентом
                        if (bytesCount == -1) break;

                        //  получаем переданную от клиента строку в нужной кодировке и очищаем буфер
                        final String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                        inputBuffer.clear();
                        System.out.println("Получено сообщение от клиента: " + msg);

                        // Выход если от клиента получили end
                        if (msg.equals("end")) {
                            break;
                        }

                        //  отправляем сообщение клиента назад с пометкой ЭХО
                        socketChannel.write(ByteBuffer.wrap(("Ответ: " + removeSpaces(msg)).getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException e) {
                    System.out.println("Server error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private String removeSpaces(String msg) {

        return msg.replaceAll("\\s+", "");
    }
}
