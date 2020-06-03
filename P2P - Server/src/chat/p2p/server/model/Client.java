
package chat.p2p.server.model;

import java.io.Serializable;

public class Client implements Serializable{
    private String ip, name, port;

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
}
