package comunicationObj;

import java.io.Serializable;

public class Utente implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1070862886216499829L;
	private String username;
	private String SESSION;
	private String stato;
	
	public Utente(String username, String SESSION, String stato){
		this.username = username;
		this.SESSION = SESSION;
		this.stato = stato;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getSESSION() {
		return SESSION;
	}
	
	public void setSESSION(String sESSION) {
		SESSION = sESSION;
	}

	@Override
	public String toString() {
		return username;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}
	
	
	
}
