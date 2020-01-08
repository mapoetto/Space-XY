package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import guiComponents.Button;
import guiComponents.CheckButton;
import guiComponents.Constants;
import guiComponents.Dialogue;
import guiComponents.Griglia;
import guiComponents.LateralPanel;
import guiComponents.Nave;
import guiComponents.NaveInserita;
import guiComponents.ToolTip;
import guiComponents.TransparentListCellRenderer;
import model.Data;
import guiComponents.Page;
import guiComponents.PaginaNavi;
import guiComponents.Quadrato;
import guiComponents.TextInput;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import comunicationObj.CmdCommands;
import comunicationObj.LogMessage;
import comunicationObj.Pacchetto;
import comunicationObj.Utente;
import controller.ClientThread;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

public class MainFrame { //VIEW
	
	public Data myData; //MODEL
	
	public ClientThread clientThread = null; //CONTROLLER
	
	private JLabel label;
	private JScrollPane scrollLabel;
	private Page homePage = new Page(this);
	private Page optionsPage = new Page(this);
	public  Page giocaPage = new Page(this);
	public  Page loadingPage = new Page(this);
	public  Page lobbyPage = new Page(this);
	public  Page errorPage = new Page(this);
	public  Page gamePage = new Page(this);
	public  Page youlostPage = new Page(this);
	public  Page youwinPage = new Page(this);
	public Page currentPage = new Page(this);
	private JLabel labelLoading;
	private JFrame frame=new JFrame();
	private static final String nameVersion = "Version 0.001";
	private Decoder decoder = Base64.getDecoder();
	private JFrame frameLog = new JFrame();
	private boolean logCreated = false;
	private AudioInputStream audioInputStream = null;
    private Clip clip = null;
    private Clip clipWINNING = null;
    private Clip clipLOSING= null;
	private Thread thread_backgroundAudio = null;
    private static JEditorPane logMessaggi = null;
    private String hostname = "localhost";
    private int port = 9090;
    private ArrayList<Page> pagine = new ArrayList<Page>();
    private Dialogue dia = null;
    
    private JWindow loadingWindow = new JWindow();
    
	//OPTIONS 
	private boolean showLog = false;
	public CheckButton buttonCheck_ViewGriglia, buttonCheck_Log, buttonCheck_BackgroundAudio;
	public boolean playMusic = true;
	public PaginaNavi pagNavi;
	
	public JTextPane lblError;
	public JTextPane lblLobby;
	public JTextPane lblCaption;
	public JTextPane lblTimer;
	public String normalTxtError = "";
	public String normalTxtLobby = "";
	public Timer timer;
	public static Font font = null;
	public Griglia griglia;
	public int secondi;
	private int FRAMEDIMENSION = Toolkit.getDefaultToolkit().getScreenSize().height-150;
	private int frameHeight = FRAMEDIMENSION;
	private int frameWidth = FRAMEDIMENSION;
	private AudioInputStream losingStream, winningStream;
	
	private JCheckBoxMenuItem chckbxSystem = new JCheckBoxMenuItem("SYSTEM");
    private JCheckBoxMenuItem chckbxSocket = new JCheckBoxMenuItem("SOCKET IN/OUT");
    private JCheckBoxMenuItem chckbxGame = new JCheckBoxMenuItem("GAME");
	
    public LateralPanel pannelloStats;
    
	private ArrayList<LogMessage> stackLog = new ArrayList<LogMessage>();
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
	
	public JList list = null;
	
	/**
	 * @wbp.parser.ent ryPoint
	 */
	public static void main(String[] args) {
		
			MainFrame main = new MainFrame();
			main.initialize();
				
	}
	
	public int getDimensionRatio(int Dimension) {
		//funzione per rendere il layout responsive
		
		int valoreDiRiferimento = 1000; //il layout è stato costruito su una base quadrata di 1000 x 1000
		
		//1000 sta a Dimension come FRAMEDIMENSION sta a x
		//x:y = a:b 
		//bx = ya
		//b = ya/x
		
		return (int)(Dimension*FRAMEDIMENSION)/valoreDiRiferimento;
		
	}
	
	/*
	 * DESCRIZIONE DELLA LOGICA DI BASE SU CUI FUNZIONA LA GUI
	 * 
	 * MainFrame è la classe padre (nonchè il VIEW di MVC)
	 * Qui vengono create le pagine della GUI che sono rappresentate dalla classe Page 
	 * 
	 * 
	 */
	
	private void initialize() {
		
		loadingWindow.setSize(492, 386); //finestra di caricamento precedente all'apertura del gioco
		loadingWindow.setLocationRelativeTo(null); //lo mette al centro
		
		JLabel loadScreen = new JLabel();
		loadScreen.setPreferredSize(new Dimension(492,386));
		loadScreen.setIcon(new ImageIcon(this.getClass().getResource("/loading_screen.png")));
		
		loadingWindow.getContentPane().add(loadScreen);
		
		loadingWindow.setVisible(true);
		
		MainFrame istance = this; //istanza che fa riferimento al JFrame attuale, che viene passato anche al ClientThred 
		myData = new Data(this); //Creiamo il MODEL
		
		//width di label = height x 1,331943
		//mette il background come sfondo
        label = new JLabel(new ImageIcon(new ImageIcon((this.getClass().getResource("/newBack.jpg"))).getImage().getScaledInstance((int)(FRAMEDIMENSION*1.331943), FRAMEDIMENSION, 3)));
        frame.setContentPane(label);
        
    	try{
    		
    		/*BACKGROUND MUSIC  /backgroundAudio.wav */
    		
  	      audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/backgroundAudio.wav"));
  	      clip = AudioSystem.getClip();
  	      clip.open(audioInputStream);
  	      FloatControl gainControl = 
  	    		    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
  	    		gainControl.setValue(+1.0f); // Reduce volume by 10 decibels.
  	    		
  	      clip.start();
  	      
  	      /* WINNING MUSIC */
  	      
  	     winningStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/winning.wav"));
 	     clipWINNING = AudioSystem.getClip();
 	     clipWINNING.open(winningStream);
 	     FloatControl gainControl2 = (FloatControl) clipWINNING.getControl(FloatControl.Type.MASTER_GAIN);
 	    	gainControl2.setValue(-5.0f); // Reduce volume by 10 decibels.
  	      
 	      /* LOSING MUSIC */
 	    
 	    	 losingStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/losing.wav"));
	  	      clipLOSING = AudioSystem.getClip();
	  	      clipLOSING.open(losingStream);
	  	      FloatControl gainControl3 = (FloatControl) clipLOSING.getControl(FloatControl.Type.MASTER_GAIN);
	  	    		gainControl3.setValue(-5.0f); // Reduce volume by 10 decibels.
 	    	
	  	}catch(Exception ex){  
	  		ex.printStackTrace();
	  	}
		
    	
   	 	ByteArrayInputStream font_InputStream = new ByteArrayInputStream(decoder.decode(Constants.ttf_Font_file));
	 
		 try {
			font = Font.createFont(Font.TRUETYPE_FONT, font_InputStream);
		 } catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		
		 try {
			font_InputStream.close();
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 
		 normalTxtLobby = "<html><center><div style=\"border: 2px solid white; padding: 5px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+getDimensionRatio(9)+"\" style=\"color: white; font-family: " + font.getFamily() + ";\">Scegli chi sfidare con la tua flotta:<br />Utenti Online: </font></div></center></html>";
    	
    	thread_backgroundAudio = new Thread() {
    		public void run() {
    			do { 
    				if(clip.isActive())
	    				do {
			    			if(!clip.isRunning() && !(clipWINNING.isActive()) && !(clipLOSING.isActive()) && !(clipLOSING.isRunning()) && !(clipWINNING.isRunning())) {
			    				
			    				if(playMusic) {
			    					updateLog(new LogMessage("System","ReStarto la clip di sottofondo",dtf.format(LocalTime.now())));
			    					clip.setMicrosecondPosition(0);
				    				clip.start();
				    				break;
			    				}
			    			}
	    				}while(playMusic);
    			}while(true);
    		}
    	};
    	thread_backgroundAudio.start();
    	
        frame.getContentPane().setLayout(null);
        frame.setSize(frameWidth,frameHeight);
        frame.setFocusable(true);
        frame.setFocusableWindowState(true);
        frame.setIconImage(new ImageIcon(this.getClass().getResource("/ICONA_png.png")).getImage());
        frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				
				if(logCreated) {
					frameLog.setVisible(false);
					frameLog.dispose();
				}
				frame.dispose();
				System.exit(0);
				
			}
		});
		
        frame.setTitle("SPACE XY");
        
	        /*
	         * 
	         * CREAZIONE ERRROR PAGE - START
	         * 
	         */
        
        	 errorPage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
        	 errorPage.setName("ERROR_PAGE");
        	 
		     lblError = new JTextPane();
		   	 lblError.setContentType("text/html");
		   	 lblError.setOpaque(false);
		   	 lblError.setEditable(false);
		   	 lblError.setFont(font);
		   	 lblError.setForeground(Color.red);
		   	 lblError.setBounds(getDimensionRatio(144), getDimensionRatio(463), getDimensionRatio(731), getDimensionRatio(295));
		   	 lblError.setText("<html><center><div style=\"border: 4px solid #542a2a; padding: 5px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+getDimensionRatio(45)+"\" style=\"color: #c42222; font-family: " + font.getFamily() + ";\">- </font></div></center></html>");
		   	 
		   	normalTxtError = lblError.getText(); 
		   	 
		    	Button buttonHome3 = new Button("Home", getDimensionRatio(230), getDimensionRatio(372), getDimensionRatio(709));
		    	buttonHome3.setName("Home");
		    	buttonHome3.addMouseListener(new MouseAdapter() {
		    		public void mouseClicked(MouseEvent e) {
		    	        Thread t = new Thread("my non EDT thread") {
		    	            public void run() {
		    	                //my work
		    	            	errorPage.esci(homePage);
		    	            }
		    	        };
		    	        t.start();
		    		}
		    	});
        	 
		    	errorPage.add(lblError, 2, 0);
		    	errorPage.add(buttonHome3, 3, 0);
        	
        /*
         * 
         * CREAZIONE ERROR PAGE - END
         * 
         */
        
        /*
         * 
         * CREAZIONE LOADING PAGE - START
         * 
         */
        
        	loadingPage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
        	loadingPage.setBounds(0, 0, 1000, 1000);
        	loadingPage.setName("LOADING_PAGE");
        	
            labelLoading = new JLabel(new ImageIcon("C:/Users/david/Desktop/SPACE XY/GUI/loading.gif"));
            labelLoading.setBounds(getDimensionRatio(350), getDimensionRatio(350), getDimensionRatio(275), getDimensionRatio(275));
        	
        	loadingPage.add(labelLoading, 2, 0);
        	
        	label.add(loadingPage);
        	
        	loadingPage.entra();
        	
        /*
         * 
         * CREAZIONE LOADING PAGE - END
         * 
         */
        
            /*
             * 
             * CREAZIONE LOBBY PAGE - START
             * 
             */
        	
            	lobbyPage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
            	lobbyPage.setName("LOBBY_PAGE");
            	
            	lblLobby = new JTextPane();
            	lblLobby.setContentType("text/html");
   		   	 	lblLobby.setOpaque(false);
   		   	 	lblLobby.setEditable(false);
   		   	 	lblLobby.setFont(font);
   		   	 	lblLobby.setForeground(Color.red);
   		   	 	lblLobby.setBounds(getDimensionRatio(144), getDimensionRatio(320), getDimensionRatio(731), getDimensionRatio(295));
   		   	 	lblLobby.setText(MainFrame.addWordBeforeTag(this.normalTxtLobby, "</font>", String.valueOf(myData.getListModel().getSize()))); //per mettere le persone online
   		   	 	
   		   	 	
   				list = new JList<Utente>(myData.getListModel());
   				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   				list.setBounds(getDimensionRatio(410), getDimensionRatio(567), getDimensionRatio(250), getDimensionRatio(200));
   				list.setCellRenderer(new TransparentListCellRenderer());
   				list.setOpaque(false);
   				
   				JScrollPane scrollPaneList = new JScrollPane(list); 
   				scrollPaneList.setBounds(getDimensionRatio(380), getDimensionRatio(517), getDimensionRatio(250), getDimensionRatio(200));
   				scrollPaneList.setPreferredSize(new Dimension(250, 200));
   				scrollPaneList.getViewport().setOpaque(false);
   				scrollPaneList.setViewportBorder(null);
   				scrollPaneList.setOpaque(false);
   				scrollPaneList.setFont(font.deriveFont(Font.PLAIN, 20));
   				scrollPaneList.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
   				
   				Button btnGo = new Button("Sfida", getDimensionRatio(170), getDimensionRatio(426),getDimensionRatio(750));
   				btnGo.addMouseListener(new MouseAdapter() {
   		    		public void mouseClicked(MouseEvent e) {
   						
   						if(list.getSelectedIndex() != -1 ) {
   							
   							Utente tmpUt = myData.getListModel().getElementAt(list.getSelectedIndex());
   							Pacchetto userPack = new Pacchetto(CmdCommands.wannaPlayWith,tmpUt,null,true);
   							
   							try {
   								clientThread.send(userPack);
   							} catch (Exception e1) {
   								// TODO Auto-generated catch block
   								e1.printStackTrace();
   							}
   							
   						}else {
   							updateLog(new LogMessage("System","Non hai selezionato alcun sfidante",dtf.format(LocalTime.now())));
   						}
   						
   					}
   				});
   				
   				Button btnReturnHome3 = new Button("Home", getDimensionRatio(170), getDimensionRatio(426), getDimensionRatio(850));
   				btnReturnHome3.addMouseListener(new MouseAdapter() {
   		    		public void mouseClicked(MouseEvent e) {
   						
   		    			Thread t = new Thread("my non EDT thread") {
   		    	            public void run() {
   		    	                //my work
   		    	            	currentPage.esci(homePage);
   		    	            }
   		    	        };
   		    	        t.start();
   						
   					}
   				});
   				
            	lobbyPage.add(lblLobby, 2, 0);
            	lobbyPage.add(scrollPaneList, 3, 0);
            	lobbyPage.add(btnGo, 4, 0);
            	lobbyPage.add(btnReturnHome3, 5, 0);

            /*
             * 
             * CREAZIONE LOBBY PAGE - END
             * 
             */
            	
            	frame.setLocationRelativeTo(null); //lo posiziona al centro dello schermo
            	frame.setBounds(frame.getX()-100, frame.getY(), frame.getWidth(), frame.getHeight());
       /*
      	* 
     	* CREAZIONE GAME PAGE - START
     	* 
     	*/

            	gamePage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
            	gamePage.setName("GAME_PAGE");
            	
            	griglia = new Griglia(this);
            	griglia.setBounds(getDimensionRatio(197),getDimensionRatio(310),600,600);
            	
            	pagNavi = new PaginaNavi(griglia, (int)new Point(frame.getLocation()).getX()+frame.getWidth(), frame.getY()+50,getDimensionRatio(50));
            	
            	lblCaption = new JTextPane();
            	lblCaption.setContentType("text/html");
   		   	 	lblCaption.setOpaque(false);
   		   	 	lblCaption.setEditable(false);
   		   	 	lblCaption.setFont(font);
   		   	 	lblCaption.setForeground(Color.white);
   		   	 	lblCaption.setBounds(getDimensionRatio(197), getDimensionRatio(243), getDimensionRatio(583), getDimensionRatio(84)); //per mettere le persone online
   		   	 	
   		   	 	lblTimer = new JTextPane();
   		   	 	lblTimer.setContentType("text/html");
   		   	 	lblTimer.setOpaque(false);
   		   	 	lblTimer.setEditable(false);
   		   	 	lblTimer.setFont(font);
   		   	 	lblTimer.setForeground(Color.red);
   		   	 	lblTimer.setBounds(getDimensionRatio(10), getDimensionRatio(243), getDimensionRatio(100), getDimensionRatio(100));
   		   	 	
   		   	 	secondi = 15;
   		   	 	
   		   	 	timer = new Timer(1000, new ActionListener() {
   		   	 		
	   		        public void actionPerformed(ActionEvent e) {
	   		        	secondi--;
	   		            lblTimer.setText("<html><b><font size=\""+getDimensionRatio(10)+"\" style=\"color: red; font-family: " + font.getFamily() + ";\">"+secondi+"</font></b></html>");
	   		            lblTimer.repaint();
	   		            
	   		            if(secondi == 0) {
	   		            	lblTimer.setText("<html><b><font size=\""+getDimensionRatio(10)+"\" style=\"color: red; font-family: " + font.getFamily() + ";\">"+secondi+"</font></b></html>");
		   		            lblTimer.repaint();
		   		            
	   		            	timer.stop();
	   		            	
	   		            	if(griglia.getNaviInserite().size() != griglia.getNaviDaInserire().size()) {
	   		            		try {
									clientThread.send(new Pacchetto (CmdCommands.iLostTheGame,"Hai perso perchè non hai posizionato le navi in tempo",null,true));
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
									updateLog(new LogMessage("Socket","Errore nell'invio del pacchetto iLostGame",dtf.format(LocalTime.now())));
								}
	   		            		
	   		            		updateLog(new LogMessage("Game","Hai perso la partita, poichè non hai posizionato tutte le navi in tempo!!",dtf.format(LocalTime.now())));
	   		            	}else {
	   		            		
	   		            		updateLog(new LogMessage("Game","Risultano " + griglia.getNaviInserite().size() + " navi inserite, dunque la partita può iniziare",dtf.format(LocalTime.now())));
	   		            		
	   		            		//invia le posizioni delle tue navi al server
	   		            		
	   		            		ArrayList<Quadrato> tmpFlotta = new ArrayList<Quadrato>();
	   		            		
	   		            		for(int i = 0; i<myData.getMyFlotta().size(); i++) {
	   		            			tmpFlotta.add(myData.getMyFlotta().get(i));
	   		            		}
	   		            		
	   		            		myData.game.setMyFlotta(myData.getMe(), tmpFlotta);
	   		            		
	   		            		try {
									clientThread.send(new Pacchetto (myData.getGame(),CmdCommands.setMyFlotta,null,true));
									
									lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+getDimensionRatio(6)+"\" style=\"color: white; font-family: " + font.getFamily() + ";\">Stai giocando contro "+myData.game.getAvversario(myData.getMe()).getUsername()+" - Invio della flotta al server... </font></div></center></html>");
									
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
									updateLog(new LogMessage("Game","Errore nell'invio del pacchetto setMyFlotta",dtf.format(LocalTime.now())));
								}
	   		            		
	   		            	}
	   		            	
	   		            }
	   		        }
	   		        
	   		    });
   		   	 	
   		   	 	
   		   	 	pannelloStats = new LateralPanel("sinistra",getDimensionRatio(50),this);
   		   	 	pannelloStats.setBounds(0,getDimensionRatio(420),pannelloStats.width,pannelloStats.height);
   		   	 	
   		   	 	gamePage.add(pannelloStats, 10, 0);
            	gamePage.add(griglia, 2, 0);
            	gamePage.add(lblTimer, 3, 0);
            	gamePage.add(lblCaption, 4, 0);
            	
   	   /*
      	* 
    	* CREAZIONE GAME PAGE - END
     	* 
    	*/
        	
            	/*
              	* 
             	* CREAZIONE YOULOST PAGE - START
             	* 
             	*/
            	
            	youlostPage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
            	youlostPage.setName("YOULOST_PAGE");
            	
   				Button btnReturnHome = new Button("Home", getDimensionRatio(170), getDimensionRatio(426), getDimensionRatio(750));
   				btnReturnHome.addMouseListener(new MouseAdapter() {
   		    		public void mouseClicked(MouseEvent e) {
   						
   		    			Thread t = new Thread("my non EDT thread") {
   		    	            public void run() {
   		    	                //my work
   		    	            	currentPage.esci(homePage);
   		    	            }
   		    	        };
   		    	        t.start();
   						
   					}
   				});
       			
       			JLabel labelYouLost = new JLabel();
       			labelYouLost.setOpaque(false);
       			labelYouLost.setIcon(new ImageIcon(new ImageIcon((this.getClass().getResource("/youlost.png"))).getImage().getScaledInstance(getDimensionRatio(679), getDimensionRatio(101), getDimensionRatio(1))));
       			labelYouLost.setPreferredSize(new Dimension(679,101));
       			labelYouLost.setBounds(getDimensionRatio(170),getDimensionRatio(554),getDimensionRatio(679),getDimensionRatio(101));
       			
       			youlostPage.add(btnReturnHome,2,0);
       			youlostPage.add(labelYouLost,3,0);
       			
            	/*
              	* 
             	* CREAZIONE YOULOST PAGE - END
             	* 
             	*/
            	
       			/*
       			 * 
       			 * CREAZIONE YOUWIN PAGE - START
       			 * 
       			 */
       			
       			youwinPage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
            	youwinPage.setName("YOUWIN_PAGE");
            	
   				Button btnReturnHome2 = new Button("Home", getDimensionRatio(170), getDimensionRatio(426), getDimensionRatio(750));
   				btnReturnHome2.addMouseListener(new MouseAdapter() {
   		    		public void mouseClicked(MouseEvent e) {
   		    			
   		    			Thread t = new Thread("my non EDT thread") {
   		    	            public void run() {
   		    	                //my work
   		    	            	currentPage.esci(homePage);
   		    	            }
   		    	        };
   		    	        t.start();
   						
   					}
   				});
            	
   				JLabel labelYouWin = new JLabel();
       			labelYouWin.setOpaque(false);
       			labelYouWin.setIcon(new ImageIcon(new ImageIcon((this.getClass().getResource("/youwin.png"))).getImage().getScaledInstance(getDimensionRatio(591), getDimensionRatio(94), getDimensionRatio(1))));
       			labelYouWin.setPreferredSize(new Dimension(591,94));
       			labelYouWin.setBounds(getDimensionRatio(214),getDimensionRatio(558),getDimensionRatio(591),getDimensionRatio(94));
       			
       			youwinPage.add(btnReturnHome2,2,0);
       			youwinPage.add(labelYouWin,3,0);
       			
       			/*
       			 * 
       			 * CREAZIONE YOUWIN PAGE -END
       			 * 
       			 */
            	
            	
        /*
         * 
         * CREAZIONE HOME PAGE - START
         * 
         */
        
    	homePage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
    	homePage.setName("HOMEPAGE_PAGE");
    	
    	Button buttonGioca = new Button("Gioca", getDimensionRatio(280), getDimensionRatio(351), getDimensionRatio(433));
    	buttonGioca.setName("Gioca");
    	
    	buttonGioca.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    	        Thread t = new Thread("my non EDT thread") {
    	            public void run() {
    	                //my work
    	            	if(clientThread == null)
    	            		homePage.esci(giocaPage);
    	            	else
    	            		homePage.esci(lobbyPage);
    	            }
    	        };
    	        t.start();
    		}
    	});
    	
    	Button buttonOpzioni = new Button("Opzioni", getDimensionRatio(230), getDimensionRatio(372), getDimensionRatio(565));
    	buttonOpzioni.setName("Opzioni");
    	
    	buttonOpzioni.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    	        Thread t = new Thread("my non EDT thread") {
    	            public void run() {
    	                //my work
    	            	homePage.esci(optionsPage);
    	            }
    	        };
    	        t.start();
    		}
    	});
    	
    	
    	Button buttonEsci = new Button("Esci", getDimensionRatio(170), getDimensionRatio(408), getDimensionRatio(672));
    	buttonEsci.setName("Esci");
    	
    	buttonEsci.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			System.exit(0);
    		}
    	});
       
    	JLabel lblVersion1 = new JLabel(nameVersion);
    	lblVersion1.setForeground(Color.BLACK);
    	lblVersion1.setBounds(getDimensionRatio(850), getDimensionRatio(925), getDimensionRatio(151), getDimensionRatio(25));
        
    	homePage.add(buttonGioca, 4, 0);
    	homePage.add(buttonOpzioni, 3, 0);
    	homePage.add(buttonEsci, 2, 0);
    	homePage.add(lblVersion1, 1, 0);

    	
    	
    	
    	/*
    	 * 
    	 * CREAZIONE HOMEPAGE - END
    	 * 
    	 */
    	
    	/*
    	 * 
    	 * CREAZIONE OPTIONSPAGE - START
    	 * 
    	 */
    	
    	optionsPage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
    	optionsPage.setName("OPTIONS_PAGE");
    	
    	buttonCheck_ViewGriglia = new CheckButton(true, getDimensionRatio(70), getDimensionRatio(453), getDimensionRatio(390), "Se attivo, divide la vista della griglia in base al turno del giocatore.");
    	
    	buttonCheck_Log = new CheckButton(showLog, getDimensionRatio(70), getDimensionRatio(453), getDimensionRatio(465), "Se attivo, viene mostrato una finestra con il log.");
    	
    	buttonCheck_Log.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    	        showLog = !showLog;
    	        
    	        if(showLog) {
    	        	istance.showLog();
    	        }else {
    	        	istance.hideLog();
    	        }
    	        
    		}
    	});
    	
    	buttonCheck_BackgroundAudio = new CheckButton(playMusic, getDimensionRatio(70), getDimensionRatio(453), getDimensionRatio(540), "Se attivo, riproduce la musica in background");
    	
    	buttonCheck_BackgroundAudio.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
	    	        if(playMusic) {
	    	        	playMusic = !playMusic; 
	    	        	clip.stop();
	    	        }else {
	    	        	playMusic = !playMusic; 
	    	        	clip.start();
	    	        }
    		}
    	});
    	
    	Button buttonHome = new Button("Home", getDimensionRatio(230), getDimensionRatio(372), getDimensionRatio(709));
    	buttonHome.setName("Home");
    	buttonHome.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    	        Thread t = new Thread("my non EDT thread") {
    	            public void run() {
    	                //my work
    	            	optionsPage.esci(homePage);
    	            }
    	        };
    	        t.start();
    		}
    	});
    	
    	JLabel lblVersion2 = new JLabel(nameVersion);
    	lblVersion2.setForeground(Color.BLACK);
    	lblVersion2.setBounds(getDimensionRatio(850), getDimensionRatio(925), getDimensionRatio(151), getDimensionRatio(25));
    	
    	optionsPage.add(buttonCheck_Log, 6, 0);
    	optionsPage.add(buttonCheck_BackgroundAudio, 7, 0);
    	optionsPage.add(buttonCheck_ViewGriglia, 8, 0);
    	optionsPage.add(buttonHome, 2, 0);
    	optionsPage.add(lblVersion2);
    	
    	/*
    	 * 
    	 * CREAZIONE OPTIONSPAGE - END
    	 * 
    	 */
    	
    	/*
    	 * 
    	 * CREAZIONE GIOCAPAGE - START
    	 * 
    	 */
    	
    	 giocaPage.setPreferredSize(new Dimension(FRAMEDIMENSION, FRAMEDIMENSION));
    	 giocaPage.setName("GIOCA_PAGE");
    	 
    	 JTextPane lblGimmeUsr = new JTextPane();
    	 lblGimmeUsr.setContentType("text/html");
    	 lblGimmeUsr.setOpaque(false);
    	 lblGimmeUsr.setEditable(false);
    	 lblGimmeUsr.setFont(font);
    	 lblGimmeUsr.setForeground(Color.white);
    	 lblGimmeUsr.setBounds(getDimensionRatio(250), getDimensionRatio(345), getDimensionRatio(531), getDimensionRatio(95));
    	 lblGimmeUsr.setText("<html><center><font size=\""+getDimensionRatio(9)+"\" style=\" color: white; font-family: " + font.getFamily() + ";\">Benvenuto Comandante !!<br /> qual è il suo nome ?</font></center></html>");
    	 
    	 TextInput username = new TextInput(getDimensionRatio(181));
    	 username.setText("Nome");
    	 username.setEditable(true);
    	 username.setBounds(getDimensionRatio(400), getDimensionRatio(460), getDimensionRatio(181), getDimensionRatio(55));
    	 
     	 Button inviaUsr = new Button("Entra", getDimensionRatio(130), getDimensionRatio(423), getDimensionRatio(529));
     	 inviaUsr.setName("inviaUsr");
    	 
     	 inviaUsr.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			if(username.getText().length() >= 3) {
	    	        if(clientThread == null) {
	    	        	clientThread = new ClientThread(hostname, port, username.getText(), istance);
	        	        clientThread.start();
	        	        
	        	        giocaPage.setVisible(false);
	        	        
	        	        loadingPage.entra();
	    	        }
    			}
    		}
    	});
     	 
     	 
     	 Button buttonHome2 = new Button("Home", getDimensionRatio(230), getDimensionRatio(372), getDimensionRatio(659));
     	 buttonHome2.setName("Home");
    	 
     	buttonHome2.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    	        Thread t = new Thread("my non EDT thread") {
    	            public void run() {
    	                //my work
    	            	giocaPage.esci(homePage);
    	            }
    	        };
    	        t.start();
    		}
    	});
     	 
    	JLabel lblVersion3 = new JLabel(nameVersion);
    	lblVersion3.setForeground(Color.BLACK);
    	lblVersion3.setBounds(getDimensionRatio(850), getDimensionRatio(925), getDimensionRatio(151), getDimensionRatio(25));
     	
     	 giocaPage.add(lblGimmeUsr, 5, 0);
     	 giocaPage.add(username, 4, 0);
     	 giocaPage.add(inviaUsr, 3, 0);
     	 giocaPage.add(buttonHome2, 2, 0);
     	 giocaPage.add(lblVersion3, 1, 0);
     	  
    	/*
    	 * 
    	 * CREAZIONE GIOCAPAGE - END
    	 * 
    	 */
    	
     	 giocaPage.setBounds(0, 0, FRAMEDIMENSION, FRAMEDIMENSION);
     	 optionsPage.setBounds(0, 0, FRAMEDIMENSION, FRAMEDIMENSION);
     	 homePage.setBounds(0, 0, FRAMEDIMENSION, FRAMEDIMENSION);
     	 errorPage.setBounds(0, 0, FRAMEDIMENSION, FRAMEDIMENSION);
     	 lobbyPage.setBounds(0, 0, FRAMEDIMENSION, FRAMEDIMENSION);
     	 gamePage.setBounds(0, 0, FRAMEDIMENSION, FRAMEDIMENSION);
     	 youlostPage.setBounds(0, 0, FRAMEDIMENSION, FRAMEDIMENSION);
     	 youwinPage.setBounds(0, 0, FRAMEDIMENSION, FRAMEDIMENSION);
     	 
     	label.add(giocaPage);
     	label.add(optionsPage);
     	label.add(homePage);
     	label.add(errorPage);
     	label.add(lobbyPage);
     	label.add(gamePage);
     	label.add(youlostPage);
     	label.add(youwinPage);
    	
     	pagine.add(giocaPage);
    	pagine.add(optionsPage);
    	pagine.add(homePage);
    	pagine.add(errorPage);
    	pagine.add(lobbyPage);
    	pagine.add(gamePage);
    	pagine.add(youlostPage);
    	pagine.add(youwinPage);
    	
    	loadingWindow.setVisible(false);
    	frame.setVisible(true);
    	
    	loadingPage.esci(homePage);
	}
	
	public void playWinningSound() {
		
		boolean iSetFalse = false;
		Thread tW = new Thread();
		
		if(playMusic) {
		  	tW = new Thread() {
		  		public void run() {
		  			while(clipWINNING.getFrameLength() != clipWINNING.getFramePosition()){
				  		playMusic = false;
				  		updateLog(new LogMessage("System","Clip Winning in riproduzione",dtf.format(LocalTime.now())));
					}
		  			playMusic = true;
		  			clip.start();
		  			
		  			this.interrupt();
		  		}
		  	};
		  	
		  	iSetFalse = true;
		  	playMusic = false;
	  	}
		
		playMusic = false;
		
	      /* WINNING MUSIC */
	      
	     try {
			winningStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/winning.wav"));
			clipWINNING = AudioSystem.getClip();
		    clipWINNING.open(winningStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	     FloatControl gainControl2 = (FloatControl) clipWINNING.getControl(FloatControl.Type.MASTER_GAIN);
	    	gainControl2.setValue(-5.0f); // Reduce volume by 10 decibels.
	    	
			if(clip.isActive())
    			if(clip.isRunning()) {
    				
    				clip.setMicrosecondPosition(clip.getMicrosecondPosition());
    				clip.stop();
    				
    			}
			
			
		  	clipWINNING.start();
		  	
		  	if(iSetFalse) {
		  		tW.start();
		  	}
	}
	
	public void playLosingSound() {
		  
		boolean iSetFalse = false;
		Thread tW = new Thread();
		
		if(playMusic) {
		  	tW = new Thread() {
		  		public void run() {
		  			while(clipLOSING.getFrameLength() != clipLOSING.getFramePosition()){
				  		playMusic = false;
				  		updateLog(new LogMessage("System","Clip Losing in riproduzione",dtf.format(LocalTime.now())));
					}
		  			playMusic = true;
		  			clip.start();
		  			
		  			this.interrupt();
		  		}
		  	};
		  	
		  	iSetFalse = true;
		  	playMusic = false;
	  	}
		
		playMusic = false;
		
	      /* LOSING MUSIC */
	    
	    	 try {
				losingStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/losing.wav"));
				clipLOSING = AudioSystem.getClip();
				clipLOSING.open(losingStream);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
	  	    
	  	      FloatControl gainControl3 = (FloatControl) clipLOSING.getControl(FloatControl.Type.MASTER_GAIN);
	  	    		gainControl3.setValue(-5.0f); // Reduce volume by 10 decibels.
		
		if(clip.isActive())
			if(clip.isRunning()) {
				
				clip.setMicrosecondPosition(clip.getMicrosecondPosition());
				clip.stop();
				
			}
		
		clipLOSING.start();
		
		if(iSetFalse) {
	  		tW.start();
	  	}
		
	}
	
	public void resetGame() {
		clientThread.getNaviColpite().removeAll(clientThread.getNaviColpite());
		clientThread.setQuadColpiti(0);
		this.griglia.resetAll();
		this.pannelloStats.resetAll();
	}
	
	private void buildLogMessages() {
		
		logMessaggi.setText("<center><b><font color=\"white\">LOADING STACK...</font></b></center>" + "<br />");
		
		String all = "<table>";
		
		for(LogMessage lmsg : stackLog) {
			if(lmsg.getType().equals("System") && chckbxSystem.isSelected()) {
				all += "<tr><td><b><font color=\"blue\">SYSTEM</b><br /><font color=\"white\">("+ lmsg.getData() +")</font></td><td>" + lmsg.getContent() +"</td></tr>";
			}else if(lmsg.getType().equals("Socket") && chckbxSocket.isSelected()) {
				all += "<tr><td><b><font color=\"red\">SOCKET</b><br /><font color=\"white\">("+ lmsg.getData() +")</font></td><td>" + lmsg.getContent() +"</td></tr>";
			}else if(lmsg.getType().equals("Game") && chckbxGame.isSelected()) {
				all += "<tr><td><b><font color=\"green\">GAME</b><br /><font color=\"white\">("+ lmsg.getData() +")</font></td><td>" + lmsg.getContent() +"</td></tr>";
			}
		}
		
		all += "</table>";
		logMessaggi.setText("<html><body>LOG (SPACE XY):<br />-----------------------------------------------------<br />" + all + "<br /></body></html>");
		
		//System.out.println("HTML-> "+logMessaggi.getText());
	}
	
	private void showLog() {
		if(!logCreated) {
			
			JLabel label = new JLabel();
			
			try {
	            label = new JLabel(new ImageIcon(ImageIO.read(this.getClass().getResource("/backgroundLOG.jpg"))));
	            frameLog.setContentPane(label);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
			
			JMenuBar menuBar = new JMenuBar();
	        frameLog.setJMenuBar(menuBar);
	        
	        chckbxSystem.setForeground(Color.blue);
	        chckbxSystem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						buildLogMessages();
					}
	            });
	        menuBar.add(chckbxSystem);
	        
	        chckbxSocket.setForeground(Color.red);
	        chckbxSocket.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					buildLogMessages();
				}
            });
	        menuBar.add(chckbxSocket);
	        
	        chckbxGame.setForeground(Color.green);
	        chckbxGame.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					buildLogMessages();
				}
            });
	        menuBar.add(chckbxGame);
			
			logMessaggi = new JEditorPane(new HTMLEditorKit().getContentType(),"");
			DefaultCaret caret = (DefaultCaret)logMessaggi.getCaret(); //serve per mantenere lo scroll sempre in basso quando si aggiorna l'area
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		    String bodyRule = "body { letter-spacing: 10px; font-size: 22 pt; color: white; }";
		    ((HTMLDocument)logMessaggi.getDocument()).getStyleSheet().addRule(bodyRule);
			logMessaggi.setEditable(false);
			logMessaggi.setPreferredSize(new Dimension(680,450));
			logMessaggi.setSize(680, 450);
			logMessaggi.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
			logMessaggi.setAutoscrolls(true);

			JScrollPane scrollPane = new JScrollPane(logMessaggi);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setPreferredSize(new Dimension(680, 450));
			scrollPane.setOpaque(false);
			scrollPane.getViewport().setOpaque(false);
			scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			
			logMessaggi.setText("LOG (SPACE XY):<br />-----------------------------------------------------<br />");
			logMessaggi.setOpaque(false);
	        frameLog.getContentPane().setLayout(new FlowLayout());
	        frameLog.setSize(1000,1000);
	        frameLog.setFocusable(true);
	        frameLog.setFocusableWindowState(true);
	        frameLog.setIconImage(new ImageIcon(this.getClass().getResource("/ICONA_png.png")).getImage());
			frameLog.setResizable(false);
			
			frameLog.getContentPane().add(scrollPane);
	        
			frameLog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					updateLog(new LogMessage("System","Hai chiuso il log",dtf.format(LocalTime.now())));
					if(showLog == true) {
						showLog = !showLog;
						buttonCheck_Log.setInteragibile(true);
						buttonCheck_Log.clicckami();
						buttonCheck_Log.setInteragibile(false);
						frame.repaint();
					}
				}
			});
			
	        logCreated = true;
	        
		}
		frameLog.setTitle("LOG");
		frame.setBounds(0,0, FRAMEDIMENSION,FRAMEDIMENSION);
		frameLog.setBounds(frame.getBounds().width, (frame.getBounds().height - 500), 700, 500);
		
		frameLog.setVisible(true);
	}
	
	public void updateLog(LogMessage msg) {
		if(logCreated) {
			
			stackLog.add(msg);
			
			buildLogMessages();
			this.updateGUI();
		}
	}
	
	public static String addWordBeforeTag(String string, String tag, String word) {
		
		String[] split = string.split(tag);
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
		    sb.append(split[i]);
		    sb.append(word);
		}
		
		String joined = sb.toString();
		
		return joined;
		
	}
	
	private void hideLog() {
		frameLog.setVisible(false);
	}
	
	public void destroyClientThread() {
		clientThread = null;
	}
	
	public void updateGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.repaint();
				if(logCreated) {
					frameLog.repaint();
				}
			}
		});
	}
	
    public void startDialogue(Utente ut) {
    	
    	dia = new Dialogue(getDimensionRatio(600), getDimensionRatio(300), getDimensionRatio(200), getDimensionRatio(350), ut, this, font);
    	
    	label.removeAll();
    	
    	label.add(dia);
	
		dia.initialize();
		
    }
	
    public void addMyPages(boolean risposta) {
    	
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				dia.setVisible(false);
				
				label.removeAll();
				
		    	for(Page pg : pagine) {
		    		label.add(pg);
		    	}
		    	
		    	currentPage.setVisible(true);
		    	
		    	frame.repaint();
			}
		});
    	
    }
    
	public ClientThread getClientThread() {
		return clientThread;
	}

	public JLabel getLabelLoading() {
		return labelLoading;
	}

	public void setLabelLoading(JLabel labelLoading) {
		this.labelLoading = labelLoading;
	}

	public Page getOptionsPage() {
		return optionsPage;
	}

	public Page getLoadingPage() {
		return loadingPage;
	}

	public Page getLobbyPage() {
		return lobbyPage;
	}

	public Page getErrorPage() {
		return errorPage;
	}

	public Page getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Page currentPage) {
		this.currentPage = currentPage;
	}

	public PaginaNavi getPagNavi() {
		return pagNavi;
	}
	
	
}
