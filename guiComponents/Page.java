package guiComponents;

import java.awt.Color;
import java.awt.Component;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import comunicationObj.LogMessage;
import view.MainFrame;

public class Page extends JLayeredPane{
	
	public ArrayList<Object> listaComponenti = new ArrayList<Object>();
	private static final String nameVersion = "Version 0.001";
	private boolean interagibile = true;
	private MainFrame mf;
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
	
	public Page(MainFrame mf) {
		super();
		this.mf = mf;
		this.setOpaque(false); //per mantenere il background
    	this.setLayout(null);
    	this.setVisible(false);
	}
	
	
	//Controlla se ci sono Button o CheckButton in movimento, in tal caso la pagina non è pronta per essere interagibile
	public void checkComponentsReady() {
		int indice = 0;
		
		while(indice < listaComponenti.size()) {
			
			if(listaComponenti.get(indice) instanceof Button) {
				
				if(!((Button)listaComponenti.get(indice)).interagibile && ((Button)listaComponenti.get(indice)).isVisible()) { //se è visibile ma non è interagibile vuol dire che si sta muovendo
					this.interagibile = false;
					//System.out.println("ho settato la pagina NON INTERAGIBILE");
					return;
				}
							
			}else if(listaComponenti.get(indice) instanceof CheckButton) {
				
				if(!((CheckButton)listaComponenti.get(indice)).interagibile && ((CheckButton)listaComponenti.get(indice)).isVisible()) {
					this.interagibile = false;
					//System.out.println("ho settato la pagina NON INTERAGIBILE");
					return;
				}
								
			}
			
			indice++;
		}
		
		//System.out.println("ho settato la pagina INTERAGIBILE");
		this.interagibile = true;
	}
	
	public boolean entra() {
		
		//se la pagina non è interagibile esci dal metodo con false
		if(!this.interagibile) {
			mf.updateLog(new LogMessage("System","Impossibile far entrare una nuova pagina poichè ci sono animazioni in corso",dtf.format(LocalTime.now())));
			return false;
		}
		
		this.setVisible(true);
		
		int indice = 0;
		
		Object tmpComponent;
		ArrayList<Timer> tmpTimerList = new ArrayList<Timer>();
		
		
		JLabel lblVersion2 = new JLabel("");
    	lblVersion2.setForeground(Color.BLACK);
    	lblVersion2.setBounds(900, 925, 81, 25);
		
    	this.add(lblVersion2);
    	
		
		Timer tim = null;
		
		while(indice < listaComponenti.size()) {
			
			if(listaComponenti.get(indice) instanceof Button) {
				
				int randomAnimation = ThreadLocalRandom.current().nextInt(1, 4 + 1); //numero casuale tra 1 e 4
				
				tmpComponent = (Button)listaComponenti.get(indice);
				
				tim = new Timer(30, ((Button)tmpComponent).getFadeInPerferformer(randomAnimation, ((Button)tmpComponent).x, ((Button)tmpComponent).y));
				tim.start();
				
				tmpTimerList.add(tim);				
			}else if(listaComponenti.get(indice) instanceof CheckButton) {
				
				int randomAnimation = ThreadLocalRandom.current().nextInt(1, 4 + 1); //numero casuale tra 1 e 4
				
				tmpComponent = (CheckButton)listaComponenti.get(indice);
				
				tim = new Timer(30, ((CheckButton)tmpComponent).getFadeInPerferformer(randomAnimation, ((CheckButton)tmpComponent).x, ((CheckButton)tmpComponent).y));
				tim.start();
				tmpTimerList.add(tim);				
			}
			
			indice++;
		}
		
		do {
			//System.out.println("Aspetto che i timer (FadeIn) finiscano. FADEIN è nell'EDT ? " + SwingUtilities.isEventDispatchThread());
		}while(timerRunning(tmpTimerList));
		
		mf.setCurrentPage(this);
		
		return true;
		
	}
	
	public void esci(Page entraPage) { //la pagina che dovrà essere mostrata dopo l'uscita
		
		//se la pagina non è interagibile esci dal metodo
		if(!this.interagibile) {
			mf.updateLog(new LogMessage("System","Impossibile uscire dalla pagina poichè ci sono animazioni in corso",dtf.format(LocalTime.now())));
			return;
		}
		
		int indice = 0;
		Object tmpComponent;
		ArrayList<Timer> tmpTimerList = new ArrayList<Timer>();
		Timer tim = null;
		
		while(indice < listaComponenti.size()) {
			
			if(listaComponenti.get(indice) instanceof Button) {
				
				int randomAnimation = ThreadLocalRandom.current().nextInt(1, 4 + 1); //numero casuale tra 1 e 4
				
				tmpComponent = (Button)listaComponenti.get(indice);
				
				tim = new Timer(50, ((Button)tmpComponent).getFadeOutPerferformer(randomAnimation));
				tim.start();
				tmpTimerList.add(tim);	
				
			}else if(listaComponenti.get(indice) instanceof CheckButton) {
				
				int randomAnimation = ThreadLocalRandom.current().nextInt(1, 4 + 1); //numero casuale tra 1 e 4
				
				tmpComponent = (CheckButton)listaComponenti.get(indice);
				
				tim = new Timer(50, ((CheckButton)tmpComponent).getFadeOutPerferformer(randomAnimation));
				tim.start();
				tmpTimerList.add(tim);	
				
			}
			
			indice++;
		}
		
		do {
			//System.out.println("Aspetto che i timer (FadeOut) finiscano. FADEOUT è nell'EDT ? " + SwingUtilities.isEventDispatchThread());
		}while(timerRunning(tmpTimerList));
		
		this.setVisible(false);
		
		if(!(entraPage == null)) {
			mf.setCurrentPage(entraPage);
			entraPage.entra();
		}
		
	}
	
	private boolean timerRunning(ArrayList<Timer> timerList) {
		
		boolean tmpFound = false;
		
		for(Timer tmpTim : timerList) {
			if(tmpTim.isRunning()) {
				tmpFound = true;
				return tmpFound; //true vuol dire che il timer sta ancora eseguendo
			}
		}
		
		return tmpFound;
		
	}
	
	@Override
	public void add(Component comp, Object constraints, int index) {
		// TODO Auto-generated method stub
		super.add(comp, constraints, index);
		listaComponenti.add(comp);
	}
	
}
