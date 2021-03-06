package me.moocar.logbackgelf;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

public class TestTcpServer implements TestServer {

    private final ServerThread serverThread;

    public TestTcpServer(ServerThread serverThread) {
        this.serverThread = serverThread;
    }

    public void start() {
        this.serverThread.start();
    }

    public static TestTcpServer build() throws IOException {
        return new TestTcpServer(new ServerThread(new ServerSocket(12201)));
    }

    public ImmutableMap<String, Object> lastRequest() {
        return serverThread.lastRequest;
    }

    public void shutdown() {
        serverThread.stopServer = true;
    }

    private static class ServerThread extends Thread {

        private final ServerSocket socket;
        private ImmutableMap<String, Object> lastRequest;
        private boolean stopServer = false;
        private static final int maxPacketSize = 8192;
        private final Gson gson;

        public ServerThread(ServerSocket socket) {
            this.socket = socket;
            this.gson = new Gson();
        }

        @Override
        public void run() {
            while (!stopServer) {
                try {
                    Socket accept = socket.accept();
                    ByteBuffer bytes = ByteBuffer.allocate(maxPacketSize);

                    int aByte;

                    while((aByte = accept.getInputStream().read()) != 0) {
                        bytes.put((byte)aByte);
                    }

                    int position = bytes.position();
                    bytes.flip();

                    byte[] message = new byte[position];

                    bytes.get(message);

                    Map map = gson.fromJson(new String(message), Map.class);
                    lastRequest = ImmutableMap.copyOf(map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}