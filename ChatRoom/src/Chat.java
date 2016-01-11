import java.io.*;
import java.net.*;
import java.util.concurrent.locks.*;

/**
 * A node that joins a network and sends and receives messages from other nodes.
 * 
 * @author Jeanine
 */
public class Chat {
	public boolean done = false;
	private ServerSocket listener;

	public static void main(String[] args) {

		try {
			System.out.println("My IP Address: " + InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		Chat chat = new Chat();

		// For debugging
		String hostname = "";
		int port = 9090;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		chat.run(hostname, port);
	}

	/**
	 * Main method for the node. Sends messages to and listens messages
	 * from other nodes in the network.
	 * 
	 * @param hostname
	 * @param port
	 */
	public void run(String hostname, int port) {
		try {
			Socket socket = this.setupConnection(hostname, port);
			Thread receiver = new Thread(new Receiver(socket));
			receiver.start();
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Hello world");
			System.out.println("Connected");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			boolean done = false;
			do {
				out.println(br.readLine());
				synchronized (this) {
					done = this.done;
				}
			} while (!done);

			socket.close();
			listener.close();
			System.out.println("Done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Socket setupConnection(String hostname, int port) throws IOException {
		// this.listener = new ServerSocket(port);
		return new Socket(hostname, port);
	}

	public void createNodeNetwork() {

	}

	public void joinNodeNetwork() {

	}

	public void sendString(String text) {

	}

	/**
	 * Receives incoming messages from other nodes in the network.
	 * 
	 * @author Jeanine
	 */
	private class Receiver implements Runnable {
		Socket socket;

		/**
		 * Initializes a new instance of the Receiver class.
		 * @param socket
		 */
		public Receiver(Socket socket) {
			this.socket = socket;
		}

		@Override
		/**
		 * Keeps listening for data from other nodes and executes
		 * the corresponding actions.
		 */
		public void run() {
			try {
				System.out.println("Thread Run");
				BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				String message = in.readLine();
				while (message != "exit") {
					System.out.println("Incoming message: " + message);
				}

				synchronized (Chat.this) {
					Chat.this.done = true;
				}
				System.out.println("Thread exit");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Handles an incoming message from another node.
		 * @param text
		 * @param nodeId
		 */
		public void receiveString(String text, int nodeId) {

		}

		/**
		 * Handles the introduction of a new node to the network.
		 * @param nodeId
		 */
		public void addNewNode(int nodeId) {

		}

	}

}
