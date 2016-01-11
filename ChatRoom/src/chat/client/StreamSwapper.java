package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes an input stream and an output stream. Writes the input stream into the
 * output stream.
 * 
 * @author Jack Galilee
 * @author Jeanine Bonot
 */
public class StreamSwapper extends Thread {

	private String masterString;
	// Stream we are going to read into the output stream.
	private InputStream inputStream;

	// Stream we are going to write the input stream into.
	private OutputStream outputStream;

	/**
	 * Constructs the stream swapper.
	 * 
	 * @param input
	 *            Stream to get data from.
	 * @param output
	 *            Stream to send data to.
	 * @throws IOException
	 */
	public StreamSwapper(InputStream input, OutputStream output) throws IOException {
		this.inputStream = input;
		this.outputStream = output;
		this.masterString = "";
	}

	/**
	 * Buffers the input stream and writes it to the output stream.
	 */
	public void run() {

		// Create a reader for the input stream.
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));

		// Create a writer for the output stream.
		PrintWriter outputWriter = new PrintWriter(outputStream, true);
		Pattern pattern = Pattern.compile(".*: (.*)");
		// Buffer each new line from the reader and write it to the output
		// stream.
		try {
			String lineBuffer;
			while ((lineBuffer = inputReader.readLine()) != null) {
				Matcher matcher = pattern.matcher(lineBuffer);
				if (matcher.find()) {
					this.masterString = matcher.group(1);
					outputWriter.println(this.masterString);
				} else {
					outputWriter.println(lineBuffer);
				}
			}

			// Close the input reader and output writer when we're finished.
			inputReader.close();
			outputWriter.close();

			// Handle any exceptions with the IO data streams.
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}