package us.codecraft.wifesays.wife;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
public class WifeSays {

    public static int DEFAULT_PORT = 40310;

    public static String DEFAULT_ADDRESS = "127.0.0.1";

    private int port = DEFAULT_PORT;

    private String address = DEFAULT_ADDRESS;

    private Socket socket;

    private BufferedReader socketReader;

    private BufferedWriter socketWriter;

    private Thread forwardThread;

    private AtomicBoolean connected = new AtomicBoolean(false);

    private Logger logger = Logger.getLogger(getClass());

    /**
     * blockingQueue, make saying be async.
     */
    private BlockingDeque<String> lines = new LinkedBlockingDeque<String>();

    public void say(String line) {
        lines.add(line);
    }

    private void forward(String line) throws IOException {
        socketWriter.write(line);
        socketWriter.newLine();
    }

    private void flush() throws IOException {
        socketWriter.flush();
    }

    public String hear() throws IOException {
        return socketReader.readLine();
    }

    public void connect() throws UnknownHostException, IOException {
        reconnect();
        startForwardThread();
    }

    public void reconnect() throws UnknownHostException, IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(address, port));
        logger.info("reconnnect to " + address + ":" + port + " success ");
        socketReader = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        connected.set(true);
    }

    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    public boolean isConnected() {
        return this.connected.get();
    }

    private void startForwardThread() {
        forwardThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    String line = null;
                    try {
                        line = lines.take();
                    } catch (InterruptedException e) {
                        logger.warn("wtf?!", e);
                    }
                    while (line != null) {
                        try {
                            forward(line);
                            flush();
                        } catch (IOException e) {
                            connected.set(false);
                            try {
                                Thread.sleep(1000);
                                reconnect();
                                lines.addLast(line);
                            } catch (IOException e1) {
                                logger.error("reconnect error", e1);
                            } catch (InterruptedException e1) {
                                logger.warn("wtf?!", e);
                            }
                        }
                        try {
                            line = lines.take();
                        } catch (InterruptedException e) {
                            logger.warn("wtf?!", e);
                        }
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
