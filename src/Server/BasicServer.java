package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BasicServer implements Runnable{
    /**
     * Constructor for Server thread.
     * @param socket client requesting file.
     */
    public BasicServer(Socket socket) {
        this.socket = socket;
    }

    public Socket socket;
    public final static int PORT_NUMBER = 4242;

    /**
     * File transfer method used by each thread.
     */
    @Override
    public void run() {
        System.out.println("Connection established : " + socket);

        FileInputStream inputStream;
        BufferedInputStream buffer;
        OutputStream outputStream;

        try {
            outputStream = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outputStream);

            //Receiving desired file code
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            int fileNumber = ois.readInt();

            String filePath;
            switch (fileNumber) {
                case 0: filePath = "/cs/scratch/ljw26/Set of 3 Files/pg44823.txt";
                    break;
                case 1: filePath = "/cs/scratch/ljw26/Set of 3 Files/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4";
                    break;
                case 2: filePath = "/cs/scratch/ljw26/Set of 3 Files/cawiki-20140129-stub-articles.xml";
                    break;
                default: filePath = "";
            }

            System.out.println("Finding file");
            File fileToSend = new File(filePath);
            System.out.println("Found file!");

            //Sending size of file to client
            int fileSize = (int) fileToSend.length();
            dos.writeInt(fileSize);
            dos.flush();

            byte[] bytes = new byte[8192];
            inputStream = new FileInputStream(fileToSend);
            buffer = new BufferedInputStream(inputStream);

            System.out.println("Sending file");

            //Sending file in chunks of size 8192 to avoid filling heap
            int count;
            while ((count = buffer.read(bytes)) >= 0) {
                dos.write(bytes, 0, count);
            }

            dos.flush();
            dos.close();
            System.out.println("File sent.");

            if (buffer != null) buffer.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();

        } catch (IOException e) {
            e.getStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * Main method instantiates listening loop
     * @param args args[0] is the amount of clients the server should wait for
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket ss = null;
        Socket socket;
        int count = 0;
        int nodeAmount = Integer.parseInt(args[0]);
        ArrayList<Thread> threads = new ArrayList<Thread>();

        try {
            ss = new ServerSocket(PORT_NUMBER);
            while (true) {
                System.out.println("Waiting...");

                socket = ss.accept();
                System.out.println("connected to:" + socket);
                Runnable server = new BasicServer(socket);      //Creates new server object with the client socket
                threads.add(new Thread(server));                //Adds thread object to list
                count++;
                if (count == nodeAmount) {                      //If enough clients are connected, starts threads
                    long start = System.currentTimeMillis();
                    for (int i = 0; i < count; i++) {
                        threads.get(i).start();
                    }

                    for (int i = 0; i < count; i++) {           //Waits until all threads have terminated.
                        threads.get(i).join();
                    }

                    System.out.println("Time taken: " + (System.currentTimeMillis() - start));     //Prints time
                    if (ss != null) ss.close();
                    System.exit(0);
                }
            }
        }
        finally {
            if (ss != null) ss.close();
        }
    }
}
