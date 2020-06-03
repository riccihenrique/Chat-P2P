package p2p.client.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import p2p.client.model.Client;

public class ClientFunctions {
    private List<Client> clients, openChats;
    private PrintStream ps;
    private BufferedReader br;
    private Socket s;
    private Client c;
    
    public List<Client> getClients() {
        return clients;
    }
    
    public void connectServer(Socket s, Client cli) {
        try {
            this.s = s;
            clients = new ArrayList<>();
            openChats = new ArrayList<>();
            ps = new PrintStream(this.s.getOutputStream());
            ps.println("@connect#nome=" + cli.getName() +  "#port=" + cli.getPort());
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void disconnectServer() {
        ps.println("@disconnect#ip=" + c.getIp() + "#nome=" + c.getName() + "#ip=" + c.getPort());
        clients = null;
    }

    public List<Client> waitServer() {
        try {
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String serverResp = br.readLine();
            String[] splitMessage = serverResp.split("#");
            if(splitMessage[0].equals("@newUser")) {
                attClients(true);
            }
            else if(splitMessage[0].equals("@removeUser")) {
                attClients(false);
            }
            else {
                // Any other implementation
            }
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
        return clients;
    }
    
    private void removeByIp(Client cli, List<Client> l) {
        for(Client c : l)
            if(c.equals(cli))
                l.remove(c);
    }
    
    private void attClients(boolean isNew) {
        try {
            String response = br.readLine();
            List<Client> l = clients;
            clients.clear();
            String[] clis = response.split(":");
            for(int i = 0; i < clis.length; i++) {
                String[] cli = clis[i].split("#");
                Client c = new Client(cli[0].split("=")[1], cli[1].split("=")[1], cli[2].split("=")[1].replace("^", ""));
                if(!isNew)
                    removeByIp(c, l);
                
                clients.add(c);
            }
            
            if(isNew) {
                // shoow msg entrou
                if(c == null) {
                    c = clients.get(clients.size() - 1);
                }
            }
            else {
                // show msg saiu
            }  
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public int isChatOpen(Client c) {
        for(int i = 0; i < openChats.size(); i++)
            if(openChats.get(i).equals(c))
                return i;
        return -1;
    }
    
    public void addChat(Client c) {
        openChats.add(c);
    }

    public Client getClientByIp(String hostAddress, String port) {
       for(Client c : clients) {
           if(c.getIp().equals(hostAddress) && !c.getPort().equals(port))
               return c;
       }
       return null;
    }
}
