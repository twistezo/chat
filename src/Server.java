import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Server {
	private int					port	=	5000;
	private	ServerSocket		server	=	null;
	private	Socket				socket	=	null;
	private	BufferedReader		br		=	null;
	private BufferedWriter		bw		=	null;	
	private String				title	=	"Server";
	
	public static void main(String[] args){
		
		/** Set LAF Style */
        try {
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	        
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}  

        /** new Server instance */
        new Server();
	}
	
	public Server(){
		try {
			
			/** Create socket for communication between apps */
			server = new ServerSocket(port);
			socket = server.accept();
			
			/** Create IO reader/writer */
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			/** Create GUI from above */
			new GUI_migLayout(title, bw, br);
			
		} catch (IOException e) {
			
			setWarningMsg("Server Error. \n" +e.getMessage());
		}
	}
	
	/** Pop-up error message with OK button */
	public void setWarningMsg(String text){
		
	    JOptionPane optionPane = new JOptionPane(text,JOptionPane.WARNING_MESSAGE);
	    JDialog dialog = optionPane.createDialog("Problem with application");
	    dialog.setAlwaysOnTop(true);
	    dialog.setVisible(true);
	}
	
}
