package server;

import java.util.ArrayList;

import comunicationObj.CmdCommands;
import comunicationObj.Pacchetto;
import comunicationObj.Utente;

public class PoolThread {
	
	private int MAX_CONNS;
	private static ArrayList<ServerThread> listaThread = new ArrayList<ServerThread>();
	
	PoolThread(int max_conns){
		this.MAX_CONNS = max_conns;
	}

	public synchronized ArrayList<ServerThread> getListaThread() {
		return listaThread;
	}
	
	/*
	public void setListaThread(ArrayList<ServerThread> listaThread) {
		PoolThread.listaThread = listaThread;
	}
	*/
	
	public synchronized static void notifyMyPresence(ServerThread st) {
		//Dice agli altri thread di notificare ai loro client che la lista degli utenti online autenticati è cambiata
		
		if(listaThread.size() > 1) { // se c'è già un ServerThread oltre quello attuale
			
			for(ServerThread sthr : listaThread ) {
				if(!sthr.getSESSION().equals(st.getSESSION()) && sthr.isAuth()) {
					
					ArrayList<Utente> tmpList = new ArrayList<Utente>();
					
					for(ServerThread sthr2 : listaThread) {
						if(!sthr2.getSESSION().equals(sthr.getSESSION()) && sthr2.isAuth()) {
							tmpList.add(new Utente(sthr2.getUsername(), sthr2.getSESSION(), "free"));
						}
					}
					
					try {
						sthr.send(new Pacchetto(CmdCommands.notifyMyPresence,"",tmpList,true));
					} catch (Exception e) {
						System.out.println("Impossibile mandare la lista degli utenti aggiornata automaticamente");
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	public synchronized void addThread(ServerThread st) throws Exception{
		if(listaThread.size() < MAX_CONNS) {
			
			listaThread.add(st);
			
		}else
			throw new Exception("LIMITE DI CONNESSIONI MULTIPLE RAGGIUNTO");
	}
	
	public synchronized static ServerThread getThread(String sess) throws NullPointerException{
		
		for(ServerThread st : listaThread) {
			if(st.getSESSION().equals(sess))
				return st;
		}
		
		throw new NullPointerException("ServerThread non trovato nel PoolThread");
	}
	
	public synchronized static boolean deleteSocket(ServerThread thread, String SESSION) throws Exception { //elimina il socket ed il token di sessione
		
		int count = 0; 		
	      while (count < listaThread.size()) {
	    	  
		  		if(listaThread.get(count).getSESSION().equals(SESSION)) {
		  			
		  			if(!listaThread.get(count).getSocket().isClosed())
		  				listaThread.get(count).getSocket().close();
		  			
		  			listaThread.get(count).setAuth(false);
					listaThread.get(count).setSocket(null);
					listaThread.get(count).setSESSION(null);
					listaThread.remove(listaThread.get(count));
					
					for(ServerThread sthr : listaThread ) {	//dico ai ServerThread restanti che uno di loro è stato kickato, e quindi si aggiorna la lista online
						if(sthr.isAuth()) {
							
							ArrayList<Utente> tmpList = new ArrayList<Utente>();
							
							for(ServerThread sthr2 : listaThread) {
								if(!sthr2.getSESSION().equals(sthr.getSESSION()) && sthr2.isAuth()) {
									tmpList.add(new Utente(sthr2.getUsername(), sthr2.getSESSION(), "free"));
								}
							}
							
							try {
								if(tmpList.size() > 0)
									sthr.send(new Pacchetto(CmdCommands.notifyMyPresence,"",tmpList,true));
								else //Sei solo
									sthr.send(new Pacchetto(CmdCommands.notifyMyPresence,"Sei solo",null,true));
							} catch (Exception e) {
								System.out.println("Impossibile mandare la lista degli utenti aggiornata automaticamente");
								e.printStackTrace();
							}
							
						}
					}
					
					return true;
				}
	  		
	    	  count++;
	      }
		
	      return false;
	      
	}
	
}
