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
	private PrintWriter writer;
	private ServerSocket listener;
	public static String EVENT_MESSAGE = "MESSAGE";
	public static String EVENT_NODE_JOIN = "JOIN";

	public static void main(String[] args) {
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
			this.listener = new ServerSocket(port);
		} catch (BindException be) {
			// Do nothing.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			// Set up connections.
			Socket socket = new Socket(hostname, port);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			// Start the receiver thread.
			new Thread(new Receiver(socket)).start();
			this.writer = new PrintWriter(socket.getOutputStream(), true);
			
			// Continue sending messages until the node leaves the network.
			System.out.println("Enter a message.");
			System.out.println("Type 'exit' to exit.");
			while (true) {
				String message = br.readLine();
				
				if (message.compareTo("exit") == 0) {
					break;
				}
				this.sendString(message);
			}

			// Safely close connections.
			socket.close();
			this.listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends message to the network.
	 * @param text
	 */
	public void sendString(String text) {
		this.writer.println(EVENT_MESSAGE);
		this.writer.println(text);
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
				BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				String event;
				boolean done = false;
				do {
					event = reader.readLine();
					if (event.matches(EVENT_MESSAGE)){
						// TODO: Read the message.
						String author = reader.readLine();
						String message = reader.readLine();
						this.receiveString(message, author);
					} else if (event.matches(EVENT_NODE_JOIN)) {
						// TODO: Announce the new node.
					}
					
					synchronized(Chat.this) {
						done = Chat.this.done;
					}
				} while (!done);
			} catch (IOException e) {
				if (e.getMessage().matches("Socket closed")) {
					return;
				}
				
				e.printStackTrace();
			}
		}

		/**
		 * Handles an incoming message from another node.
		 * @param text
		 * @param nodeId
		 */
		public void receiveString(String text, String author) {
			System.out.println(author + " says: " + text);
		}

		/**
		 * Handles the introduction of a new node to the network.
		 * @param nodeId
		 */
		public void addNewNode(String name) {
			System.out.println(name + " has joined.");
		}

	}

}
