package guiComponents;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.Serializable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;

public class Quadrato extends JLabel implements Serializable, MouseListener{
	
	
	private static final long serialVersionUID = 6545358173380098232L;
	private String state; //indica il colore del bordo del quadrato
	private boolean clickato = false; //indica se è stato clickato
	private boolean scritto = false; //indica se il quadrato è associato ad una nave
	private int x , y ,width, height;
	private boolean gameStarted = false;
	private JLabel puntatore = new JLabel();
	private Timer tim;
	private Nave naveAssociata;
	private boolean rec = false; //tiene traccia se la sua clip è già stata riprodotta una volta 
	
	//queste due variabili controllano la visualizzazione del quadrato, in base al turno 
	private String realState; //indica l'immagine da mostrare (colpita o mancata) all'interno del quadrato
	private String realBorder; //indica il bordo da mostrare 
	
	public Quadrato (int x, int y, int width, int height, int CoordX, int CoordY) {
		super();
		this.x=x;
		this.y=y;
		this.width = width;
		this.height = height;
		
		//this.addMouseListener(this);
		
		this.state = "Normal";
		
		this.setOpaque(false);
		
		this.setBounds(this.x, this.y, this.width, this.height);
		this.setPreferredSize(new Dimension(width, height));
		
		this.setName(String.valueOf("X"+CoordX + "__Y" + CoordY));
		
		puntatore.setPreferredSize(new Dimension(this.width, this.height));
		puntatore.setVisible(false);
		this.add(puntatore);
		
	}
	
	public void updateView(boolean myTurn) {
		
		if(myTurn) { 
			//se è il mio turno nascondo tutti i border dei quadrati e anche le mie navi, e mostro solo l'immagine (colpita o mancata)
			
			this.state = "Normal";
			if(realState != null)
				this.setImageStatus(realState);
			
			this.repaint();
		}else {
			//nascondo le immagini (colpita o mancata) e mostro le mie navi con il border
			
			this.setIcon(null);
			this.setState(realBorder);
			
			this.repaint();
		}
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
		realBorder = state;
		this.repaint();
	}
	
	public void setImageStatus(String state) {
		
		this.setClickato(true);
		
		Icon icon = null;
		if(state.equals("mancato")) {
			
			icon = new ImageIcon(new ImageIcon(this.getClass().getResource("/mancato.png")).getImage().getScaledInstance(width,height,1));
			
		}else if(state.equals("colpito")) {
			icon = new ImageIcon(new ImageIcon(this.getClass().getResource("/explosion_GOOD.png")).getImage().getScaledInstance(width,height,1));
			if(rec == false) {
				try{
			  	      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/colpita.wav"));
			  	      Clip clip = AudioSystem.getClip();
			  	      clip.open(audioInputStream);
			  	      FloatControl gainControl = 
			  	    		    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			  	    		gainControl.setValue(+1.0f); // Reduce volume by 10 decibels.
			  	      
			  	      clip.start( );
				  }catch(Exception ex){  
				  	  ex.printStackTrace();
				  }
				
				rec = true;
			}
			
		}
		
		this.setIcon(icon);
		
		this.repaint();
		
		realState = state;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		if(state.equals("Normal"))
			g2.setColor(new Color(0,0,0,50));
		else if(state.equals("green"))
			g2.setColor(Color.GREEN);
		else if(state.equals("red"))
			g2.setColor(Color.RED);
		else if(state.equals("settato")) {
			//g2.setColor(Color.decode("#275e72"));
			return;
		}
		
		int thickness = 2;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(thickness));
		
			g2.drawLine(1, 1, width, 1);
			g2.drawLine(1, height, width, height);
			g2.drawLine(1, 1, 1, height);
			g2.drawLine(width, 1, width, height);
		
		//g2.drawRect(1,1 , width, height);
		
		g2.setStroke(oldStroke);
		
		super.paintComponent(g2);
	}

	public boolean isScritto() {
		return scritto;
	}

	public void setScritto(boolean scritto) {
			if(scritto) {
				this.setState("settato");
			}else {
				this.setState("Normal");
			}
			this.scritto = scritto;
	}

	public boolean isClickato() {
		return clickato;
	}

	public void setClickato(boolean clickato) {
		this.clickato = clickato;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getX() <= 1 && e.getY() >= 1) { //viene da sinistra
			//drawPuntatore("sinistra");
		}else if(e.getX() >= this.width-3 && (e.getY() <= height-2)) { //viene da destra
			//drawPuntatore("destra");
		}else if( e.getY() <= 1) { //viene da sopra
			//drawPuntatore("sopra");
		}else { //viene da sotto
			//drawPuntatore("sotto");
		}
		
	}
	
	
	/*
	 * Funzione implementata, ma non utilizzata.
	 * Server per mostrare dei puntatori rossi (immmagini png) che possono muoversi in 4 direzioni e serviranno per notificare la presenza nei loro pressi delle navi nemiche
	 * 
	 * 
	 */
	public void drawPuntatore(String provenienza) {
		
		if(tim == null || (tim != null && !tim.isRunning())) {
			
			puntatore.setVisible(true);
			
			ActionListener taskPerformer = new ActionListener() {
		   	      int count=0;
		   	      public void actionPerformed(ActionEvent evt) {
		   	    	
			   	    	if(provenienza.equals("sopra")) {
			   	    		
			   	    		if(count == 0) {
			   	    			puntatore.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/newpuntatore.png")).getImage().getScaledInstance((width), (height), 1)));
			   	    			puntatore.setVisible(true);
			   	    			puntatore.setBounds(0, -((int)(height/2)), width, height);
			   	    		}
			   	    		
			   	    		Rectangle r = puntatore.getBounds();
			   	    		
			   	    		System.out.println("Y: " + r.getY() + "X:" + r.getX());
			   	    		
			   	    		 if(r.getY() > (height+(int)(height/2))) {
			   	    			puntatore.setVisible(false);
			   	    			((Timer)evt.getSource()).stop();
				   	         }else {
				   	        	 puntatore.setBounds(new Rectangle(0, -((int)(height/2))+(5*count), puntatore.getWidth(), puntatore.getHeight()));
				   	         }
			   	    		 
			   			}else if (provenienza.equals("sotto")) {
			   	    		
			   				if(count == 0) {
			   					puntatore.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/newpuntatore.png")).getImage().getScaledInstance((width), (height), 1)));
			   					puntatore.setVisible(true);
			   	    			puntatore.setBounds(0, ((int)(height/2)), width, height);
			   				}
			   				
			   				Rectangle r = puntatore.getBounds();
			   				
			   	    		 if(r.getY() < -(int)(height/2)) {
			   	    			puntatore.setVisible(false);
			   	    			((Timer)evt.getSource()).stop();
				   	         }else {
				   	        	 puntatore.setBounds(new Rectangle(0, ((int)(height/2))-(5*count), puntatore.getWidth(), puntatore.getHeight()));
				   	         }
			   				
			   			}else if(provenienza.equals("sinistra")) {
			   	    		
			   	    		if(count == 0) {
			   	    			puntatore.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/newpuntatoreVERTICALE.png")).getImage().getScaledInstance((width), (height), 1)));
			   	    			puntatore.setVisible(true);
			   	    			puntatore.setBounds(-((int)(width/2)), 0, width, height);
			   	    		}
			   	    		
			   	    		Rectangle r = puntatore.getBounds();
			   	    		
			   	    		 if(r.getX() > (width+(int)(width/2))) {
			   	    			puntatore.setVisible(false);
			   	    			((Timer)evt.getSource()).stop();
				   	         }else {
				   	        	 puntatore.setBounds(new Rectangle(-((int)(width/2))+(5*count), 0, puntatore.getWidth(), puntatore.getHeight()));
				   	         }
			   	    		 
			   			}else if (provenienza.equals("destra")) {
			   	    		
			   				if(count == 0) {
			   					puntatore.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/newpuntatoreVERTICALE.png")).getImage().getScaledInstance((width), (height), 1)));
			   					puntatore.setVisible(true);
			   	    			puntatore.setBounds(((int)(width/2)), 0, width, height);
			   				}
			   				
			   				Rectangle r = puntatore.getBounds();
			   				
			   	    		 if(r.getX() < -(int)(width/2)) {
			   	    			puntatore.setVisible(false);
			   	    			((Timer)evt.getSource()).stop();
				   	         }else {
				   	        	 puntatore.setBounds(new Rectangle(((int)(width/2))-(5*count), 0, puntatore.getWidth(), puntatore.getHeight()));
				   	         }
			   				
			   			}
			   	    	
			   	    count++;
		   	      }
		   	}; 
			
			tim = new Timer(40, taskPerformer);
			
			tim.start();
		}else {
			//chiamato ma non startato
		}
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public Nave getNaveAssociata() {
		return naveAssociata;
	}

	public void setNaveAssociata(Nave naveAssociata) {
		this.naveAssociata = naveAssociata;
	}

	public String getRealState() {
		return realState;
	}

	public void setRealState(String realState) {
		this.realState = realState;
	}

	public String getRealBorder() {
		return realBorder;
	}

	public void setRealBorder(String realBorder) {
		this.realBorder = realBorder;
	}
	
	
}
