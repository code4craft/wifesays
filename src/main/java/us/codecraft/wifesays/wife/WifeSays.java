package us.codecraft.wifesays.wife;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * @author yihua.huang@dianping.com
 * @date 2012-12-15
 */
public class WifeSays {

	private static int DEFAULT_PORT = 40310;

	private static String DEFAULT_ADDRESS = "127.0.0.1";

	private int port;

	private String address;

	private Socket socket;

	private BufferedReader reader = new BufferedReader(new InputStreamReader(
			System.in));

	private BufferedReader socketReader;

	private PrintWriter socketWriter;

	private String readline() throws IOException {
		return reader.readLine();
	}

	private void putLine(String line) {
		socketWriter.println(line);
		socketWriter.flush();
	}

	private String getResponse() throws IOException {
		return socketReader.readLine();
	}

	private void processResponse() throws IOException {
		String line = null;
		if ((line = getResponse()) != null) {
			System.out.println(line);
		}
	}

	private static void readOptions(CommandLine commandLine, WifeSays wifeSays) {
		if (commandLine.hasOption("f")) {
			String filename = commandLine.getOptionValue("f");
			try {
				wifeSays.reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(filename)));
			} catch (FileNotFoundException e) {
				System.out.println("can't find file " + filename);
			}
		}
		if (commandLine.hasOption("c")) {
			String command = commandLine.getOptionValue("c");
			wifeSays.reader = new BufferedReader(new StringReader(command));
		}
	}

	private void process() throws IOException {
		String line = null;
		while ((line = readline()) != null && line.length() > 0) {
			System.out.println(line);
			putLine(line);
			processResponse();
		}
	}

	private void connect() throws UnknownHostException, IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(address, port));
		System.out.println("connnect to " + address + ":" + port + " success ");
		socketReader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		socketWriter = new PrintWriter(socket.getOutputStream());
	}

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		WifeSays wifeSays = new WifeSays();
		try {
			Options options = new Options();
			options.addOption(new Option("f", true, "input file"));
			options.addOption(new Option("c", true, "simple command"));
			CommandLineParser commandLineParser = new PosixParser();
			CommandLine commandLine = commandLineParser.parse(options, args);
			readOptions(commandLine, wifeSays);
			List<String> argList = commandLine.getArgList();
			if (argList == null || argList.size() < 1) {
				wifeSays.port = DEFAULT_PORT;
				wifeSays.address = DEFAULT_ADDRESS;
			} else {
				wifeSays.address = argList.get(0);
				wifeSays.port = Integer.parseInt(argList.get(1));
			}
			wifeSays.connect();
			wifeSays.process();
		} catch (ParseException e) {
			System.out.println("parse command error " + e);
			System.exit(-1);
		} catch (UnknownHostException e) {
			System.out.println("connnect to " + wifeSays.address + ":"
					+ wifeSays.port + " failed " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("connnect to " + wifeSays.address + ":"
					+ wifeSays.port + " failed " + e);
			System.exit(-1);
		}

	}
}
