/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyClasses;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author dmalonas
 */
public class Voucher implements Serializable
{
    private String voucherCode;
    private String purchaserName;
    private boolean gift;
    private String recipientName;
    private String deliveryAddress;
    private String emailAddress;
    private String purchaseDate;
    
    public Voucher(String voucherCode, String purchaserName, boolean gift, String recipientName, String deliveryAddress,String emailAddress,String purchaseDate)
    {
        this.voucherCode = voucherCode;
        this.purchaserName = purchaserName;
        this.gift = gift;
        this.recipientName = recipientName;
        this.deliveryAddress = deliveryAddress;
        this.emailAddress = emailAddress;
        this.purchaseDate = purchaseDate;
    }
    
    public int insertToDB(Connection con)
    {
        Statement st = null;
        try
        {
            // Connection to DB
            con = (com.mysql.jdbc.Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/glidingdb","root","");
            st = con.createStatement();
            // Insert voucher data
            int giftBoolToInt = 0; // We use this technique to insert to DB 1/0 insteaf true/false
            if (gift)
                giftBoolToInt = 1;
            String insertStatement = "INSERT INTO `vouchers` (`CODE`, `CUSTOMER_NAME`, `CUSTOMER_EMAIL_ADDRESS`, `PURCHASE_DATE`, `GIFT`, `GIFT_RECIPIENT_NAME`, `REDEEMED`, `FLIGHT_DATE`, `FLIGHT_TYPE`, `COMPLETED`, `FLIGHT_DURATION`, `GLIDER_NUMBER`, `GLIDING_INSTRUCTOR`) "
                    + "VALUES ('" + voucherCode + "', '" + purchaserName + "', '" + emailAddress + "', '" + purchaseDate + "', '" + giftBoolToInt + "', '" + recipientName + "', '0', NULL, NULL, '0', NULL, NULL, NULL)";
            
            st.executeUpdate(insertStatement);
            st.close();
            return 1;
        }
        catch (Exception e)
        {
            // Show error message
            JOptionPane.showMessageDialog(null, "Something went wrong with the voucher creation.", "DB Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        
    }
    

    
}
