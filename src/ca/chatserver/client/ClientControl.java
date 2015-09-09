/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.chatserver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Scanner;

/**
 *
 * @author LukaszKrawczyk
 */
public class ClientControl implements Runnable {

    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    private ClientForm clientForm;
    private String userName;

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    }

    public ClientControl(String address, int port, ClientForm clientForm, String userName) throws IOException {
        this.clientForm = clientForm;
        this.userName = userName;
        connect(address, port);
    }

    public void run() {
        output.println(Protocol.USER + userName);
        while (true) {
            String msg = input.nextLine();
            if (msg.contains(Protocol.USERLIST)) {
                String userlist = msg.substring(Protocol.USERLIST.length());
                userlist = userlist.replace(",", "\n");
                clientForm.UpdateUserList(userlist);
            } else if (msg.contains(Protocol.MSG)) {
                String message = msg.substring(Protocol.MSG.length());
                String[] messageArr = message.split("[#]");
                String sender = messageArr[0];
                message = messageArr[1];
                clientForm.AddToChat(sender + ": " + message);
            }
        }
    }

    public void Disconnet() {
        output.println(Protocol.STOP);
    }
    
    public void SendToAll(String msg){
        output.println(Protocol.MSG + "*#" + msg);
    }

}
