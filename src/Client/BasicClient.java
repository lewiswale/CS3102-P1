package Client;

import java.io.*;
import java.net.Socket;

public class BasicClient {

    public final static int PORT_NUMBER = 4242;
    public final static String SERVER = "138.251.29.25";

    /**
     * Main method used to connect to server and request file
     * @param args args[0] is the code for which file you want a copy of.
     */
    public static void main(String[] args) {
        String fileToReceive;
        int fileCode = Integer.parseInt(args[0]);
        switch (fileCode) {
            case 0: fileToReceive = "/cs/scratch/ljw26/received.txt";
                break;
            case 1: fileToReceive = "/cs/scratch/ljw26/received.mp4";
                break;
            case 2: fileToReceive = "/cs/scratch/ljw26/received.xml";
                break;
            default: fileToReceive = "";
        }

        FileOutputStream outputStream;
        BufferedOutputStream buffer;
        Socket socket;

        try {
            System.out.println("Connecting...");
            socket = new Socket(SERVER, PORT_NUMBER);
            System.out.println("Connected to " + SERVER + " on port " + PORT_NUMBER);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeInt(fileCode);                                                     //Sending file code to server
            oos.flush();

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            int fileSize = inputStream.readInt();
            byte[] bytes = new byte[fileSize];

            outputStream = new FileOutputStream(fileToReceive);
            buffer = new BufferedOutputStream(outputStream);

            System.out.println("Receiving File...");
            int count;
            while ((count = inputStream.read(bytes)) >= 0) {
                buffer.write(bytes, 0, count);
            }

            buffer.flush();
            System.out.println("File received.");

            if (outputStream != null) outputStream.close();
            if (buffer != null) buffer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
