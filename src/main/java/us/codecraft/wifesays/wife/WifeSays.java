package us.codecraft.wifesays.wife;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
public class WifeSays {

    public static int DEFAULT_PORT = 40310;

    public static String DEFAULT_ADDRESS = "127.0.0.1";

    private int port;

    private String address;

    private Socket socket;

    private BufferedReader socketReader;

    private PrintWriter socketWriter;

    private Thread forwardThread;

    /**
     * blockingQueue, make saying be async.
     */
    private BlockingQueue<String> lines = new LinkedBlockingDeque<String>();

    public void say(String line) {
        lines.add(line);
    }

    private void forward(String line) {
        socketWriter.println(line);
    }

    private void flush() {
        socketWriter.flush();
    }

    public String hear() throws IOException {
        return socketReader.readLine();
    }

    public void connect() throws UnknownHostException, IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(address, port));
        System.out.println("connnect to " + address + ":" + port + " success ");
        socketReader = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        socketWriter = new PrintWriter(socket.getOutputStream());
        startForwardThread();
    }

    private void startForwardThread() {
        forwardThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String line;
                        while ((line = lines.take()) != null) {
                            forward(line);
                            if (lines.isEmpty()) {
                                flush();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        forwardThread.setDaemon(true);
        forwardThread.start();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
