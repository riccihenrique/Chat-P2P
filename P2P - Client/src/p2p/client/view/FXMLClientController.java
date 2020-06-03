package p2p.client.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import p2p.client.model.Client;

public class FXMLClientController implements Initializable {
    
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextField txtPort;
    @FXML
    private JFXButton btConnect;
    @FXML
    private TableView<Client> tableClients;
    @FXML
    private TableColumn<Client, String> colName;
    @FXML
    private TableColumn<Client, String> colIp;
    @FXML
    private TabPane tbpChats;
    
    private ServerSocket server;
    private Socket socket;
    private Socket socketServer;
    private boolean run = false;
    private Thread twaitConnection;
    private Runnable waitConnection;
    private ClientFunctions clientFunctions;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIp.setCellValueFactory(new PropertyValueFactory("ip"));
        colName.setCellValueFactory(new PropertyValueFactory("name"));        
        
        clientFunctions = new ClientFunctions();
        
        waitConnection = () -> {
            try {
                server = new ServerSocket(Integer.parseInt(txtPort.getText()));
                while(run) {
                    try {
                        Socket socketClient = server.accept();
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                Client c = clientFunctions.getClientByIp(socketClient.getInetAddress().getHostAddress(), socketClient.getLocalPort() + "");
                                c.setSocket(socketClient);
                                int index = clientFunctions.isChatOpen(c);
                                if(index == -1) {
                                    Tab t = new Tab(c.getName() + " - " + c.getIp());
                                    try {
                                        clientFunctions.addChat(c);
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMessage.fxml"));
                                        Parent root = (Parent) loader.load();
                                        FXMLMessageController control = loader.getController();
                                        control.setSocket(c);

                                        t.setContent(root);

                                        tbpChats.getTabs().add(t);
                                    }
                                    catch(IOException e) {

                                    }
                                }
                            }
                        });
                    }
                    catch(IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } 
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        };
        twaitConnection = new Thread(waitConnection);
    }    

    @FXML
    private void clkConnect(ActionEvent event) {
        try {
            Client c = new Client("", txtName.getText(), txtPort.getText());
            if(btConnect.getText().equals("Connect")) {                
                socketServer = new Socket("localhost", 12345);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            clientFunctions.connectServer(socketServer, c);
                            
                            btConnect.setText("Disconnect");
                            run = true;
                            twaitConnection.start();
                            while(clientFunctions.getClients() != null)
                                tableClients.setItems(FXCollections.observableArrayList(clientFunctions.waitServer()));
                        }
                }).start();
            }
            else {
                btConnect.setText("Connect");
                tableClients.getItems().clear();
                clientFunctions.disconnectServer();
                tbpChats.getTabs().clear();
                this.run = false;
            }
        }
        catch(IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("O servidor não está disponível");
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void clkClient(MouseEvent event) throws IOException {
        Client c = tableClients.getSelectionModel().getSelectedItem();
        if(c.getName().equals(txtName.getText()) && c.getPort().equals(txtPort.getText())) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Erro");
            a.setContentText("Você não pode conversar com você mesmo!!");
            a.showAndWait();
            return;
        }
            
        if(c != null) {
            int index = clientFunctions.isChatOpen(c);
            if(index == -1) {
                Tab t = new Tab(c.getName() + " - " + c.getIp());
                try {
                    Socket s = new Socket(c.getIp(), Integer.parseInt(c.getPort()));
                    c.setSocket(s);
                    clientFunctions.addChat(c);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMessage.fxml"));
                    Parent root = (Parent) loader.load();
                    FXMLMessageController control = loader.getController();
                    control.setSocket(c);

                    t.setContent(root);

                    tbpChats.getTabs().add(t);
                }
                catch(IOException | NumberFormatException e) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setHeaderText("O cliente não pode mais se conectar");
                    a.showAndWait();
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
