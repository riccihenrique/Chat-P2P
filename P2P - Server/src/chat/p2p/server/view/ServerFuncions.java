package chat.p2p.server.view;

import chat.p2p.server.model.Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerFuncions {
    
    private List<Client> clients = new ArrayList<>();
    private List<Socket> sockets = new ArrayList<>();

    public List<Client> getClients() {
        return clients;
    }
    
    private void connect(String[] cmd, Socket s) {
        Client c = new Client(s.getInetAddress().getHostAddress(), cmd[1].split("=")[1], cmd[2].split("=")[1]);
        sockets.add(s);
        clients.add(c);
        
        sendUsers(true, c);
    }
    
    public void execute(Socket s) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String strCon = br.readLine();
            String[] cmd = strCon.split("#");
            if(cmd[0].equals("@connect"))
                connect(cmd, s);
            else if(cmd[0].equals("@disconnect"))
                disconnect(cmd);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void disconnect(String[] cmd) {        
        int i = 0;
        while(i < clients.size())
            if(clients.get(i).getIp().equals(cmd[1].split("=")[1]) && clients.get(i).getPort().equals(cmd[3].split("=")[1]))
                break;
            else
                i++;
        
        Client c = clients.remove(i);
        sockets.remove(i);
        sendUsers(false, c);
    }
    
    private String convertClients() {
        String s = "";
        for(Client c : clients)
            s += c + ":";
        return s;
    }

    private void sendUsers(boolean newUser, Client c) {
        String users = convertClients();
        System.out.println(users);
        System.out.println("Qtd de usuários: " + clients.size());
        for(int i = 0; i < clients.size(); i++)
        {
            try {
                PrintStream ps = new PrintStream(sockets.get(i).getOutputStream());
                ps.println(newUser ? "@newUser#" : "@removeUser#" + c);
                ps.println(users);
            }
            catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }    
        
    public List<Client> waitClient() {
        return this.clients;
    }
}
