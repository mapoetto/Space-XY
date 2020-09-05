package server;


import guiComponents.*;
import comunicationObj.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


public class MainServerXY implements Runnable{
	
	private static JFrame frame;
	private static JTextArea areaMessaggi = new JTextArea(10, 50);
	private ServerSocket serverSocket;
	private static ArrayList<JPanel> listaJPanel = new ArrayList<JPanel>();
	private static ArrayList<JEditorPane> listaJTextArea = new ArrayList<JEditorPane>();
	private static ArrayList<JTextPane> listaJTextPane = new ArrayList<JTextPane>();

	private static ArrayList<Game> listaGame = new ArrayList<Game>();
	
	private static PoolThread poolThread = new PoolThread(10);
	private final JPanel panelTHREADS = new JPanel();
	
    public static void main(String[] args) {
       
    	new MainServerXY();
        
    }
    
    MainServerXY(){
    	this.run();
    }
    
	@Override
	public void run() {
		
		frame = new JFrame();
		frame.setTitle("Server XY");
		frame.getContentPane().setLayout(new FlowLayout());
		frame.setBounds(100, 10, 595, 1000);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				
				try {
					
					serverSocket.close();
					
					for(ServerThread sthr : poolThread.getListaThread()) {
						sthr.close();
					}
					
					e.getWindow().dispose();
					
				} catch (Exception e1) {
					System.out.println("Errore nella chiusura del server: " + e1.getMessage());
				}
				
			}
		});
		
		JPanel panel1 = new JPanel();
		panel1.setBounds((int)(frame.getWidth()/2), 0, 100, 100);
		
		JButton btnCloseServer = new JButton("Chiudi Server");
		btnCloseServer.setBounds((int)(frame.getWidth()/2), 0, 50, 50);
		btnCloseServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					
					serverSocket.close();
					
					for(ServerThread sthr : poolThread.getListaThread()) {
						sthr.close();
					}
					
					
				} catch (Exception e1) {
					System.out.println("Errore nella chiusura del server: " + e1.getMessage());
				}
				
			}
		});
		
		areaMessaggi.setBounds(10, 10, frame.getWidth(), 300);
		JScrollPane scrollPane = new JScrollPane(areaMessaggi);
		areaMessaggi.setEditable(false);
		frame.getContentPane().add(scrollPane, BorderLayout.SOUTH);
		
		panel1.add(btnCloseServer);
		frame.getContentPane().add(panel1);
		
		JScrollPane scrollPaneTHREADS = new JScrollPane(panelTHREADS);
		panelTHREADS.setLayout(new WrapLayout());
		scrollPaneTHREADS.setPreferredSize(new Dimension(550, 650));
		frame.getContentPane().add(scrollPaneTHREADS);
		
		
		frame.setVisible(true);
		
		this.startServer();
	}
    
	private Object getThread(String sess) {
		
		for(ServerThread sthr : poolThread.getListaThread()) {
			if(sthr.getSESSION().equals(sess)) {
				return sthr;
			}
		}
		
		return false;
		
	}
	
	private void startServer() {
        int port = 9090;
        
        try { 
        	
        	this.serverSocket = new ServerSocket(port);
        	
            log("Server is listening on port " + port);
 
            while (true) {
                Socket socket = serverSocket.accept();
                SocketAddress clientAdress = socket.getRemoteSocketAddress();
                String sess = String.valueOf(Math.random());
                
                log("New client connected [ " + sess + " ]");
                
                socket.setTcpNoDelay(true);
                
                final ServerThread st = new ServerThread(socket, sess);  
                	
                poolThread.addThread(st);
                	
	                if(addPanelThread(st, clientAdress) > 0) {
	                	poolThread.getThread(sess).start();
	                }
                
                
            }
 
        } catch (Exception ex) {
            log("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
	}
	
	public static ArrayList<Utente> onlineThreadsWitoutMe(ServerThread st){
		
		ArrayList<Utente> tmpList = new ArrayList<Utente>();
		
		for(ServerThread sthr : poolThread.getListaThread()) {
			if(!sthr.getSESSION().equals(st.getSESSION()) && sthr.isAuth()) {
				tmpList.add(new Utente(sthr.getUsername(), sthr.getSESSION(), "free"));
				
			}
		}
		
		return tmpList;
	}
	
	
	/*
	 * Aggiunge un Panel per ogni client connesso
	 */
	public int addPanelThread(final ServerThread th, SocketAddress clientAddress) {
		
		final JPanel panel = new JPanel(); 		 //panel è il pannello padre della GUI relativa ad un client
		final String session = th.getSESSION();
		panel.setLayout(new WrapLayout());
		panel.setName(session);
		panel.setSize(500, 90);
		
		JPanel topPanel = new JPanel();			//topPanel è il pannello posto superiormente
		topPanel.setLayout(new BorderLayout());
		topPanel.setPreferredSize(new Dimension(500, 20));
		
		JTextPane txtpnLog = new JTextPane();	//Pane per mostrare il Client Address
		txtpnLog.setToolTipText("Client Address");
		txtpnLog.setEditable(false);
		txtpnLog.setText(String.valueOf(clientAddress));
		txtpnLog.setBounds(0, 0, 57, 20);
		
		JTextPane pnStatus = new JTextPane();	//Pane per mostrare lo status del Client
		pnStatus.setContentType("text/html");
		pnStatus.setToolTipText("Stato");
		pnStatus.setEditable(false);
		pnStatus.setText("<html><b><font color=\"green\">Online</font></b></html>");
		pnStatus.setForeground(Color.GREEN);
		pnStatus.setBounds(0, 0, 57, 20);
		pnStatus.setName(session);
		topPanel.add(pnStatus, BorderLayout.EAST);
		
		JPanel spazioBianco = new JPanel();
		spazioBianco.setLayout(new BorderLayout());
		spazioBianco.setSize(450, 150);
		final JTextPane pnKick = new JTextPane();	//Pane per kickare il client
		pnKick.setContentType("text/html");
		pnKick.setToolTipText("Kicka il client");
		pnKick.setEditable(false);
		pnKick.setName(session);
		pnKick.setText("<html><center><font color=\"grey\">Kick</font></center></html>");
		
		pnKick.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Object tmpTh = getThread(session);
				if(tmpTh instanceof ServerThread) {
					
					tmpTh = (ServerThread)tmpTh; 
					try {
						updateStatePanel((ServerThread)tmpTh, "<html><b><font color=\"orange\">Kicked</font></b></html>");
						if(!PoolThread.deleteSocket((ServerThread)tmpTh, session)) 
							log("Impossibile trovare il socket con questa sessione: " + session);
						else {
							log("Il socket: " + session + " è stato disconnesso");
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						log("Impossibile disconnettere il socket: " + session);
						e1.printStackTrace();
					}
				}else {
					log("Impossibile trovare il socket");
				}
			}
			
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {
				pnKick.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			} 
			public void mouseExited(MouseEvent e) {
				pnKick.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		spazioBianco.add(pnKick,BorderLayout.CENTER);
		topPanel.add(spazioBianco, BorderLayout.CENTER);
		topPanel.add(txtpnLog, BorderLayout.WEST);
		
		JEditorPane areaMessaggi = new JEditorPane(new HTMLEditorKit().getContentType(),"");
		DefaultCaret caret = (DefaultCaret)areaMessaggi.getCaret(); //serve per mantenere lo scroll sempre in basso quando si aggiorna l'area
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    Font font = new Font("Segoe UI", Font.PLAIN, 14);
	    String bodyRule = "body { font-family: " + font.getFamily() + "; " +
	            "font-size: " + font.getSize() + "pt; }";
	    ((HTMLDocument)areaMessaggi.getDocument()).getStyleSheet().addRule(bodyRule);
		areaMessaggi.setEditable(false);
		areaMessaggi.setSize(500, 50);
		areaMessaggi.setName(th.getSESSION());
		
		JScrollPane scrollPane = new JScrollPane(areaMessaggi);
		scrollPane.setPreferredSize(new Dimension(500, 190));
		
		areaMessaggi.setText("<b>Pannello sessione:</b> " + th.getSESSION() + "<br />---------------<br />");
		
		panel.add(topPanel);
		panel.add(scrollPane);
		
		listaJPanel.add(panel);
		listaJTextArea.add(areaMessaggi);
		listaJTextPane.add(pnStatus);
		
		SwingUtilities.invokeLater(new Runnable () {
			public void run() {
				
				panelTHREADS.add(panel);
				frame.repaint();
				frame.setVisible(false);
				frame.setVisible(true);
				
			}
		});

		return listaJPanel.size();
	}
	
	public static void updatePanel(ServerThread th, String msg) {
		
		for(JEditorPane txtA : listaJTextArea) {
			if(txtA.getName().equals(th.getSESSION())) {
				
				String aggiorno = addWordBeforeTag(txtA.getText(), "</body>", msg.toString()+ "<br />");
				
				txtA.setText(aggiorno);
				
				frame.repaint();
				
				break;
			}
		}
		
	}
	
	public static void updateStatePanel(ServerThread th, String msg) {
		
		for(JTextPane txtA : listaJTextPane) {
			if(txtA.getName().equals(th.getSESSION())) {
				
				txtA.setText(msg); //msg deve essere in formato html
				
				frame.repaint();
				frame.setVisible(false);
				frame.setVisible(true);
				
				break;
			}
		}
		
	}
	
	/*
	 * Quando si setta un JEditorPane con text/html, il testo viene inserito in una tipica pagina html contenente i tag HTML, HEAD e BODY... 
	 * quindi se si vuole aggiornare il JEditoPane, è utile inserire il testo da aggiornare prima di un tag (solitamente BODY) 
	 * 
	 */
	
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
	
	public synchronized static boolean updateGameFlotta(Game game, Utente ut) {
		/*for(Game tmpGame : listaGame) {
			if(tmpGame.getSESSION().equals(game.getSESSION()) && !(tmpGame.getStato().equals("finito"))) {
				
				tmpGame.setMyFlotta(ut, game.getMyFlotta(ut));
				
				listaGame.set(index, element)
				
				return true;
			}
		}*/
		
		System.out.println("Quantità di game: ->"+listaGame.size());
		
		for (int i = 0; i < listaGame.size(); i++) {
			if(listaGame.get(i).getSESSION().equals(game.getSESSION()) && !(listaGame.get(i).getStato().equals("finito"))) {
				
				listaGame.get(i).setMyFlotta(ut, game.getMyFlotta(ut));
				
				return true;
			}
		}
		
		return false;
	}
	
	public synchronized static Game checkIfReadyToStart(Game game) {
		
		for(Game tmpGame : listaGame) {
			
			if(tmpGame.getSESSION().equals(game.getSESSION()) && !(tmpGame.getStato().equals("finito"))) {
				
				if(tmpGame.getFlottaSfidante() != null) {
					
					if(tmpGame.getFlottaSfidato() != null) {
						tmpGame.setStato("iniziato");
						//log("SONO DENTRO YEAHHHHH");
						
						return tmpGame;
					}else {
						//log("terzo IF errato");
					}
				}else {
					//log("secondo IF errato");
				}
				
				return null;
			}else {
				//log("primo IF errato");
			}
		}
		
		//log("sono ancora qua");
		
		return null;
	}
	
	public synchronized static boolean checkMove(Game game, Quadrato quad, Utente ut){
		
		for(Game tmpGame : listaGame) {
			
			if(tmpGame.getSESSION().equals(game.getSESSION()) && !(tmpGame.getStato().equals("finito"))) {
				
				if(tmpGame.getFlottaSfidante() != null) {
					
					if(tmpGame.getFlottaSfidato() != null) {
						
						ArrayList<Quadrato> flotta = tmpGame.getFlottaAvversario(ut);
						
						for(Quadrato tmpQuad : flotta) {
							if(tmpQuad.getName().equals(quad.getName())) {
								
								//Comunico al serverThread il quadrato dell'avversario, in modo che poi al clientThread verrà notificato tale quadrato, per poter capire quale Nave ha colpito
								PoolThread.getThread(ut.getSESSION()).quadAvversario = tmpQuad;
								
								return true;
							}
						}
						
						return false;
						
					}else {
						//log("terzo IF errato");
					}
				}else {
					//log("secondo IF errato");
				}
				
				return false;
			}else {
				//log("primo IF errato");
			}
		}
		
		return false;
		
	}
	
	public synchronized static Game createGame(Utente sfidante, Utente sfidato) throws Exception{
		
		if(PoolThread.getThread(sfidante.getSESSION()).getMe().getStato() == "waiting for response") {
			if(PoolThread.getThread(sfidato.getSESSION()).getMe().getStato() == "free") {
				
				PoolThread.getThread(sfidato.getSESSION()).getMe().setStato("inGame");
				PoolThread.getThread(sfidante.getSESSION()).getMe().setStato("inGame");
				
				String tmpSessionGame = sfidante.getSESSION() + " - " + sfidato.getSESSION();
				
				Game tmpGame = new Game("Iniziale", sfidante, sfidato, tmpSessionGame);
				
				PoolThread.getThread(sfidante.getSESSION()).setCurrentGame(tmpGame);
				
				listaGame.add(tmpGame);
				
				return tmpGame;
				
			}else {
				throw new Exception("Lo sfidato (" + sfidato.getUsername() + ") non risulta libero per giocare (" + sfidato.getStato() + ")");
			}
		}else {
			throw new Exception("Lo sfidante (" + sfidante.getUsername() + ") non risulta libero per giocare (" + sfidante.getStato() + ")");
		}
		
	}
	
	public synchronized static void endGame(Utente vincitore, Utente perdente, Game game) {
		
		//il perdente è stato già notificato dal suo serverThread
		//quindi qui dobbiamo notificare il vincitore che ha vinto
		
		
		for(Game tmpGame : listaGame) {
			if(tmpGame.getSESSION().equals(game.getSESSION()) && !(tmpGame.getStato().equals("finito"))) {
				
				PoolThread.getThread(vincitore.getSESSION()).getMe().setStato("free");
				PoolThread.getThread(perdente.getSESSION()).getMe().setStato("free");
				
				try {
					PoolThread.getThread(vincitore.getSESSION()).send(new Pacchetto(CmdCommands.youWinTheGame,vincitore,null,true));
				} catch (NullPointerException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				tmpGame.setStato("finito");
				
				listaGame.remove(tmpGame);
				
				break;
			}
		}
		
	}
	
	public static void log(String s) {
		areaMessaggi.append(s + "\n"); //il metodo append è thread-safe
		 frame.repaint();
	}


}
