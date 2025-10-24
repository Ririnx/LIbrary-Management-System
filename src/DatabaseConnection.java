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
            }else{
                JOptionPane.showMessageDialog(loginFrame, "Login Failed");
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
    
    public void fetchAllLibrarian(JTable table){
        String query = "SELECT * FROM librarians";
        DefaultTableModel librarianModel = new DefaultTableModel();
        librarianModel.addColumn("Librarian ID");
        librarianModel.addColumn("Username");
        librarianModel.addColumn("Full Name");
        librarianModel.addColumn("Password");
        librarianModel.addColumn("Role");
        
        try (Connection conn = connect();
            Statement stmt = conn.createStatement()){
            
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()){
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String name = rs.getString("name");
                String password = rs.getString("password");
                String role = rs.getString("role");

                Object[] row = {id, username, name, password, role};
                
                librarianModel.addRow(row);
            }
            
            table.setModel(librarianModel);
            
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void insertAttendance(int userId, String action) {
        String query = "INSERT INTO attendance (user_id, action, timestamp) VALUES (?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();
        String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

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
        String query = "SELECT * FROM attendance";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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
    
    
    
    public static void addBooks(String title, String author, String category, int quantity){
        String query = "INSERT INTO books (book_name, book_author, book_categories, book_quantity) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, category);
            pstmt.setInt(4, quantity);
            
            pstmt.executeUpdate();
            
        }catch(SQLException e){
            System.out.println("Error adding books." + e.getMessage());
        }
    }
    
    public static void updateBooks(int quantity, int id){
        String query = "UPDATE books SET book_quantity = ? WHERE book_id = ?";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, id);
            
            int rowsUpdate = pstmt.executeUpdate();
            
            if(rowsUpdate > 0){
                System.out.println("Updated Successfully");
            }else{
                System.out.println("Update Failed");
            }
            
        }catch(SQLException e){
            System.out.println("Error adding books." + e.getMessage());
        }
    }
    
    public static void deleteBooks(int id){
        String query = "DELETE FROM books where book_id = ?";
        
        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            
            pstmt.setInt(1, id);
            
            int rowsDelete = pstmt.executeUpdate();
            
            if(rowsDelete > 0){
                JOptionPane.showMessageDialog(null, "Book deleted successfully");
            }else{
                JOptionPane.showMessageDialog(null, "No books found with that ID");
            }
            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error deleting books." + e.getMessage());
        }
    }
    
    public static void addLibrarian(String username, String fullname, String password, String role){
        String query = "INSERT INTO librarians (username, name, password, role) VALUES (?, ?, ?, ?)";
        String query1 = "INSERT INTO users (username, name, password, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            PreparedStatement pstmt1 = conn.prepareStatement(query1);){
            
            pstmt.setString(1, username);
            pstmt.setString(2, fullname);
            pstmt.setString(3, password);
            pstmt.setString(4, role);
            
            pstmt.executeUpdate();
            
            pstmt1.setString(1, username);
            pstmt1.setString(2, fullname);
            pstmt1.setString(3, password);
            pstmt1.setString(4, role);
            
            pstmt1.executeUpdate();
            
        }catch(SQLException e){
            System.out.println("Error creating account." + e.getMessage());
        }
    }
    
    public static void updateLibrarian(String password, String username, int id){
        String query = "UPDATE librarians SET username = ?, password = ? WHERE id = ?";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, id);
            
            int rowsUpdate = pstmt.executeUpdate();
            
            if(rowsUpdate > 0){
                System.out.println("Updated Successfully");
            }else{
                System.out.println("Update Failed");
            }
            
        }catch(SQLException e){
            System.out.println("Error updating librarian." + e.getMessage());
        }
    }
    
    public static void deleteLibrarian(int id){
        String query = "DELETE FROM librarians where id = ?";
        
        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query);){
                
            pstmt.setInt(1, id);
            
            int rowsRemove = pstmt.executeUpdate();
            
            if(rowsRemove > 0){
                JOptionPane.showMessageDialog(null, "Librarian removed successfully");
            }else{
                JOptionPane.showMessageDialog(null, "No librarian found with that ID");
            }    
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error removing librarian." + e.getMessage()); 
        }
    }
        
    public static void searchBooks(JTable table, String keyword){
        DefaultTableModel searchModel = (DefaultTableModel) table.getModel();
        searchModel.setRowCount(0);
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT * FROM books WHERE " + "book_name LIKE ? OR book_author LIKE ? OR book_categories LIKE ?")){
            
            String searchFunction = "%" + keyword.trim() + "%";
            pstmt.setString(1, searchFunction);
            pstmt.setString(2, searchFunction);
            pstmt.setString(3, searchFunction);
            
            ResultSet rs = pstmt.executeQuery();
            boolean found = false;
            
            while (rs.next()){
                found = true;
                searchModel.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("book_author"),
                    rs.getString("book_categories"),
                    rs.getInt("book_quantity")
                });
            }
            
            if (!found) {
                searchModel.addRow(new Object[]{"", "Nothing found", "", "", ""});
            }
                    
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void searchLibrarian(JTable table, String keyword){
        DefaultTableModel searchModel = (DefaultTableModel) table.getModel();
        searchModel.setRowCount(0);
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT * FROM librarians WHERE " + "username LIKE ? OR name LIKE ?")){
            
            String searchFunction = "%" + keyword.trim() + "%";
            pstmt.setString(1, searchFunction);
            pstmt.setString(2, searchFunction);
            
            ResultSet rs = pstmt.executeQuery();
            boolean found = false;
            
            while (rs.next()){
                found = true;
                searchModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("password")
                });
            }
            
            if (!found) {
                searchModel.addRow(new Object[]{"", "Nothing found", "", ""});
            }
                    
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static int bookTotal(){
        String query = "SELECT COUNT(*) AS total FROM books";
        int total = 0;
        
        try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)){
            
            if(rs.next()){
                total = rs.getInt("total");
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error showing all books!" + e.getMessage());
        }
        
        return total;
    }
    
    public static int librarianTotal(){
        String query = "SELECT COUNT(*) AS total FROM librarians";
        int total = 0;
        
        try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)){
            
            if(rs.next()){
                total = rs.getInt("total");
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error showing all librarian!" + e.getMessage());
        }
        
        return total;
    }
    
    public static int borrowedTotal(){
        String query = "SELECT COUNT(*) AS total FROM [borrowed books]";
        int total = 0;
        
        try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)){
            
            if(rs.next()){
                total = rs.getInt("total");
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error showing all borrowed books!" + e.getMessage());
        }
        
        return total;
    }
}