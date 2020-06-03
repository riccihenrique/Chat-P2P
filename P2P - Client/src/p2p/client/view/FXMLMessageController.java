package p2p.client.view;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import p2p.client.model.Client;

public class FXMLMessageController implements Initializable {

    @FXML
    private JFXTextArea txtChat;
    @FXML
    private JFXTextField txtMessage;
    
    private PrintStream ps;
    private BufferedReader br;
    private Client c;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void clkSend(ActionEvent event) {
        txtChat.setText(txtChat.getText() + "Você diz: " + txtMessage.getText() + "\n");
        ps.println(txtMessage.getText());
        txtMessage.clear();
    }
    
    public void setSocket(Client c) {
        this.c = c;
        try {
            ps = new PrintStream(this.c.getSocket().getOutputStream());
            br = new BufferedReader(new InputStreamReader(this.c.getSocket().getInputStream()));
            
            new Thread(() -> {
                while(true) {
                    try {
                        txtChat.setText(txtChat.getText() + (this.c.getName() + " diz: " + br.readLine() + "\n"));
                    } 
                    catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }).start();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
