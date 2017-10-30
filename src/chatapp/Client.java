
package chatapp;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class Client{

    
    private int port;
    String username;
    String address;
    JTextField jTextFieldUsername;
    JTextField jTextFieldIP;
    JTextField jTextFieldPort;
    JTextArea area_output;
    JTextArea area_input;
    JButton button;
    String message="";
    
    DataInputStream input;
    DataOutputStream output;
    Socket socket;
    
    Client(){  
        constructFrame();
    }
    public void constructFrame(){
        JFrame frame = new JFrame("Chat App");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 380);         
        
        JPanel panel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);      
        
        
        
        JLabel jLabelUsername = new JLabel("Usuario:");
        panel.add(jLabelUsername, c);       
        jTextFieldUsername = new JTextField("beni");
        panel.add(jTextFieldUsername, c);
        c.insets = new Insets(10,5,10,5);
        
        JLabel jLabelIP = new JLabel("IP:");
        panel.add(jLabelIP, c);       
        jTextFieldIP = new JTextField("127.0.0.1");//34.210.252.187
        panel.add(jTextFieldIP, c);        
        
        JLabel jLabelPort = new JLabel("Puerto:");
        panel.add(jLabelPort, c);       
        jTextFieldPort = new JTextField("2121");
        jTextFieldPort.setEditable(false);
        panel.add(jTextFieldPort, c);   
        
        button = new JButton();
        button.setText("Conectar");
        panel.add(button, c);     
        
        button.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!jTextFieldUsername.getText().isEmpty()){
                    if(button.getText().equals("Conectar")){
                        username = jTextFieldUsername.getText();
                        address = jTextFieldIP.getText();
                        port = Integer.parseInt(jTextFieldPort.getText());              
                        start();
                        button.setText("Desconectar");
                    }else{                       
                            writeMessage("exit");
                            area_output.append("\n Desconectado \n \n");
                            button.setText("Conectar");
                    }
                }else{
                    area_output.append("Ingrese su usuario! \n");
                }
            }
        });
        
        JPanel panel1 = new JPanel(new GridBagLayout());
                
        c.insets = new Insets(5,0,5,0);
        
        area_output = new JTextArea(10,40);
        DefaultCaret caret = (DefaultCaret)area_output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scroll = new JScrollPane(area_output);        
        panel1.add(scroll, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = WEST;
               
        area_input = new JTextArea(2,30);
        area_input.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    e.consume();
                    writeMessage(area_input.getText());
                }              
            }
            @Override
            public void keyReleased(KeyEvent e) {               
            }
            @Override
            public void keyTyped(KeyEvent e) {               
            }            
            
        });
        JScrollPane scroll_1 = new JScrollPane(area_input);              
        panel1.add(scroll_1, c);    
        
        c.anchor = EAST;
        JLabel image = new JLabel(new ImageIcon("C:\\Users\\android-hw\\Documents\\NetBeansProjects\\ChatApp\\src\\images\\chat_icon.png"));
        panel1.add(image, c);
        
        frame.add(panel, BorderLayout.NORTH);
        frame.add(panel1, BorderLayout.CENTER);
        frame.setVisible(true);
        
        
        //on exit event
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {                
                    writeMessage("exit");                    
                
            }
        });
              
    }
    
    public void start(){
        
        area_output.append("Connecting to server... \n");
        try {
            socket = new Socket(address, port);
            area_output.append("Connection successful! \n");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            area_output.append("Connection failed! \n");
        }
        
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }       
        
        writeMessage(username);
        
        ListenFromServer l = new ListenFromServer();
        
        area_output.append("You can start chatting! \n");       
        
    }        
    
        public void writeMessage(String message){
          
           byte size = (byte)message.length();
        try {
            output.writeByte(size);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.write(message.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        area_input.setText("");
        } 
    public String readMessage(){
                      
            byte[] messageByte = new byte[1000];
            boolean end = false;
            String messageString = "";

            try 
            {               
                int bytesRead = 0;
                messageByte[0] = input.readByte();
                //messageByte[1] = input.readByte();
                
                int bytesToRead = messageByte[0];
                //int bytesToRead = messageByte[1];

                while(!end)
                {
                    bytesRead = input.read(messageByte);
                    messageString += new String(messageByte, 0, bytesRead);
                    if (messageString.length() == bytesToRead )
                    {
                        end = true;
                    }
                }
                System.out.println("MESSAGE: " + messageString);
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return messageString;
        }
    
    
    public static void main(String args[]){
        
        Client client = new Client();
        
    }
    
    class ListenFromServer extends Thread{
        
        ListenFromServer(){
            start();
        }
        
            public void run(){
                while(true){                                                                                                         
                                area_output.append("\n >> "+readMessage());
                            }
                                                                                               
                        //area_output.append("\n >> "+input.readUTF());
                                    
            }
        
    }
       
    
    
}
