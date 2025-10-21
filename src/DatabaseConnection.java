import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
//import javax.swing.table.DefaultTableModel;

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
    
    public static Connection connect(){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite Database!");
        }catch(SQLException e){
            System.out.println("Error Connecting database: " + e.getMessage());
        }
        
        return conn;
    }
    
    public boolean login(String username, String password , JFrame loginFrame){
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try(Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(query)){
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()){
                String role = rs.getString("role");
                if(role.equalsIgnoreCase("admin")){
                    
                    JOptionPane.showMessageDialog(loginFrame, "Login Success");
                    loginFrame.dispose();
                    new adminDashboard(rs.getString("name")).setVisible(true);
                    
                }else{
                    JOptionPane.showMessageDialog(loginFrame, "Login Success");
                    loginFrame.dispose();
                    new librarianDashboard(rs.getString("name")).setVisible(true);
                    }
            }
            
            return rs.next();

        }catch(SQLException e){
            System.out.println("Login Failed" + e.getMessage());
        }
        return false;
    }
    
    public void fetchAllBooks(JTable table){
        String query = "SELECT * FROM books";
        DefaultTableModel bookModel = new DefaultTableModel();
        bookModel.addColumn("Book ID");
        bookModel.addColumn("Book Name");
        bookModel.addColumn("Author");
        bookModel.addColumn("Categories");
        bookModel.addColumn("Quantity");
        
        try (Connection conn = connect();
            Statement stmt = conn.createStatement()){
            
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()){
                int id = rs.getInt("book_id");
                String name = rs.getString("book_name");
                String author = rs.getString("book_author");
                String categories = rs.getString("book_categories");
                int qty = rs.getInt("book_quantity");

                Object[] row = {id, name, author, categories, qty};
                
                bookModel.addRow(row);
            }
            
            table.setModel(bookModel);
            
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void insertAttendance(int userId, String action) {
        String sql = "INSERT INTO attendance (user_id, action, timestamp) VALUES (?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();
        String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, action); // "IN" or "OUT"
            pstmt.setString(3, formattedNow);

            pstmt.executeUpdate();
            System.out.println("Attendance record added successfully.");

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // Retrieve all attendance records
    public static void getAttendanceRecords() {
        String sql = "SELECT * FROM attendance";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getInt("user_id") + " | " +
                    rs.getString("action") + " | " +
                    rs.getString("timestamp")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
