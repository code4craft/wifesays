package us.codecraft.wifesays.wife;

import org.apache.commons.cli.*;

import java.io.*;
import java.net.UnknownHostException;
import java.util.List;

/**
 * User: cairne
 * Date: 13-5-11
 * Time: 下午6:20
 */
public class Console {

    private WifeSays wifeSays;

    private BufferedReader reader = new BufferedReader(new InputStreamReader(
            System.in));

    public WifeSays getWifeSays() {
        return wifeSays;
    }

    public void setWifeSays(WifeSays wifeSays) {
        this.wifeSays = wifeSays;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        Console console = new Console();
        WifeSays wifeSays = new WifeSays();
        console.setWifeSays(wifeSays);
        try {
            Options options = new Options();
            options.addOption(new Option("f", true, "input file"));
            options.addOption(new Option("c", true, "simple command"));
            CommandLineParser commandLineParser = new PosixParser();
            CommandLine commandLine = commandLineParser.parse(options, args);
            console.readOptions(commandLine);
            List<String> argList = commandLine.getArgList();
            if (argList == null || argList.size() < 1) {
                wifeSays.setPort(WifeSays.DEFAULT_PORT);
                wifeSays.setAddress(WifeSays.DEFAULT_ADDRESS);
            } else {
                wifeSays.setAddress(argList.get(0));
                wifeSays.setPort(Integer.parseInt(argList.get(1)));
            }
            wifeSays.connect();
            console.process();
        } catch (ParseException e) {
            System.out.println("parse command error " + e);
            System.exit(-1);
        } catch (UnknownHostException e) {
            System.out.println("connnect to " + wifeSays.getAddress() + ":"
                    + wifeSays.getPort() + " failed " + e);
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("connnect to " + wifeSays.getAddress() + ":"
                    + wifeSays.getPort() + " failed " + e);
            System.exit(-1);
        }
    }

    private void readOptions(CommandLine commandLine) {
        if (commandLine.hasOption("f")) {
            String filename = commandLine.getOptionValue("f");
            try {
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(filename)));
            } catch (FileNotFoundException e) {
                System.out.println("can't find file " + filename);
            }
        }
        if (commandLine.hasOption("c")) {
            String command = commandLine.getOptionValue("c");
            reader = new BufferedReader(new StringReader(command));
        }
    }

    private void process() throws IOException {
        String line = null;
        while ((line = readline()) != null && line.length() > 0) {
            System.out.println(line);
            wifeSays.say(line);
            processResponse();
        }
    }

    private void processResponse() throws IOException {
        String line = null;
        if ((line = wifeSays.hear()) != null) {
            System.out.println(line);
        }
    }

    public String readline() throws IOException {
        return reader.readLine();
    }
}
