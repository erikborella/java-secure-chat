package multicast;

import crypto.AESUtils;
import crypto.BinaryUtils;
import interfaces.ICallback;
import interfaces.ICallbackException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receiver {
    private final MulticastSocket socket;
    private final InetAddress groupAddress;
    
    private final byte[] key;
    
    private byte[] buf = new byte[5000];
        
    private ICallback callback;
    private ICallbackException callbackException;
    
    private Thread listenerThread;
    private boolean running = true;
    
    private class Listener implements Runnable {

        @Override
        public void run() {
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    
                    if (!running)
                        return;
                    
                    String received = new String(
                        packet.getData(), 0, packet.getLength()
                    );
                    
                    String decryptedReceived = AESUtils.decrypt(received, key);

                    callback.run(decryptedReceived);
                } catch (Exception e) {
                    callbackException.run(e);
                }
            }
        }
        
    }

    public Receiver(String address, int port, String key) 
            throws IOException{
        this.socket = new MulticastSocket(port);
        this.groupAddress = InetAddress.getByName(address);
        
        this.key = BinaryUtils.toByteArray(key);
        
        socket.joinGroup(groupAddress);
    }
    
    public void startListen(ICallback callback, ICallbackException callbackException) {
        this.callback = callback;
        this.callbackException = callbackException;
        
        this.listenerThread = new Thread(new Listener());
        listenerThread.start();
    }
    
    public void destroy() {
        this.running = false;
    }
}
