package controller;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import comunicationObj.CmdCommands;
import comunicationObj.Game;
import comunicationObj.LogMessage;
import comunicationObj.Pacchetto;
import comunicationObj.Utente;
import guiComponents.Nave;
import guiComponents.Quadrato;
import view.MainFrame;

public class ClientThread extends Thread{
	
	private int port;
	private String hostname;
	private ObjectOutputStream writer;
	private Socket socket;
	private ObjectInputStream reader;
	private boolean mayIWrite = false;
	private boolean mayIListen = false;
	private Utente me;
	private String username;
	private MainFrame MF;
	private Game currentGame;
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
	private ArrayList<Nave> naviColpite = new ArrayList<Nave>();
	private int quadColpiti;
	
	public Socket getSocket() {
		return socket;
	}

	public ClientThread(String hostname, int port, String usr, MainFrame MF){
    	this.port = port;
    	this.hostname = hostname;
    	this.username = usr;
    	this.MF = MF;
    }
	
	public void tryConnect() {
		 System.out.println("i am here 0");
        try  {
        	Socket socket = new Socket(hostname, port);
        	mayIWrite = true;
        	mayIListen = true;
        	MF.updateLog(new LogMessage("Socket","Connessione stabilita con: " + hostname + ":" + port,dtf.format(LocalTime.now())));

        	 System.out.println("i am here 11");
            writer = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("i am here 111");
            
            writer.writeObject(new Pacchetto(CmdCommands.hello,"",null,true));
            System.out.println("i am here 13");
            mayIWrite = true;
            
            this.socket = socket;
            System.out.println("i am here 14");
            try {
				send(new Pacchetto(CmdCommands.authMe, username, null, true));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            reader = new ObjectInputStream(socket.getInputStream());
            System.out.println("i am here 12");
            Thread t = new Thread("my non EDT thread") {
   	            public void run() {
   	                //my work
   	            	MF.getLoadingPage().esci(MF.lobbyPage);
   	            }
   	        };
   	        t.start();
            
   	        System.out.println("i am here 1");
   	        
            Ascolta();
            
        } catch (UnknownHostException ex) {
 
        	MF.updateLog(new LogMessage("Socket","Server not found: " + ex.getMessage(),dtf.format(LocalTime.now())));
        	
        	MF.lblError.setText(MF.normalTxtError);
        	MF.lblError.setText(MainFrame.addWordBeforeTag(MF.lblError.getText(), "</font>", "Ci dispiace comandante, l'indirizzo del server � sconosciuto !!"));
        	
        	Thread t = new Thread("my non EDT thread") {
   	            public void run() {
   	                //my work
   	            	MF.getLoadingPage().esci(MF.errorPage);
   	            }
   	        };
   	        t.start();
        	
        	MF.destroyClientThread();
        	
        	 System.out.println("i am here 2");
        } catch (IOException ex) {
 
        	MF.updateLog(new LogMessage("Socket","I/O Error: " + ex.getMessage(),dtf.format(LocalTime.now())));
        	
        	MF.lblError.setText(MF.normalTxtError);
        	MF.lblError.setText(MainFrame.addWordBeforeTag(MF.lblError.getText(), "</font>", "Ci dispiace comandante, il server sembra essere offline !!"));
        	
        	Thread t = new Thread("my non EDT thread") {
   	            public void run() {
   	                //my work
   	            	MF.getLoadingPage().esci(MF.errorPage);
   	            }
   	        };
   	        t.start();
        	
        	MF.destroyClientThread();
        	
        	 System.out.println("i am here 3");
        	
        } finally {
        	 System.out.println("i am here 4");
        }
        System.out.println("i am here 5");
        
	}
	
	public void run() {
		this.tryConnect();
	}
	
	public void Close() throws Exception {
		socket.close();
		
		if(!socket.isClosed())
			throw new Exception("errore nella chiusura del socket");
	}
	
	   private void Ascolta() {
		   
	       Runnable ascoltatore = new Runnable() {
	       	public void run() {
	       		
	           	//System.out.println("Sto ascoltando");
	           
	           	do {
	           		
	           		if(mayIListen == true)
	           		
			       		try {
			       			
			   	            do {
			   	            	
			   	            	Pacchetto msg = (Pacchetto)reader.readObject();
			   	            	//System.out.println("Leggo il pacchetto sull'EDT? " + SwingUtilities.isEventDispatchThread());
			   	            	
			   	            	if(msg == null) 
			   	            		socket.close();
			   	            	else {
			   	            		
			   	            		if(msg.isResult()) {
			   	            			
			   	            			if(!(msg.getFirstParam() instanceof Game)) {
			   	            				if(msg.getFirstParam().equals(CmdCommands.welcome)){
					   	            			
			   	            					MF.updateLog(new LogMessage("System","Il Server ti ha dato il benvenuto !!",dtf.format(LocalTime.now())));
			   	            					
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.setQuadGreen)){
		   	            			
			   	            					StringBuilder tmpQuad = (StringBuilder)msg.getSecondParam();
					   	            	        
			   	            					MF.griglia.setQuadGreen(tmpQuad.toString());
			   	            					
			   	            					if(MF.buttonCheck_ViewGriglia.isClicked()) {//splittedView � attivo
			   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Aspettando 1,5 Secondi...</font></div></center></html>");
			   	            						try {
														Thread.sleep(1500);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
														new LogMessage("System","ERRORE nel thread a causa di sleep: "+e.getMessage(),dtf.format(LocalTime.now()));
													}
			   	            					}
			   	            					
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.setQuadRed)){
					   	            			
			   	            					StringBuilder tmpQuad = (StringBuilder)msg.getSecondParam();
					   	            	        
			   	            					MF.griglia.setQuadRed(tmpQuad.toString());
			   	            					
			   	            					if(MF.buttonCheck_ViewGriglia.isClicked()) {//splittedView � attivo
			   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Aspettando 1,5 Secondi...</font></div></center></html>");
			   	            						try {
														Thread.sleep(1500);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
														new LogMessage("System","ERRORE nel thread a causa di sleep: "+e.getMessage(),dtf.format(LocalTime.now()));
													}
			   	            					}
			   	            					
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.setMyFlottaOK)){
					   	            			
			   	            					//la tua flotta � stata settata correttamente
			   	            					
			   	            					MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Stai giocando contro "+currentGame.getAvversario(me).getUsername()+" - In attesa della flotta avversaria.. </font></div></center></html>");
					   	            			MF.updateGUI();
					   	            	        
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.youWinTheGame)){
					   	            			
					   	            			MF.updateLog(new LogMessage("Game","Hai Vinto la partita!!",dtf.format(LocalTime.now())));
					   	            			
					   	            			MF.playWinningSound();
					   	            			
					   	            			MF.getPagNavi().getFrame2().setVisible(false);
					   	            			
					   	            			currentGame = null;
					   	            			MF.resetGame();
					   	            			
					   	            			Thread t = new Thread("my non EDT thread") {
					   	            	            public void run() {
					   	            	                //my work
					   	            	            	MF.currentPage.esci(MF.youwinPage);
					   	            	            }
					   	            	        };
					   	            	        t.start();
					   	            			
					   	            	        
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.youLostTheGame)){
					   	            			
					   	            			MF.updateLog(new LogMessage("Game","Hai perso la partita per questa motivazione: "+ msg.getSecondParam(),dtf.format(LocalTime.now())));
					   	            			
					   	            			MF.playLosingSound();
					   	            			
					   	            			MF.getPagNavi().getFrame2().setVisible(false);
					   	            			
					   	            			currentGame = null;
					   	            			MF.resetGame();
					   	            			
					   	            			Thread t = new Thread("my non EDT thread") {
					   	            	            public void run() {
					   	            	                //my work
					   	            	            	MF.currentPage.esci(MF.youlostPage);
					   	            	            }
					   	            	        };
					   	            	        t.start();
					   	            			
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.setMeFree)){
					   	            			
					   	            			MF.updateLog(new LogMessage("Game","L'utente "+ ((Utente)msg.getSecondParam()).getUsername() + " � andato offline, per questo terminiamo la partita",dtf.format(LocalTime.now())));
					   	            			
					   	            			currentGame = null;
					   	            			MF.resetGame();
					   	            			
					   	            			if(MF.timer.isRunning()) {
					   	            				MF.timer.stop();
					   	            				MF.secondi = 15;
					   	            			}
					   	            			
					   	            			MF.getPagNavi().getFrame2().setVisible(false);
					   	            			
					   	            			if(MF.getCurrentPage() == MF.gamePage) {
						   	            			Thread t = new Thread("my non EDT thread") {
						   	            	            public void run() {
						   	            	                //my work
						   	            	            	MF.getCurrentPage().esci(MF.lobbyPage);
						   	            	            }
						   	            	        };
						   	            	        t.start();
					   	            			}
					   	            			
					   	            			Pacchetto resp = new Pacchetto(CmdCommands.setMeFree,"",null,true);
					   	            			
					   	            			send(resp);
					   	            			
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.hello)){
					   	            			
					   	            			MF.updateLog(new LogMessage("Socket","Scambio di \"hello\" con il server",dtf.format(LocalTime.now())));
					   	            			
					   	            		}else if(msg.getSecondParam().equals(CmdCommands.authMe)){
					   	            			
					   	            			me = (Utente)msg.getFirstParam();
					   	            			username = (String)me.getUsername();
					   	            			
					   	            			MF.myData.setMe(me);
					   	            			
					   	            			Pacchetto resp = new Pacchetto(CmdCommands.gimmeUsrList,"",null,true);
					   	            			
					   	            			send(resp);
					   	            			
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.notifyMyPresence)){	//restituzione utenti online
					   	            			if(!msg.getSecondParam().equals("Sei solo")) {
					   	            				
					   	            				if(msg.getThirdParam() instanceof ArrayList<?> && msg.getThirdParam().get(0) instanceof Utente) {
					   	            					
					   	            					//se le condizioni dell'if sono verificate, il Serverthread avr� sicuramente inviato un tipo Utente
					   	            					ArrayList<Utente> tmpList = (ArrayList<Utente>)msg.getThirdParam();
					   	            					
					   	            					MF.updateLog(new LogMessage("System","Ecco le persone online:",dtf.format(LocalTime.now())));
					   	            					
					   	            					MF.myData.getListModel().removeAllElements(); //siamo nel controller e quindi posso permettermi di modificare il Model
					   	            					
						   	            				for(Utente sthr : tmpList) {
						   	            					MF.updateLog(new LogMessage("System","Nome: " + sthr.getUsername() + " Session: " + sthr.getSESSION(),dtf.format(LocalTime.now())));
						   	            					MF.myData.getListModel().addElement(sthr);
						   	            				}
						   	            				
						   	            				MF.lblLobby.setText(MainFrame.addWordBeforeTag(MF.normalTxtLobby, "</font>", String.valueOf(MF.myData.getListModel().getSize())));
						   	            				
						   	            				MF.updateGUI();
					   	            				}else {
					   	            					MF.updateLog(new LogMessage("System","La lista degli online non � un ArrayList",dtf.format(LocalTime.now())));
					   	            				}
					   	            			}else {
					   	            				MF.myData.getListModel().removeAllElements();
					   	            				MF.lblLobby.setText(MainFrame.addWordBeforeTag(MF.normalTxtLobby, "</font>", "0"));
					   	            				MF.updateLog(new LogMessage("System","Non c'� nessuno online oltre te",dtf.format(LocalTime.now())));
					   	            			}
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.wannaPlayWith)){
					   	            			
				   	            				if(msg.getSecondParam() instanceof Utente) {
				   	            					Utente tmpUtente = (Utente)msg.getSecondParam();
				   	            					
				   	            					MF.updateLog(new LogMessage("Game","Richiesta di gioca ricevuta: " + tmpUtente.getUsername() + " vuole giocare con te",dtf.format(LocalTime.now())));
				   	            					
				   	            					MF.startDialogue(tmpUtente);
				   	            					
				   	            				}else {
				   	            					MF.updateLog(new LogMessage("Game","FATAL ERROR: lo sfidante non � un utente valido",dtf.format(LocalTime.now())));
				   	            				}
					   	            			
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.waitingForResponse)){
					   	            			MF.updateLog(new LogMessage("Game","Richiesta di gioco inoltrata... siamo in attesa di un responso...",dtf.format(LocalTime.now())));
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.userBusy)){
					   	            			
					   	            			MF.updateLog(new LogMessage("Game","L'utente non pu� essere sfidato in quanto � gi� impegnato in un'altra partita",dtf.format(LocalTime.now())));
					   	            			
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.answerNO)){
					   	            			
					   	            			MF.updateLog(new LogMessage("Game","L'utente ha declinato il tuo invito!",dtf.format(LocalTime.now())));
					   	            			
					   	            		}else if(msg.getFirstParam().equals(CmdCommands.answerYES)){
					   	            			
					   	            			MF.updateLog(new LogMessage("Game","L'utente ha accettato il tuo invito!",dtf.format(LocalTime.now())));
					   	            			
					   	            		}else {
					   	            			MF.updateLog(new LogMessage("System","FATAL ERROR: dunno wut to do",dtf.format(LocalTime.now())));
					   	            		}
					   	            		MF.updateLog(new LogMessage("Socket","RICEVO: " + msg,dtf.format(LocalTime.now())));
					   	            		
			   	            			}else { //il primo paramentro � instanceof Game
			   	            				
			   	            				Game tmpGame = (Game)msg.getFirstParam();
			   	            				
			   	            				currentGame = tmpGame;
		   	            					
		   	            					MF.myData.game = currentGame;
			   	            				
			   	            				if(msg.getSecondParam().equals(CmdCommands.gameStart)) {
			   	            					
			   	            					Thread t = new Thread("my non EDT thread") {
			   	        		    	            public void run() {
			   	        		    	                //my work
			   	        		    	            	MF.currentPage.esci(MF.gamePage);
			   	        		    	            }
			   	        		    	        };
			   	        		    	        t.start();
			   	        						
			   	            					MF.lobbyPage.setVisible(false);
			   	            					
			   	            					MF.gamePage.repaint();
			   	            					
			   	            					MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Stai giocando contro "+tmpGame.getAvversario(me).getUsername()+" - Posiziona la flotta!! </font></div></center></html>");
			   	            					MF.getPagNavi().getFrame2().setVisible(true);
			   	            					
			   	            					MF.lblTimer.setVisible(true);
			   	            					MF.secondi = 25;
			   	            					
			   	            					MF.timer.start();
			   	            					
			   	            				}else if(msg.getSecondParam().equals(CmdCommands.letsPlay)) {
			   	            					
			   	            					MF.getPagNavi().getFrame2().setVisible(false);
			   	            					MF.griglia.setNaveClickata(null);
			   	            					
			   	            					MF.lblTimer.setVisible(false);
			   	            					
			   	            					if(MF.myData.game.getStato().equals("Iniziale"))
			   	            						MF.myData.game.setStato("iniziato");
			   	            					
			   	            					if(!MF.pannelloStats.isEntrata())
			   	            						MF.pannelloStats.entra();
			   	            					
			   	            					if(tmpGame.getTurno().getSESSION().equals(me.getSESSION())) { // � il mio turno
			   	            						
				   	            					if(MF.buttonCheck_ViewGriglia.isClicked()) {//splittedView � attivo
				   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Aspettando 1,5 Secondi...</font></div></center></html>");
				   	            						try {
															Thread.sleep(1500);
														} catch (InterruptedException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
															new LogMessage("System","ERRORE nel thread a causa di sleep: "+e.getMessage(),dtf.format(LocalTime.now()));
														}
				   	            					}
			   	            						
			   	            						MF.griglia.setMyTurno(true);
			   	            						
			   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Stai giocando contro "+tmpGame.getAvversario(me).getUsername()+" - Fai la tua mossa!! </font></div></center></html>");
			   	            						
			   	            						MF.updateGUI();
			   	            						
			   	            					}else {
			   	            						
				   	            					if(MF.buttonCheck_ViewGriglia.isClicked()) {//splittedView � attivo
				   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Aspettando 1,5 Secondi...</font></div></center></html>");
				   	            						try {
															Thread.sleep(1500);
														} catch (InterruptedException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
															new LogMessage("System","ERRORE nel thread a causa di sleep: "+e.getMessage(),dtf.format(LocalTime.now()));
														}
				   	            					}
			   	            						
			   	            						MF.griglia.setMyTurno(false);
			   	            						
			   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Stai giocando contro "+tmpGame.getAvversario(me).getUsername()+" - in attesa dell'avversario... </font></div></center></html>");
			   	            						
			   	            						MF.updateGUI();
			   	            					}
			   	            				}else if(msg.getSecondParam().equals(CmdCommands.playing)) {
			   	            					
			   	            					MF.lblTimer.setVisible(false);
			   	            					
			   	            					if(MF.myData.game.getStato().equals("Iniziale"))
			   	            						MF.myData.game.setStato("iniziato");
			   	            					
			   	            					if(tmpGame.getTurno().getSESSION().equals(me.getSESSION())) { // � il mio turno
			   	            						
			   	            						MF.griglia.setMyTurno(true);
			   	            						
			   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Stai giocando contro "+tmpGame.getAvversario(me).getUsername()+" - Fai la tua mossa!! </font></div></center></html>");
			   	            						
			   	            						MF.updateGUI();
			   	            						
			   	            					}else {
			   	            						
			   	            						MF.griglia.setMyTurno(false);
			   	            						
			   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Stai giocando contro "+tmpGame.getAvversario(me).getUsername()+" - in attesa dell'avversario... </font></div></center></html>");
			   	            						
			   	            						MF.updateGUI();
			   	            					}
			   	            				}else if(msg.getSecondParam().equals(CmdCommands.mancato)) {
			   	            					
			   	            					MF.griglia.getStackQuad().get(MF.griglia.getStackQuad().size()-1).setImageStatus("mancato");
			   	            					
			   	            					//Calcolo percentuale precisione->  x : 100 = colpiti : tot
		   	            						int newPercentuale = (int)(quadColpiti*100)/MF.griglia.getStackQuad().size();
		   	            						MF.pannelloStats.setTerzoPaneNumber(String.valueOf(newPercentuale)+"%");
		   	            						
			   	            					if(MF.buttonCheck_ViewGriglia.isClicked()) {//splittedView � attivo
			   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Aspettando 1,5 Secondi...</font></div></center></html>");
			   	            						try {
														Thread.sleep(1500);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
														new LogMessage("System","ERRORE nel thread a causa di sleep: "+e.getMessage(),dtf.format(LocalTime.now()));
													}
			   	            					}
		   	            						
			   	            					MF.griglia.setMyTurno(false);
			   	            					
			   	            					MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Stai giocando contro "+tmpGame.getAvversario(me).getUsername()+" -in attesa dell'avversario... </font></div></center></html>");
			   	            					
			   	            				}else if(msg.getSecondParam().equals(CmdCommands.colpito)) {
			   	            					
			   	            					MF.griglia.getStackQuad().get(MF.griglia.getStackQuad().size()-1).setImageStatus("colpito");
			   	            					
			   	            					if(msg.getLastParam() instanceof Quadrato) {
			   	            						
			   	            						quadColpiti++;
			   	            						
			   	            						Quadrato tmpQuad = (Quadrato)msg.getLastParam();
			   	            						
			   	            						tmpQuad.getNaveAssociata();
			   	            						tmpQuad.getNaveAssociata().getNome();
			   	            						
			   	            						boolean foundNav = false;
			   	            						
			   	            						for(Nave nav : naviColpite) {
			   	            							if(nav.getNome().equals(tmpQuad.getNaveAssociata().getNome())) {
			   	            								foundNav = true;
			   	            							}
			   	            						}
			   	            						
			   	            						if(!foundNav) {
			   	            							naviColpite.add(tmpQuad.getNaveAssociata());
			   	            							
			   	            							MF.pannelloStats.setPrimoPaneNumber(String.valueOf(naviColpite.size()));
			   	            						}
			   	            						
			   	            						for(Nave nav : naviColpite) {
			   	            							if(nav.getNome().equals(tmpQuad.getNaveAssociata().getNome())) {
			   	            								
			   	            								if(nav.getQuadratiColpiti()!=null) {
			   	            									nav.getQuadratiColpiti().add(tmpQuad);
			   	            								}else {
			   	            									ArrayList<Quadrato> tmpQuads = new ArrayList<Quadrato>();
			   	            									tmpQuads.add(tmpQuad);
			   	            									nav.setQuadratiColpiti(tmpQuads);
			   	            								}
			   	            								
			   	            								if(nav.getQuadratiColpiti().size() == nav.getGrandezzaQuadrati()) {
			   	            									MF.pannelloStats.setSecondoPaneNumber(String.valueOf((Integer.valueOf(MF.pannelloStats.getSecondaStringa())+1)));
			   	            								}
			   	            							}
			   	            						}
			   	            						
			   	            						//Calcolo percentuale precisione->  x : 100 = colpiti : tot
			   	            						int newPercentuale = (int)(quadColpiti*100)/MF.griglia.getStackQuad().size();
			   	            						MF.pannelloStats.setTerzoPaneNumber(String.valueOf(newPercentuale)+"%");
			   	            						
			   	            						
			   	            					}else {
			   	            						MF.updateLog(new LogMessage("Game","ERRORE: non � stato possibile aggiornare le statistiche",dtf.format(LocalTime.now())));
			   	            					}
			   	            					
			   	            					if(MF.buttonCheck_ViewGriglia.isClicked()) {//splittedView � attivo
			   	            						MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Aspettando 1,5 Secondi...</font></div></center></html>");
			   	            						try {
														Thread.sleep(1500);
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
														new LogMessage("System","ERRORE nel thread a causa di sleep: "+e.getMessage(),dtf.format(LocalTime.now()));
													}
			   	            					}
			   	            					
			   	            					MF.griglia.setMyTurno(false);
			   	            					
			   	            					MF.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+MF.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + MainFrame.font.getFamily() + ";\">Stai giocando contro "+tmpGame.getAvversario(me).getUsername()+" -in attesa dell'avversario... </font></div></center></html>");

			   	            				}
			   	            				
			   	            			}
					   	            		
			   	            		}else {
			   	            			MF.updateLog(new LogMessage("System","ERRORE: " + msg.getFirstParam(),dtf.format(LocalTime.now())));
			   	            			
			   	            			if(msg.getFirstParam().equals(CmdCommands.setMeFree)) {
			   	            				me.setStato("free");
			   	            				MF.addMyPages(false);
			   	            			}
			   	            			
			   	            		}
			   	            	}
			   	            } while (mayIListen == true && !socket.isClosed());
			       			
			       		} catch (IOException e) {//Il server � andato offline o ha chiuso la connessione
			       			
			       			e.printStackTrace();
			       			
							MF.updateLog(new LogMessage("Socket","Il Server (socket) � offline",dtf.format(LocalTime.now())));
							MF.updateLog(new LogMessage("Socket",e.getMessage(),dtf.format(LocalTime.now())));
							
			       			try {
								Close(); 
							} catch (Exception e1) {
								System.out.println("Non sono riuscito a chiudere la connessione per questo motivo:");
								e1.printStackTrace();
							}
			       			
			       		} catch (ClassNotFoundException e) {
			       			
			       			e.printStackTrace();
			       			
			       		}
	           		
	           	} while(!socket.isClosed());
	           	
	           	if(socket.isClosed()) {
	           		mayIWrite = false;
	           		mayIListen = false;
	           	}
	           	
	       	}
	       };
	       
	       ascoltatore.run();
		   
	   }
	
	public synchronized void send(Pacchetto p) throws IOException{
		if(mayIWrite == true) {
			
				writer.writeObject(p);
				
				writer.flush();
				writer.reset();
				
				MF.updateLog(new LogMessage("Socket","INVIO: " + p,dtf.format(LocalTime.now())));
		} else {
			throw new IOException("mayIWrite is false");
		}
	}

	public ArrayList<Nave> getNaviColpite() {
		return naviColpite;
	}

	public void setNaviColpite(ArrayList<Nave> naviColpite) {
		this.naviColpite = naviColpite;
	}

	public int getQuadColpiti() {
		return quadColpiti;
	}

	public void setQuadColpiti(int quadColpiti) {
		this.quadColpiti = quadColpiti;
	}
	
}
