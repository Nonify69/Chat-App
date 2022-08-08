import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    private JLabel heading = new JLabel("Client");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();

    @Override
    public void run() {
        try {

            createGUI();

            client = new Socket("192.168.1.19", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();
                    if (message.equals("/quit")) {
                        out.println(message);
                        inReader.close();
                        shutdown();
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

    }

    private void createGUI() {

        this.setTitle("Client");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());

        this.add(heading, BorderLayout.NORTH);
        this.add(messageArea, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.setVisible(true);

    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

}
