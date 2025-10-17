import java.sql.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Bestlink
 */
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:DB/pms.db";
    
    public Connection connect(){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite Database!");
        }catch(SQLException e){
            System.out.println("Error Connecting database: " + e.getMessage());
        }
        
        return conn;
    }
    
    public boolean login(String username, String password){
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try(Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()){
                String role = rs.getString("role");
                if(role.equalsIgnoreCase("admin")){
                    new adminDashboard().setVisible(true);
                    
                }else{
                    new librarianDashboard().setVisible(true);
                    }
            }
            
            return rs.next();
        }catch(SQLException e){
            System.out.println("Login Failed" + e.getMessage());
        }
        return false;
    }
}
