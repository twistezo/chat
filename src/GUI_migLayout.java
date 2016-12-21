import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author twistezo
 * 
 * @features
 * -connect by ip and port number
 * -server and client use one gui class
 * -can send message bothsides by write and click send button or press enter
 * -when message is received, app get current local time and put in message
 * -warning window when you trying to run firstly client
 * -red/green icon about connection status
 * -if connection lost icon show red color and warning message
 * 
 * @bugs
 * -when connection lost you have to restart app
 * -you have to run firstly server 
 * 
 * @todo
 * -start window with text fields for enter ip and port number
 * -continue app working after connection lost
 * -test button for on/off connection
 * 
 */

public class GUI_migLayout implements KeyListener {
	private	JFrame 				mainFrame;
	private	JPanel 				mainPanel;
	private	JLabel				titleLabel;
	private JLabel				serverStatusLabel;
	private JSeparator			separatorH;
	private	JTextArea 			incomingMessagesArea;
	private	JTextField			userInputField;
	private	JButton 			sendButton;
	private	JScrollPane 		scroll;
	private JLabel 				imagesOnOff;
	private ImageIcon 			pictureOnOff;
	private BufferedReader		br;
	private BufferedWriter		bw;
	private String				title;
	private String 				message;
	private	boolean				isConnection = true;
	private volatile boolean 	isRunningThread = true;
	
	public GUI_migLayout(String t, BufferedWriter buffwr, BufferedReader buffre) {
		title = t;
		bw = buffwr;
		br = buffre;
		
		/** Main Frame - MIGLayout */
		mainFrame = new JFrame(title);
		MigLayout layout = new MigLayout();
		mainPanel = new JPanel (layout);
		
		/** Title label */
		titleLabel = new JLabel("CHAT " +title);
		titleLabel.setFont(new Font(null, Font.PLAIN, 20));
		titleLabel.setForeground(Color.GRAY);
		
		/** Server status label */
		serverStatusLabel = new JLabel("Connection status: ");
		
		/** Horizontal eparator */
		separatorH = new JSeparator(JSeparator.HORIZONTAL);
		separatorH.setPreferredSize(new Dimension(2,2));
		
		/** Incoming messages text area */
		incomingMessagesArea = new JTextArea(15,30);
		incomingMessagesArea.setLineWrap(true);
		incomingMessagesArea.setWrapStyleWord(true);
		incomingMessagesArea.setEditable(false);
		
		/** Scroll for incoming message text field */
		scroll = new JScrollPane (incomingMessagesArea, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		/** Send message field */
		userInputField = new JTextField();
		userInputField.addKeyListener(this);
		
		/** Send button */
		sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		sendButton.setPreferredSize(new Dimension(70, 30));
		
		/** Method for realise RED/GREEN connection status by boolean isConnection */
		pictureOnOff = connectionStatus(isConnection);
		
		/** Create icon in JLabel */
		imagesOnOff = new JLabel();
		imagesOnOff.setIcon(pictureOnOff);
		
		/** Relocate components in panel */
		mainPanel.add(titleLabel, "span 2");
		mainPanel.add(serverStatusLabel, "align right, growy, split 2");
		mainPanel.add(imagesOnOff, "align left, growy, wrap");
		mainPanel.add(separatorH, "span 3, growx, growy, wrap 10px");
		mainPanel.add(scroll, "span 3, growx, wrap");
		mainPanel.add(userInputField, "span 2, growx, width 100%");
		mainPanel.add(sendButton, "span 1, align right, width 50%");
		
		/** Main Frame things */
		mainFrame.add(mainPanel);
		mainFrame.setBounds(50, 100, 0, 0);
		mainFrame.setResizable(false);
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setAlwaysOnTop(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/** Create new ATOM thread for communicate client-server IO */
		new Thread(new Runnable() {
	        public synchronized void run() {
	            while (isRunningThread) {
	            	
	            	/** If bufferedReader isn't empty */
	            	if(br != null && bw != null){
	            		
	            		try {
	            			
	            			/** Read text from BufferedReader */
							message = br.readLine();
							
							/** Get current local time */
							String time = currentTime();
							
							/** Set readed text to incoming messages area */
							incomingMessagesArea.append("Someone:                    "
									+ "                                              "
									+ "        " +time+ "\n  " +message+ "\n");
							
						} catch (IOException e) {
							
							/** Method for realise RED/GREEN connection status by boolean isConnection */
	            			isConnection = false;
	            			pictureOnOff = connectionStatus(isConnection);
	            			imagesOnOff.setIcon(pictureOnOff);
	            			
	            			/** Set warning OK window message 
	            			 *  and STOP thread by boolean variable */
	            			isRunningThread = false;
	            			setWarningMsg("Communication problem: " +e.getMessage());
						}
	            	}
	            }
	        }
	    }
		).start();
		
	}
	
	/** Method for realise RED/GREEN connection status by boolean isConnection 
	 *  ON TWO DIFFRENT WAYS OF READ & RESIZE IMAGE								 */
	public ImageIcon connectionStatus(boolean isConnection){
		
		/** Image icon ON-OFF data server */
		BufferedImage connectionIm = null;
		this.isConnection = isConnection;
		
		if (isConnection) {
			pictureOnOff = new ImageIcon(new ImageIcon("resources/on.png")
													.getImage()
													.getScaledInstance(12, 12, Image.SCALE_SMOOTH));
			
		} else {
			
			try {
				
				connectionIm = ImageIO.read(getClass().getResource("off.png"));
				Image offConnectionImageResized = connectionIm.getScaledInstance(12, 12, java.awt.Image.SCALE_SMOOTH);
				pictureOnOff = new ImageIcon(offConnectionImageResized);
				
			} catch (IOException e) {
				System.out.println("No File");
			}	
		}
		
		return pictureOnOff;
	}
	
	/** Pop-up error message with OK button */
	public void setWarningMsg(String text){
		
	    JOptionPane optionPane = new JOptionPane(text,JOptionPane.WARNING_MESSAGE);
	    JDialog dialog = optionPane.createDialog("Problem with application");
	    dialog.setAlwaysOnTop(true);
	    dialog.setVisible(true);
	}
	
	/** Method for sendButtonListener and sendKeyListener */
	public void sendButtonAction(){
		
		try {
			
			/** If BufferedWriter isn't null */
			if (bw != null && br != null){
				
				/** Connect sended message with user input text field */
				String messageOut = userInputField.getText();
				
				/** Send text through BufferedWriter */
				bw.write(messageOut);
	            bw.write('\n');
	            bw.flush();
	            
	            /** Clean user input text field */
	            userInputField.setText("");
	            
	            /** Show sended text in sender JTextArea
	             * 	and get current local time  			*/
	            String time = currentTime();
	            incomingMessagesArea.append(title+ ":                    "
				+ "                                              "
				+ "               " +time+ "\n  " +messageOut+ "\n");
			}
			
		} catch(Exception ex) {
			
			isConnection = false;
			pictureOnOff = connectionStatus(isConnection);
			imagesOnOff.setIcon(pictureOnOff);
		}
	}
	
	/** Button for send message to BufferedWriter */
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			
			sendButtonAction();
		}
	}
	
	/** Enter key listeners */
    public void keyPressed(KeyEvent e) {
  		int key = e.getKeyCode();
  		
	    if (key == KeyEvent.VK_ENTER) {
	    	
	    	sendButtonAction();
	    }
    }
 
    public void keyReleased(KeyEvent e) {
    }
 
    public void keyTyped(KeyEvent e) {
    }
    
    /** Get user local time */
    public String currentTime(){
    	
    	 DateFormat df = new SimpleDateFormat("HH:mm:ss");
  	     Date dateobj = new Date();
  	     String time = (df.format(dateobj));
  	     return time;
    }
	
}
