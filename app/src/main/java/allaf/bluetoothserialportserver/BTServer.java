package allaf.bluetoothserialportserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class BTServer extends Thread {
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private static final String SERVER_NAME = "BT Serial Port";
    private static final UUID BASE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean serverLoop = false;
    private boolean receiveLoop = false;

    private InputStream inStream;
    private OutputStream outStream;

    public Queue<String> messageQueue;

    public BTServer() {
        inStream = null;
        outStream = null;
        BluetoothServerSocket tmp = null;
        messageQueue = new LinkedList<>();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, BASE_UUID);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        serverSocket = tmp;
    }

    public void run() {
        BluetoothSocket tmpSocket = null;
        serverLoop = true;
        receiveLoop = false;
        while(serverLoop) {
            tmpSocket = acceptConnection(tmpSocket);
            if(tmpSocket != null) {
                receiveLoop = true;
                socket = tmpSocket;
                setIOStreams();
                receiveingLoop();
                closeConnection();
            }
        }
        closeServerSocket();
    }

    private void receiveingLoop() {
        byte[] buffer = new byte[256];
        int bytes;
        String message;
        while(receiveLoop) {
            try {
                bytes = inStream.read(buffer);
                String receivedData = new String(buffer, Charset.defaultCharset());
                message = receivedData.substring(0,bytes);
                if(!message.isEmpty())
                    messageQueue.add(message);
            } catch (IOException e) {
                e.printStackTrace();
                receiveLoop = false;
            }
        }
    }

    private void closeConnection() {
        try {
            socket.close();
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setIOStreams() {
        InputStream tmpInStream = null;
        OutputStream tmpOutStream = null;
        try {
            tmpInStream = socket.getInputStream();
            tmpOutStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inStream = tmpInStream;
        outStream = tmpOutStream;
    }

    private BluetoothSocket acceptConnection(BluetoothSocket tmpSocket) {
        try {
            tmpSocket = this.serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpSocket;
    }

    private void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        serverLoop = false;
        receiveLoop = false;
    }
}
