package domain;

public class MulticastConnection {
    public final String address;
    public final int port;
    public final String key;

    public MulticastConnection(String address, int port, String key) {
        this.address = address;
        this.port = port;
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getKey() {
        return key;
    }
    
    
}
