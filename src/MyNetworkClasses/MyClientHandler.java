/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyNetworkClasses;

import MyClasses.Voucher;
import com.mysql.jdbc.Connection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmalonas
 */
public class MyClientHandler implements Runnable
{
    private final Socket socket;
    
    private final PrintWriter printWriter;
    private final BufferedReader bufferedReader;
    
    private static int connectionCount = 0;
    private final int connectionNumber;

    /**
     * Constructor just initialises the connection to client.
     * 
     * @param socket the socket to establish the connection to client.
     * @param hashMapNames a reference to the lookup table for getting email.
     * @throws IOException if an I/O error occurs when creating the input and
     * output streams, or if the socket is closed, or socket is not connected.
     */
    public MyClientHandler(Socket socket) throws IOException
    {
        this.socket = socket;
        
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        connectionCount++;
        connectionNumber = connectionCount;
        threadSays("Connection " + connectionNumber + " established.");
    }

    /**
     * The run method is overridden from the Runnable interface. It is called
     * when the Thread is in a 'running' state - usually after thread.start()
     * is called. This method reads client requests and processes names until
     * an exception is thrown.
     */
    @Override
    public void run() {
        try
        {
            // Read and process names until an exception is thrown.
            threadSays("Waiting for data from client...");
            ObjectInputStream serverInputStream = new ObjectInputStream(socket.getInputStream());
            Voucher newVoucher = (Voucher)serverInputStream.readObject();
            serverInputStream.close();
            // Connect to DB
            Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/glidingdb","root","");
            // Write to DB
            try
            {
                //Insert to DB
                if (newVoucher.insertToDB(con) > 0)
                {
                    // Confirm everything is fine
                    System.out.println("Voucher succesfully created.");
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(MyClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally
            {
                con.close();
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(MyClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try
            {
                threadSays("We have lost connection to client " + connectionNumber + ".");
                socket.close();
            } catch (IOException ex)
            {
                Logger.getLogger(MyClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Private helper method outputs to standard output stream for debugging.
     * @param say the String to write to standard output stream.
     */
    private void threadSays(String say) {
        System.out.println("ClientHandlerThread" + connectionNumber + ": " + say);
    }
}
