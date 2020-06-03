package chat.p2p.server.view;

import chat.p2p.server.model.Client;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLServerController implements Initializable {

    @FXML
    private JFXButton btnStart;
    @FXML
    private TableView<Client> tableClients;
    @FXML
    private TableColumn<Client, String> colIp;
    @FXML
    private TableColumn<Client, String> colName;
    @FXML
    private TableColumn<Client, String> colPort;
    
    private ServerSocket server;
    private Socket client;
    private Thread tWaitConnection;
    private boolean running = false;
    private ServerFuncions serverFuncions;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIp.setCellValueFactory(new PropertyValueFactory("ip"));
        colName.setCellValueFactory(new PropertyValueFactory("name"));
        colPort.setCellValueFactory(new PropertyValueFactory("port"));        
        
        serverFuncions = new ServerFuncions();
        try {
            server = new ServerSocket(12345);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML
    private void clkStart(ActionEvent event) {
        if(this.btnStart.getText().equals("Start")) {
            this.btnStart.setText("Close");
            this.running = true;
            if(tWaitConnection == null) {
                new Thread(() -> {
                    while(running) {
                        try {
                            System.out.println("Wait for a client connect");
                            client = server.accept();
                            new Thread(() -> {                                
                                while(true) {
                                    serverFuncions.execute(client);
                                    tableClients.setItems(FXCollections.observableArrayList(serverFuncions.waitClient()));
                                }
                            }).start();
                        }
                        catch(IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }).start();
            }
        }            
        else {
            this.btnStart.setText("Start");
            this.running = false;
        }
    }
}
