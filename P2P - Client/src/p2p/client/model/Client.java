
package p2p.client.model;

import java.io.Serializable;
import java.net.Socket;

public class Client implements Serializable{
    private String ip, name, port;
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Client(String ip, String name, String port) {
        this.ip = ip;
        this.name = name;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ip=" + this.ip + "#name=" + this.name + "#port=" + this.port;
    }

    @Override
    public boolean equals(Object obj) {
        Client c = (Client) obj;
        return this.ip.equals(c.getIp()) && this.name.equals(c.getName()) && this.port.equals(c.getPort());
    }
}
