package server;

import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.SwingUtilities;

import comunicationObj.CmdCommands;
import comunicationObj.Game;
import comunicationObj.Pacchetto;
import comunicationObj.Utente;
import guiComponents.Nave;
import guiComponents.Quadrato;
 
/**
 * This thread is responsible to handle client connection.
 *
 * @author www.codejava.net
 */
public class ServerThread extends Thread {

	private Socket socket;
    private SocketAddress clientAdress;
    private String SESSION;
    private int localProgress;
    private String username;
    private boolean auth = false;
    private ObjectOutputStream writer;
    private Utente me = null;
    private Game currentGame = null;
    private int countWin;
    private ServerThread avversarioThread;
    public Quadrato quadAvversario;
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public ServerThread(Socket socket, String session) {
        this.socket = socket;
        this.clientAdress = socket.getRemoteSocketAddress();
        this.SESSION = session;
    }
    
    public boolean close() {
    	try {
			this.socket.close();
			
			return true;
			
		} catch (IOException e) {
			return false;
		}
    	
    }
    
    public void run() {
        try {
        	
            //OutputStream output = socket.getOutputStream();
            //writer = new ObjectOutputStream(output);
            
             writer = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            
            //InputStream input = socket.getInputStream();
            //ObjectInputStream reader = new ObjectInputStream(input);
            
            ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            
            Pacchetto inputMsg = null;
	            do {
	            	try {
	            		
	            		Pacchetto resp;
	            		synchronized (reader) {
	            			inputMsg = (Pacchetto)reader.readObject();
	            			MainServerXY.updatePanel(this, "<b>Ricevo:</b> " + inputMsg);
	            			//System.out.println("Leggo il pacchetto sull'EDT? " + SwingUtilities.isEventDispatchThread());
	            		}
	            		
	            			if(inputMsg.getFirstParam() instanceof Game) {
	            				
	            				Game tmpGame = (Game)inputMsg.getFirstParam();
	            				
	            				if(inputMsg.getSecondParam().equals(CmdCommands.setMyFlotta)) {
	            					 
	            					countWin = 0;
	            					
	            					if(MainServerXY.updateGameFlotta(tmpGame, me)) {
	            						
	            						Game tmpGameToSend = MainServerXY.checkIfReadyToStart(tmpGame);
	            						
	            						if(tmpGameToSend == null)	// ci manca ancora la flotta dell'avversario
	            							resp = new Pacchetto(CmdCommands.setMyFlottaOK,"",null,true);
	            						else {
	            							resp = new Pacchetto(tmpGameToSend,CmdCommands.letsPlay,null,true);
	            							PoolThread.getThread(tmpGame.getAvversario(me).getSESSION()).send(new Pacchetto(tmpGameToSend,CmdCommands.letsPlay,null,true));
	            						}
	            						
	            						avversarioThread = PoolThread.getThread(tmpGame.getAvversario(me).getSESSION());
	            						
	            					}else {
	            						resp = new Pacchetto("Impossibile aggiornare il gioco sul server","",null,false);
	            					}
	            				}else if(inputMsg.getSecondParam() instanceof Quadrato) {
	            					
	            					Quadrato tmpQuad = (Quadrato)inputMsg.getSecondParam();
	            					
	            					if(MainServerXY.checkMove(tmpGame,tmpQuad, me)) {
	            						
	            						tmpGame.setTurno(tmpGame.getAvversario(me));
	            						countWin++;
	            						
	            						if(countWin < currentGame.getMyFlotta(me).size()) {
	            						
	            							resp = new Pacchetto(tmpGame,CmdCommands.colpito, null, quadAvversario, true);
	            							avversarioThread.send(new Pacchetto(CmdCommands.setQuadRed,new StringBuilder(tmpQuad.getName()),null,true));
		            						
	            						}else {
	            							//questo non deve essere mandato, perchè verrà mandato un YOUWIN
	            							
	            							System.out.println("\n non hai vinto perchè hai colpito "+ countWin + "ma flottaSize è: "+ currentGame.getMyFlotta(me).size() + "\n");
	            							
	            							resp = new Pacchetto("","",null,true);
	            							
	            							MainServerXY.endGame(me, tmpGame.getAvversario(me), currentGame);
		            						
	            							avversarioThread.send(new Pacchetto(CmdCommands.youLostTheGame,inputMsg.getSecondParam(),null,true));
		            						currentGame = null;
		            						countWin = 0;
	            						}
	            						
	            					}else {
	            						
	            						tmpGame.setTurno(tmpGame.getAvversario(me));
	            						resp = new Pacchetto(tmpGame,CmdCommands.mancato,null, quadAvversario ,true);
	            						avversarioThread.send(new Pacchetto(CmdCommands.setQuadGreen,new StringBuilder(tmpQuad.getName()),null,true));
	            					}
	            					
	            					ArrayList<Integer> tmpRand = new ArrayList<Integer>();
	            					tmpRand.add(ThreadLocalRandom.current().nextInt(0, 100 + 1));
	            					
	            					avversarioThread.send(new Pacchetto(tmpGame,CmdCommands.playing,tmpRand,true)); //notifica all'avversario che è il suo turno
	            					
	            				}else if(inputMsg.getSecondParam().equals(CmdCommands.playing)) {
	            					
	            					avversarioThread.send(new Pacchetto(tmpGame,CmdCommands.playing,null,true));
	            					
	            					resp = new Pacchetto(tmpGame,CmdCommands.playingOK,null,false);
	            					
	            				}else {
	            					resp = new Pacchetto("dunno wut replay","",null,false);
	            				}
	            				
	            			}else if(inputMsg.getFirstParam().equals(CmdCommands.iLostTheGame)) {
	            				
	            				MainServerXY.endGame(currentGame.getAvversario(me), me, currentGame);
	            				
	            				resp = new Pacchetto(CmdCommands.youLostTheGame,inputMsg.getSecondParam(),null,true);
	            				
	            			}else if(inputMsg.getFirstParam().equals(CmdCommands.setMeFree)) {
	            				
	            				currentGame = null;
		            			me.setStato("free");
		            			
		            			resp = new Pacchetto("Il tuo serverThread ha resettato il tuo stato","",null,true);
		            			
		            			MainServerXY.updatePanel(this, "<b>INFO:</b> " + "Il mio clientThread mi ha detto di reimpostare il mio stato");
		            			
	            			}else if(inputMsg.getFirstParam().equals(CmdCommands.hello)) {
		            			
			            		resp = new Pacchetto("Benvenuto nel server !!","",null,true);
				                
			            		localProgress = 3;
			            		
		            		}else if(inputMsg.getFirstParam().equals(CmdCommands.authMe)) {
		            			
		            			if(me == null) {
			            			if(inputMsg.getSecondParam().toString().length() > 2) {
			            				
			            				MainServerXY.updateStatePanel(this, "<html><b><font color=\"green\">Autenticato</font></b> (" + inputMsg.getSecondParam() + ")</html>");
			            				
			            				auth = true;
			            				username = (String)inputMsg.getSecondParam();
			            				me = new Utente(username, SESSION, "free");
			            				
			            				PoolThread.notifyMyPresence(this);
			            				
			            				resp = new Pacchetto(me,CmdCommands.authMe,null,true);
			            				
			            				localProgress = 4;
			            				
			            			}else {
			            				
			            				resp = new Pacchetto("Specificare un nome > 2","",null,false);
		   
			            			}
		            			}else
		            				resp = new Pacchetto("SEI GIA AUTENTICATO","",null,false);
				                
		            		}else if(localProgress == 4) {
		            			
	            				ArrayList<Utente> tmpList = MainServerXY.onlineThreadsWitoutMe(this);
	            				
	            				if(!tmpList.isEmpty()) {
	            					resp = new Pacchetto(CmdCommands.notifyMyPresence,"",tmpList,true);
	            				}else {
	            					resp = new Pacchetto(CmdCommands.notifyMyPresence,"Sei solo",null,true);
	            				}
	            				
	            				localProgress = 5;
	            				
	            			}else if(inputMsg.getFirstParam().equals(CmdCommands.wannaPlayWith)) {
		            			
	            				if(me.getStato() == "free") {//questo IF permette di poter interagire con una sola persona alla volta
	            				
		            				if(inputMsg.getSecondParam() instanceof Utente) {
		            					
		            					Utente destinatarioUtente = (Utente)inputMsg.getSecondParam();
		            					if(PoolThread.getThread(destinatarioUtente.getSESSION()).getMe().getStato() == "free") {//se il destinatario non è già occupato (lo stato poteva essere preso anche dal secondo paramentro del pacchetto, ma prenderlo dal pooldithread garantisce una sincronizzazione migliore)
			            					PoolThread.getThread(destinatarioUtente.getSESSION()).send(new Pacchetto(CmdCommands.wannaPlayWith,me,null,true));
				            				
				            				me.setStato("waiting for response");
				            				
				            				resp = new Pacchetto(CmdCommands.waitingForResponse,destinatarioUtente.getUsername(),null,true);
		            					}else {
		            						resp = new Pacchetto(CmdCommands.userBusy,"",null,false);
		            					}
		            				}else {
		            					resp = new Pacchetto("Il secondo parametro inviato non è un Utente","",null,false);
		            				}
	            				}else {
	            					resp = new Pacchetto("Stai già interagendo con qualcuno -> "+ me.getStato(),"",null,false);
	            				}
	            				
	            			}else if(me.getStato() == "free") {
	            					
	            					if(inputMsg.getSecondParam() instanceof Utente) {
	            					
	            						Utente destinatarioUtente = (Utente)inputMsg.getSecondParam();
	            						
			            				if(inputMsg.getFirstParam().equals(CmdCommands.answerYES)) {
			            					
			            					//destinatarioUtente.getSESSION() =  sfidato !!
			            					//me = sfidante
			            					
			            					Game tmpGame = null;
			            					Pacchetto tmpPack = null;
			            					
			            					try{
			            						
			            						tmpGame = MainServerXY.createGame(destinatarioUtente, me);
			            						
			            						PoolThread.getThread(destinatarioUtente.getSESSION()).getMe().setStato("inGame");
			            						me.setStato("inGame");
			            						
			            					}catch(Exception e) {
			            						e.printStackTrace();
			            						tmpPack = new Pacchetto(e.getMessage(),CmdCommands.setMeFree,null,false);
			            					}
			            					
			            					
			            					if(tmpGame != null) {
			            						
			            						tmpPack = new Pacchetto(tmpGame,CmdCommands.gameStart,null,true);
			            						
			            						currentGame = tmpGame;
			            					}
			            					
			            					PoolThread.getThread(destinatarioUtente.getSESSION()).send(tmpPack);
			            					
		            						resp = tmpPack;
			            					
			            				}else if(inputMsg.getFirstParam().equals(CmdCommands.answerNO)) {
			            					
			            					me.setStato("free");
			            					
			            					PoolThread.getThread(destinatarioUtente.getSESSION()).send(new Pacchetto(CmdCommands.answerNO,me,null,true));
			            					
			            					resp = new Pacchetto("In attesa...","",null,true);
			            					
			            					PoolThread.getThread(destinatarioUtente.getSESSION()).localProgress = 5;
			            					
			            					PoolThread.getThread(destinatarioUtente.getSESSION()).getMe().setStato("free");
			            					
			            				}else {
			            					resp = new Pacchetto("FATAL ERROR, risposta sconosciuta " + inputMsg.getFirstParam(),"",null,false);
			            				}
		            				
	            					}else {
	            						resp = new Pacchetto("FATAL ERROR, non c'è il parametro utente " + inputMsg.getFirstParam(),"",null,false);
	            					}
		            				
		            				
	            				}else {
	            					resp = new Pacchetto("Non so cosa risponderti, oppure Stai già interagendo con qualcuno -> "+ me.getStato(),"",null,false);
	            				}
		            	
	            		
		               send(resp);
		               
	            	} catch(SocketException e) {
	            		
	            		disconnectFromAnyGame();
	            		
	            		if(socket != null) { //se il socket è stato kickato già dal poolthread è inutile chiuderlo di nuovo
		            		socket.close();
		            		System.out.println("Socket not null"+e.getClass());
	            		}
	            		
	            		MainServerXY.updateStatePanel(this, "<html><b><font color=\"red\">Offline</font></b></html>");
	            		
	            		//System.out.println("Class-> "+e.getClass()+"\n C'è un problema col socket, quindi lo metto offline");
	            		
	            	} catch(IOException e) {//eccezzione generata dall'invio del pacchetto o dal reader (EOFException)
	            		
	            		disconnectFromAnyGame();
	            		
	            		socket.close();
	            		MainServerXY.updateStatePanel(this, "<html><b><font color=\"red\">Offline</font></b></html>");
	            		
	            		//System.out.println("Class-> "+e.getClass());
	            		e.printStackTrace();
	            		
	            	} catch(NullPointerException e) {//eccezzione generata da getThread di poolThread
	            		
	            		disconnectFromAnyGame();
	            		
	            		socket.close();
	            		
	            		//System.out.println("Class-> "+e.getClass());
	            		e.printStackTrace();
	            		
	            	} catch(ClassNotFoundException e) {//eccezzione generata dalla lettura dell'oggetto readObject
	            		
	            		//System.out.println("Class-> "+e.getClass());
	            		e.printStackTrace();
	            		
	            	}
	            	
	            } while (!socket.isClosed());
            
            //socket.close();
            
            if(socket.isClosed()) {
            	
            	try {
					if(!PoolThread.deleteSocket(this, this.SESSION)) {
						System.out.println("Errore nell'eliminazione dal PoolThread");
					}
				} catch (Exception e) {
					
					// TODO Auto-generated catch block
					System.out.println("Errore nell'eliminazione dal PoolThread (Catched)");
					e.printStackTrace();
				}
            	
            }else {
            	throw new IOException("ERRORE ELIMINAZIONE THREAD");
            }
            
            
        } catch (IOException ex) {
        	
            //System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NullPointerException ex) {
        	
        	if(socket != null) { //se il socket è stato kickato già dal poolthread è inutile chiuderlo di nuovo
        		try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		MainServerXY.updateStatePanel(this, "<html><b><font color=\"red\">Offline</font></b></html>");
            //System.out.println("Il socket è diventato null, quindi la condizione nel while per l'ascolto perpetuo ha generato un nullPointerException, ma è tutto sotto controllo-> " + ex.getMessage());
            
			try {
				PoolThread.deleteSocket(this, SESSION);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    public static int sizeof(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length;
    }
    
	public synchronized void send(Pacchetto p) throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		
		writer.writeObject(p);
		System.out.println("(1) WRITE took " + (System.currentTimeMillis() - startTime) + " milliseconds, and that's what i wrote:");
		System.out.println(p);
		System.out.println("SIZE of Packet: "+sizeof(p));
		System.out.println("SIZE of FP: "+sizeof(p.getFirstParam()));
		System.out.println("");
		writer.flush();
		writer.reset();
		
        //MainServerXY.updatePanel(this, "<b>Rispondo:</b> " + p);
	}
	
	private void disconnectFromAnyGame() {
		
				Utente avversario = currentGame.getAvversario(me);
				
					try {
						PoolThread.getThread(avversario.getSESSION()).send(new Pacchetto(CmdCommands.setMeFree,me,null,true));
					} catch (NullPointerException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					PoolThread.getThread(avversario.getSESSION()).setCurrentGame(null);
					
					//System.out.println("Ho inviato il setMeFree a "+avversario.getUsername());
					
					MainServerXY.updatePanel(this, "<b>Rispondo:</b> " + "ho inviato un comando setMeFree all'utente " + avversario.getUsername());
					
					
			currentGame = null;
			
			//System.out.println("Ho disconnesso un game");
		
	}
	
	public Nave checkQuadNave(Quadrato quad, Game game) {
		return null;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getSESSION() {
		return SESSION;
	}

	public void setSESSION(String sESSION) {
		SESSION = sESSION;
	}

	public Utente getMe() {
		return me;
	}

	public void setMe(Utente me) {
		this.me = me;
	}

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}
}