package network;

import sun.nio.cs.UTF_8;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThead;
    private  final TCPConnectionListenner eventListEner;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListenner eventListEner, String ipAddr, int port) throws IOException{
        this(eventListEner,new Socket(ipAddr, port));
    }


    public TCPConnection(TCPConnectionListenner eventListEner, Socket socket) throws IOException {
        this.eventListEner = eventListEner;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThead = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    eventListEner.onConnectionReady(TCPConnection.this);
                    while (!rxThead.isInterrupted()){
                        eventListEner.onReceiveString(TCPConnection.this,in.readLine());
                    }
               String msg = in.readLine();
            } catch (IOException e){
                    eventListEner.onException(TCPConnection.this, e);
                }finally {
                   eventListEner.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThead.start();
    }

    public synchronized void sendstring(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListEner.onException(TCPConnection.this, e);
            disconect();
        }

    }

    public synchronized void disconect(){
        rxThead.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListEner.onException(TCPConnection.this, e);
        }

    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
