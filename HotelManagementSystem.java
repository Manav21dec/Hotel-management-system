package manav;

import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class HotelReservationSystem {
private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
private static final String username = "root";
private static final String password ="123456789";
public static void main(String args[]) throws ClassNotFoundException, SQLException{
	try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		}catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	try {
		Connection connection = DriverManager.getConnection(url, username, password);
		while(true) {
			System.out.println();
			System.out.println("HOTEL MANAGEMENT SYSTEM");
			Scanner scanner = new Scanner(System.in);
			System.out.println("1. Reserve a room");
			System.out.println("2. View Reservation");
			System.out.println("3. Get Room Number");
			System.out.println("4. Update Reservation");
			System.out.println("5. Delete Reservation");
			System.out.println("0. Exit");
			System.out.println("Choose an option");
			int choice =scanner.nextInt();
			switch(choice) {
			case 1:
				reserveRoom(connection, scanner);
				break;
			case 2:
				viewreservation(connection);
				break;
			case 3: 
				getRoomNumber(connection, scanner);
				break;
			case 4:
				updateReservation(connection, scanner);
				break;
			case 5:
				deleteReservation(connection, scanner);
				break;
			case 0:
				exit();
				scanner.close();
				return;
			default:
				System.out.println("Invalid choice. try again.");
			}
			
		}
	   }catch(SQLException e) {
		   System.out.println(e.getMessage());
	   }catch(InterruptedException e) {
		   throw new RuntimeException(e);  
	   }
   }

   private static void reserveRoom(Connection connection, Scanner scanner) {
	   try {
	   System.out.print("Enter guest name: ");
	   String guestName = scanner.next();
	   scanner.nextLine();
	   System.out.println("Enter Room number");
	   int roomNumber = scanner.nextInt();
	   System.out.println("Enter contact number");
	   String contactNumber = scanner.next();
	   
	   String sql = "INSERT INTO reservations(guest_name, room_number, contact_number)" + "Values('" + guestName + "', " + roomNumber + " '" + contactNumber + "')";
	   
	   try(Statement statement = connection.createStatement()){
		   int affectedRows = statement.executeUpdate(sql);
		   
		   if(affectedRows > 0) {
			   System.out.println("Reservation successful!");
		   }else {
			   System.out.println("Reservation failed");
		   }
	   }
	   
	}catch(SQLException e) {
		e.printStackTrace();
	}
   }
   
   private static void viewreservation(Connection connection) throws SQLException{
	   String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
   
   
 try(Statement statement = connection.createStatement();
		 ResultSet resultset = statement.executeQuery(sql)){
     System.out.println("Current reservation:");
     System.out.println("+---------------+---------------+---------------+----------------+--------------------+");
     System.out.println("| Reservation ID| Guest         | Room Number   | Contact number | Reservation time   |");
     System.out.println("+---------------+---------------+---------------+----------------+--------------------+");
     
     while(resultset.next()) {
    	 int reservationId = resultset.getInt( "reservation_id");
    	 String guestName = resultset.getString("guest_name");
    	 String roomNumber = resultset.getString("room_number");
    	 String contactNumber = resultset.getString("contact_number");
    	 String reservationDate = resultset.getTimestamp("reservation_date").toString();
    	 
    	 System.out.printf("|%-14d | %-15s | %-13d | %-20s | %-19s |\n", reservationId, guestName, roomNumber, contactNumber, reservationDate );
    	 }
  
     System.out.println("+---------------+---------------+---------------+----------------+--------------------+");
     
      }
   }
   
   
   private static void getRoomNumber(Connection connection, Scanner scanner) {
	   try {
		   System.out.println("Enter reservation ID");
		   int reservationId = scanner.nextInt();
		   System.out.println("Enter guest name:");
		   String guestName = scanner.next();
		   
		   String sql = "Select room_number FROM reservations" + "WHERE reservation_id = " + reservationId + "AND guest_name = '" + guestName + "'";
		   try(Statement statement = connection.createStatement();
				   ResultSet resultset = statement.executeQuery(sql)){
			   if(resultset.next()) {
				   int roomNumber = resultset.getInt("room_number");
				   System.out.println("Room number for reservation ID" + reservationId + "and Guest" + guestName + "is:" + roomNumber);
				   } else {
					   System.out.println("Reservation not found for the given ID and guest name");
		   }
	   }
   }catch(SQLException e) {
	   e.printStackTrace();
   }
 }
   private static void updateReservation(Connection connection, Scanner scanner) {
	   try {
		   System.out.println("Enter reservation ID to update");
		   int reservationId = scanner.nextInt();
		   scanner.nextLine();
		   
		   if(!reservationExists(connection, reservationId)) {
			   System.out.println("Reservation not found for the given ID");
			   return;
			   }
		   
		   System.out.println("Enter new guest name: ");
		   String newGuestName = scanner.nextLine();
		   System.out.println("Enter new room number: ");
		   int newRoomNumber = scanner.nextInt();
		   System.out.println("Enter new contact number");
		   String newContactNumber = scanner.next();
		   
		   String sql ="UPDATE reservation SET guest_name = '" + newGuestName + "',"+ "room_number = " + newRoomNumber + ", " + "contact_number = '" +newContactNumber + "' " + "WHERE reservation_id = " + reservationId;
		   try(Statement statement = connection.createStatement()){
			   int affectedRows = statement.executeUpdate(sql);
			   
		   if(affectedRows > 0) {
			System.out.println("RESERVATION UPDATED SUCCESSFULLY");
			}else{
				System.out.println("Reservation update failed");
			}
		   }
		   }catch(SQLException e) {
			   e.printStackTrace();
	    }
	 }
   
   private static void deleteReservation(Connection connection, Scanner scanner) {
	   try{
		   System.out.println("Enter reservation id to delete:");
		   int reservationId = scanner.nextInt();
		   
		   if(!reservationExists(connection, reservationId)) {
			   System.out.println("Reservation not found for the given ID.");
			   return;
			  }
		   String sql = "Delete FROM reservations WHERE reservation_id = " + reservationId;
		   
		   try(Statement statement = connection.createStatement()){
			   int affectedRows = statement.executeUpdate(sql);
			   
			   if(affectedRows > 0) {
				   System.out.println("Reservation deleted successfully");
				 }else {
					 System.out.println("Reservation deletion failed");
					 
				 }
		   }
		   }catch (SQLException e) {
			e.printStackTrace() ;
			}
		}
   
   private static boolean reservationExists(Connection connection, int reservationId) {
	   try {
		   String sql = "SELECT reservation_id From reservation WHERE reservation_id = " + reservationId;
		   
		   try(Statement statement = connection.createStatement();
				   ResultSet resultSet = statement.executeQuery(sql)){
			   return resultSet.next();
			  }
	   }catch(SQLException e) {
		   e.printStackTrace();
		   return false;
	   }
   }
   public static void exit() throws InterruptedException{
	   System.out.print("Existing System");
	   int i = 5;
	   while(i!=0) {
		   System.out.print(".");
		   Thread.sleep(450);
		   i--;
	   }
		System.out.println();
		System.out.println("THANKYOU FOR USING HOTEL RESERVATION SYSTEM");
		
	   }
	   
   }
  