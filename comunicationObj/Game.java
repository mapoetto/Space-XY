package comunicationObj;

import java.io.Serializable;
import java.util.ArrayList;

import guiComponents.Quadrato;

public class Game implements Serializable{
	
	private static final long serialVersionUID = 1848895928681486630L;
	private String SESSION;
	private String stato;
	private Utente turno;
	private Utente sfidante;
	private Utente sfidato;
	private ArrayList<Quadrato> flottaSfidante = null;
	private ArrayList<Quadrato> flottaSfidato = null;
	private int numTurno;
	
	public Game(String stato, Utente sfidante, Utente sfidato, String SESSION){
		this.stato = stato;
		this.sfidante = sfidante;
		this.sfidato = sfidato;
		this.SESSION = SESSION;
		
		this.setNumTurno(1);
		this.setTurno(this.getSfidante()); //colui che è stato sfidato inizia per primo
		
	}
	
	public void setMyFlotta(Utente ut, ArrayList<Quadrato> flotta) {
		
		if(ut.getSESSION().equals(sfidato.getSESSION())) {
			flottaSfidato = flotta;
			System.out.println("Ho settato la flotta di "+ut.getUsername()+" (SFIDATO)");
		}else {
			flottaSfidante = flotta;
			System.out.println("Ho settato la flotta di "+ut.getUsername()+" (SFIDANTE)");
		}
	}
	
	public ArrayList<Quadrato> getMyFlotta(Utente ut) {
		
		if(ut.getSESSION().equals(sfidato.getSESSION())) 
			return flottaSfidato;
		else
			return flottaSfidante;
		
	}
	
	public ArrayList<Quadrato> getFlottaAvversario(Utente ut){
		if(ut.getSESSION().equals(sfidato.getSESSION())) 
			return flottaSfidante;
		else
			return flottaSfidato;
	}
	
	public Utente getAvversario(Utente ut){
		if(ut.getSESSION().equals(sfidato.getSESSION())) 
			return sfidante;
		else
			return sfidato;
	}
	
	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public Utente getSfidante() {
		return sfidante;
	}

	public void setSfidante(Utente sfidante) {
		this.sfidante = sfidante;
	}

	public Utente getSfidato() {
		return sfidato;
	}

	public void setSfidato(Utente sfidato) {
		this.sfidato = sfidato;
	}

	public String getSESSION() {
		return SESSION;
	}

	public void setSESSION(String sESSION) {
		SESSION = sESSION;
	}

	public ArrayList<Quadrato> getFlottaSfidante() {
		return flottaSfidante;
	}

	public ArrayList<Quadrato> getFlottaSfidato() {
		return flottaSfidato;
	}

	public Utente getTurno() {
		return turno;
	}

	public void setTurno(Utente turno) {
		this.turno = turno;
	}

	public int getNumTurno() {
		return numTurno;
	}

	public void setNumTurno(int numTurno) {
		this.numTurno = numTurno;
	}
	
	
}
