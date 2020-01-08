package model;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import comunicationObj.Game;
import comunicationObj.Utente;
import guiComponents.Quadrato;
import view.MainFrame;

public class Data {
	
	private MainFrame mainFrame;
	private Utente me;
	public Game game;
	private ArrayList<Quadrato> myFlotta = new ArrayList<Quadrato>();
	
	public DefaultListModel<Utente> listModel = new DefaultListModel<Utente>();
	
	public Data(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public Utente getMe() {
		return me;
	}

	public void setMe(Utente me) {
		this.me = me;
	}

	public DefaultListModel<Utente> getListModel() {
		return listModel;
	}

	public void setListModel(DefaultListModel<Utente> listModel) {
		this.listModel = listModel;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public ArrayList<Quadrato> getMyFlotta() {
		return myFlotta;
	}

	public void setMyFlotta(ArrayList<Quadrato> myFlotta) {
		this.myFlotta = myFlotta;
	}
	
}
