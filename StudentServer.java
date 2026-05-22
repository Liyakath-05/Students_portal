import java.io.*;
import java.net.*;
import java.sql.*;

public class StudentServer {

    public static void main(String args[]) {
        try {

            ServerSocket ss = new ServerSocket(8001);
            System.out.println("Student Server Started on Port 8001...");

            while (true) {

                Socket s = ss.accept();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(s.getInputStream()));

                String req = br.readLine();
                if (req == null || !req.contains(" "))
                    continue;

                String urlPath = req.split(" ")[1];

                PrintWriter pw = new PrintWriter(s.getOutputStream());

                if (urlPath.contains("/search?")) {

                    pw.println("HTTP/1.1 200 OK");
                    pw.println("Content-Type: text/html");
                    pw.println();

                    pw.println("<html>");
                    pw.println("<head><title>Student Search Result</title></head>");
                    pw.println("<body>");
                    pw.println("<center>");

                    // Database Connection
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    Connection con = DriverManager.getConnection(
                            "jdbc:oracle:thin:@localhost:1521:xe",
                            "system",
                            "root");

                    String rollNo = urlPath.split("=")[1];

                    PreparedStatement ps = con.prepareStatement(
                            "SELECT * FROM Students WHERE ROLL_NO = ?");

                    ps.setInt(1, Integer.parseInt(rollNo));

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {

                        pw.println("<h2>Student Found</h2>");

                        pw.println("<table border='1' cellpadding='10'>");
                        pw.println("<tr>");
                        pw.println("<th>Roll No</th>");
                        pw.println("<th>Name</th>");
                        pw.println("<th>Section</th>");
                        pw.println("</tr>");

                        pw.println("<tr>");
                        pw.println("<td>" + rs.getInt(1) + "</td>");
                        pw.println("<td>" + rs.getString(2) + "</td>");
                        pw.println("<td>" + rs.getString(4) + "</td>");
                        pw.println("</tr>");

                        pw.println("</table>");

                    } else {

                        pw.println("<h2>Student Not Found</h2>");
                        pw.println("<p>Roll No <b>" + rollNo + "</b> is not in our records.</p>");
                    }

                    con.close();

                    pw.println("</center>");
                    pw.println("</body>");
                    pw.println("</html>");
                }

                pw.flush();
                s.close();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}