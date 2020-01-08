package guiComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import comunicationObj.CmdCommands;
import comunicationObj.LogMessage;
import comunicationObj.Pacchetto;
import guiComponents.WrapLayout;
import view.MainFrame;

public class Griglia extends JPanel implements MouseMotionListener, MouseListener{
	
	private boolean shouldIdraw = false;
	private boolean mayIwrite;
	private ArrayList<Quadrato> listaComps = new ArrayList<Quadrato>();
	private ArrayList<Quadrato> quadratiIntersecati = new ArrayList<Quadrato>();
	public ArrayList<NaveInserita> naviInserite = new ArrayList<NaveInserita>();
	public ArrayList<NaveInserita> listaDraggedImgs = new ArrayList<NaveInserita>();
	private ArrayList<Nave> naviDaInserire = new ArrayList<Nave>();
	protected ArrayList<Nave> listaNavi = new ArrayList<Nave>();  //lista che non muta mai e server per ripopolare naviDaInserire quando si resetta la griglia
	private ArrayList<Quadrato> stackQuad = new ArrayList<Quadrato>();
	private Griglia griglia;
	private MainFrame mf;
	private boolean myTurno = false;
	private String naveClickata;
	private transient DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	final int unità;
	private String oldNaveClickata;
	public Font font;
	
	public Griglia(MainFrame mf) {
		this.mf = mf;
		this.griglia = this;
		this.setLayout(null);
		this.setSize(600,600);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.setOpaque(false);
		this.font = mf.font;
		
		unità = this.mf.getDimensionRatio(50);
		
		griglia.initialize();
	}
	
	
	/*
	 * Quando si inizia un nuovo Game questa funziona vene richiamata per pulire la griglia da tutte le navi ed i quadrati
	 * 
	 * 
	 */
	public void resetAll() {
		
		int count = 0;
		for(NaveInserita nv : naviInserite) {
			
			nv.getNave().setBorderState("non inserita");;
			nv.getNave().getQuadratiIntersecati().removeAll(nv.getNave().getQuadratiIntersecati());
			nv.getNave().setQuadratiColpiti(null);
			nv.getNave().setQuadratiColpiti(new ArrayList<Quadrato>());
			nv.setVisible(false);
			
			count++;
		}
		
		int i = 0;
		while(i < this.getComponentCount()) {
			if(this.getComponent(i) instanceof NaveInserita) {
				NaveInserita tmpNaveInserita = (NaveInserita)this.getComponent(i);
				tmpNaveInserita.setInserita(false);
				this.remove(this.getComponent(i));
			}else if(this.getComponent(i) instanceof Quadrato){
				Quadrato tmpQuad = ((Quadrato)this.getComponent(i));
				//tmpQuad.setGameStarted(false);
				tmpQuad.setState("Normal");
				tmpQuad.setIcon(null);
				tmpQuad.setScritto(false);
				tmpQuad.setClickato(false);
				tmpQuad.setNaveAssociata(null);
				tmpQuad.setRealBorder(null);
				tmpQuad.setRealState(null);
			}
			
			i++;
		}
		
		for(Nave nav : listaNavi) {
			nav.setQuadratiColpiti(null);
		}
		
		naveClickata = null;
		
		naviInserite.removeAll(naviInserite);
		naviDaInserire.removeAll(naviDaInserire);
		
		naviDaInserire.addAll(listaNavi);
		
		mf.myData.getMyFlotta().removeAll(mf.myData.getMyFlotta());
		mf.myData.game = null;
		
		stackQuad = new ArrayList<Quadrato>();
		
		this.repaint();
	}
	
	/*
	 * Questa funziona viene chiamata una sola volta e serve per costruire la griglia
	 */
	private void initialize() {
		
		final Griglia griglia = this;
		
		Runnable createGui = new Runnable() {
			public void run() {
				
				int xg = unità;
				int yg = unità;
				Quadrato tmpLabel = null;
				
				while(xg <= (unità*10)) {
					yg = unità;
					
					while(yg <= (unità*10)) {
						tmpLabel = new Quadrato(xg,yg,unità,unità, xg/unità, yg/unità);
						
						griglia.add(tmpLabel);
						listaComps.add(tmpLabel);
						
						yg = yg + unità;
					}
					xg = xg+unità;
				}
				
				xg = 0;
				yg = unità;
				
				int numDiv;
				JLabel tmpLabelCoord;
				
				while(yg <= (unità*10)) {
					
					tmpLabelCoord = new JLabel();
					numDiv = yg /unità;
					tmpLabelCoord.setText("Y_" + numDiv);
					tmpLabelCoord.setForeground(Color.white);
					
					tmpLabelCoord.setBounds(0, yg, unità, unità);
					
					griglia.add(tmpLabelCoord);
					
					yg = yg+unità;
				}
				
				xg = unità;
				yg = 0;
				
				while(xg <= (unità*10)) {
					
					tmpLabelCoord = new JLabel();
					numDiv = xg /unità;
					tmpLabelCoord.setText("X_" + numDiv);
					tmpLabelCoord.setForeground(Color.white);
					
					tmpLabelCoord.setBounds(xg, 0, unità, unità);
					
					griglia.add(tmpLabelCoord);
					
					xg = xg+unità;
				}
				
				griglia.setVisible(true);
			}
		};

		SwingUtilities.invokeLater(createGui);
	}

	public boolean isShouldIdraw() {
		return shouldIdraw;
	}

	public void setShouldIdraw(boolean shouldIdraw) {
		this.shouldIdraw = shouldIdraw;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		
		if(mf.myData.game.getStato().equals("Iniziale")){
			
			if(findComponentAt(e.getPoint()) instanceof Quadrato) {
				
				Quadrato qdr = (Quadrato)findComponentAt(e.getPoint());
				
				//qdr è il quadrato su cui scorre il mouse
				
			}
			
		}
		/*
		 * 
		 * PARTE NON ANCORA COMPLETAMENTE IMPLEMENTATA
		 * 
		 * Mostra un puntatore rosso che si muove su alcuni quadrati
		 * 
		 * 
		 * 
		if(findComponentAt(e.getPoint()) instanceof Quadrato) {
			
			Quadrato qdr = (Quadrato)findComponentAt(e.getPoint());
			
			int relativePositionX = e.getX() - qdr.getX();
			int relativePositionY = e.getY() - qdr.getY();
			
			if(relativePositionX <= 1 && relativePositionY >= 1) { //viene da sinistra
				
				qdr.drawPuntatore("sinistra");
				
			}else if(relativePositionX >= qdr.getWidth()-3 && (relativePositionY <= qdr.getHeight()-2)) { //viene da destra
				
				qdr.drawPuntatore("destra");
				
			}else if(relativePositionY <= 1) { //viene da sopra
				
				qdr.drawPuntatore("sopra");
				
			}else { //viene da sotto
				
				qdr.drawPuntatore("sotto");
				
			}
			
		}
		*/
		
		
		
		/*
		 * 
		 * Calcolo quanti quadrati AL MINIMO sono necessari per disegnare quella draggedImg
		 * poi sposto il cursore all'origine del quadrato più vicino, per aiutarlo ad inserirlo correttamente
		 * ovvero, prendi la posizione del mouse, trova lo spigolo in alto a sinistra più vicino e disegnalo a partire da la
		 * 
		 * 
		 * 
		 * draggedImg è una variabile TEMP che viene settata nella classe PaginaNavi, quando l'utente fa click su un oggetto Nave
		 * 
		 * 
		 */
		
		
		
		//controlla navi che sono in "floating" cioè sono state selezionate senza essere posizionate
		checkFloatShips();
		
		NaveInserita draggedImg = listaDraggedImgs.get(getDraggedImgByNave(naveClickata));
		
		Rectangle q_inAltoSinistra_ABSOLUTE = getCornerQuadrato("in alto a sinistra", listaComps);
		Rectangle q_inBassoDestra_ABSOLUTE = getCornerQuadrato("in basso a destra", listaComps);
		
		
		if(draggedImg != null) {
			
			int numQuadratiLunghezza = (int)draggedImg.getWidth()/unità;
			int numQuadratiAltezza = (int)(numQuadratiLunghezza * (draggedImg.getHeight()/unità) - numQuadratiLunghezza);
			
			int numQuadratiTot = numQuadratiLunghezza + numQuadratiAltezza;
			
			//System.out.println("Lunghezza->:"+numQuadratiLunghezza+"esce da:"+draggedImg.getWidth()+"diviso"+unità);
			
			if(shouldIdraw) {
				mayIwrite = true;
				
				
				//se la nave che si sta muovendo sulla griglia, era già posizionata, bisogna rimuovere i suoi quadratri precedentemente intersecati
				
				int tmpInd = 0;
				for(int j = 0; j< naviDaInserire.size(); j++) {
					if(naviDaInserire.get(j).getNome().equals(draggedImg.getName())) {
						tmpInd = j;
						naviDaInserire.get(tmpInd).getQuadratiIntersecati().removeAll(naviDaInserire.get(tmpInd).getQuadratiIntersecati());
						break;
					}
				}
				
				
				
				
				//prendi la posizione del mouse, trova lo spigolo in alto a sinistra più vicino e disegnalo a partire da la
				for(Quadrato jcTmp : listaComps) {
					if(jcTmp instanceof Quadrato) {
						if(jcTmp.getBounds().contains(e.getPoint())) {
								draggedImg.setBounds(jcTmp.getX(), jcTmp.getY(), draggedImg.getWidth(), draggedImg.getHeight());
						}
					}
				}
				
				
				//per il momento rendiamo la nave visibile
				this.add(draggedImg);
				draggedImg.setVisible(true);
				this.repaint();
				
				//questa lista servirà al metodo successivo per capire se può effettivamente disegnare quella nave, quindi qui lo puliamo se era stato già settato da altre navi
				quadratiIntersecati.removeAll(quadratiIntersecati);
				
				int numeroQuadratiIntersecati = 0;
				
				if(draggedImg.isVisible()) {
					Rectangle imgRect = draggedImg.getBounds();
					
					for(Quadrato jcTmp : listaComps) {
						if(jcTmp instanceof Quadrato) {
							
							Rectangle quadTmp = jcTmp.getBounds();
							
							if(imgRect.intersects(quadTmp)) {
								
								naviDaInserire.get(tmpInd).getQuadratiIntersecati().add(jcTmp);
								
								numeroQuadratiIntersecati++;
								
								quadratiIntersecati.add(jcTmp);
							}else {
								if(!(jcTmp.isScritto())) {
									jcTmp.setState("Normal");
								}else {
									jcTmp.setState("settato");
								}
							}
							
						}
					}
					
					draggedImg.setListaQuad(quadratiIntersecati);
					draggedImg.getNave().setBorderState("selezionata");
					
				}
				
				Point inAltoSinistra = new Point(draggedImg.getX(), draggedImg.getY());
				Point inAltoDestra = new Point(draggedImg.getX()+draggedImg.getWidth()-unità, draggedImg.getY());
				Point inBassoDestra = new Point(draggedImg.getX()+draggedImg.getWidth()-unità, draggedImg.getY()+draggedImg.getHeight()-unità);
				Point inBassoSinistra = new Point(draggedImg.getX(), draggedImg.getY()+draggedImg.getHeight()-unità);
				
				Rectangle q_inAltoSinistra = getCornerQuadrato("in alto a sinistra", quadratiIntersecati);
				Rectangle q_inAltoDestra = getCornerQuadrato("in alto a destra", quadratiIntersecati);
				Rectangle q_inBassoSinistra = getCornerQuadrato("in basso a sinistra", quadratiIntersecati);
				Rectangle q_inBassoDestra = getCornerQuadrato("in basso a destra", quadratiIntersecati);
				
				boolean foundQuadRed = false;
				//controlli geomentrici per garantire che effettivamente la nave sia all'interno della griglia, senza sforare alcuno spigolo
				for(Quadrato tmpQuad : quadratiIntersecati) {
					if(numeroQuadratiIntersecati == numQuadratiTot) {
						
						if(q_inAltoSinistra.getX() <= inAltoSinistra.getX() && q_inAltoSinistra.getY() == inAltoSinistra.getY()) {
							if(q_inAltoDestra.getX() >= inAltoDestra.getX() && q_inAltoDestra.getY() == inAltoSinistra.getY()) {
								if(q_inBassoDestra.getX() >= inBassoDestra.getX() && q_inBassoDestra.getY() == inBassoDestra.getY()) {
									if(q_inBassoSinistra.getX() <= inBassoSinistra.getX() && q_inBassoSinistra.getY() == inBassoSinistra.getY()) {
										if(!(tmpQuad.isScritto())) {
											
											tmpQuad.setState("green");
											
										}else {
											tmpQuad.setState("red");
											foundQuadRed = true;
										}
									}else {
										foundQuadRed = true;
										tmpQuad.setState("red");
									}
								}else {
									foundQuadRed = true;
									tmpQuad.setState("red");
								}
							}else {
								foundQuadRed = true;
								tmpQuad.setState("red");
							}
							
						}else {
							foundQuadRed = true;
							tmpQuad.setState("red");
						}
					}else {
						foundQuadRed = true;
						tmpQuad.setState("red");
					}
				}
				
				if(foundQuadRed) {
					//ti stai sovrapponendo a qualche quadrato occupato
					
					mayIwrite = false;
					draggedImg.setVisible(false);
					this.remove(draggedImg);
					this.repaint();
					
				}else if(numeroQuadratiIntersecati == numQuadratiTot) {
					//E' tutto ok
					
					mayIwrite = true;
					draggedImg.setVisible(true);
				}else {
					//sei fuori dalla griglia
					
					mayIwrite = false;
					draggedImg.setVisible(false);
					this.remove(draggedImg);
					this.repaint();
					
				}
			}else {
				//draggedImg.setVisible(false);
				//this.remove(draggedImg);
				//this.repaint();
			}
		}else {
			//System.out.println("dragged img is null");
		}
		
	}
	
	public Rectangle getCornerQuadrato(String corner, ArrayList<Quadrato> listaQuadrati) {
		
		Quadrato[] tmpArrayQuads = new Quadrato[listaQuadrati.size()];
		Rectangle tmpRect = null;
		
		int i = 0;
		
		for(Quadrato tmpQuad : listaQuadrati) {
			
			tmpArrayQuads[i] = tmpQuad;
			
			i++;
			
		}
		
		Quadrato aDestra = getLargest(tmpArrayQuads, "X");
		Quadrato inAlto = getSmallest(tmpArrayQuads, "Y");
		Quadrato aSinistra = getSmallest(tmpArrayQuads, "X");
		Quadrato inBasso = getLargest(tmpArrayQuads, "Y");
		
		if(corner.equals("in alto a destra")) {
			
			tmpRect = new Rectangle(aDestra.getX(), inAlto.getY(), unità, unità); //se le coordinate della figura disegnata non sforano questo quadrato, allora vuol dire che quel lato è disegnato bene
			
		}else if(corner.equals("in alto a sinistra")) {
			
			tmpRect = new Rectangle(aSinistra.getX(), inAlto.getY(), unità, unità); //se le coordinate della figura disegnata non sforano questo quadrato, allora vuol dire che quel lato è disegnato bene
			
		}else if(corner.equals("in basso a destra")) {
			
			tmpRect = new Rectangle(aDestra.getX(), inBasso.getY(), unità, unità); //se le coordinate della figura disegnata non sforano questo quadrato, allora vuol dire che quel lato è disegnato bene
			
		}else if(corner.equals("in basso a sinistra")){
			
			tmpRect = new Rectangle(aSinistra.getX(), inBasso.getY(), unità, unità); //se le coordinate della figura disegnata non sforano questo quadrato, allora vuol dire che quel lato è disegnato bene
			
		}
		
		
		return tmpRect;
	}
	
		public static Quadrato getLargest(Quadrato[] a, String where){  
			Quadrato temp;  
			int total = a.length;
			for (int i = 0; i < total; i++)   
			        {  
			            for (int j = i + 1; j < total; j++)   
			            {  	
			            	if(where.equals("X")) {
			            		 if (a[i].getX() > a[j].getX()){  
					                    temp = a[i];  
					                    a[i] = a[j];  
					                    a[j] = temp;  
					                }
			            	}else {
			            		if (a[i].getY() > a[j].getY()){  
				                    temp = a[i];  
				                    a[i] = a[j];  
				                    a[j] = temp;  
				                }
			            	}
			            }  
			        }  
				if(total > 0)
			       return a[total-1]; 
				else
				   return new Quadrato(0,0,0,0,0,0); 
		}  
		
		public static Quadrato getSmallest(Quadrato[] a, String where){  
			Quadrato temp;  
			int total = a.length;
			for (int i = 0; i < total; i++)   
			        {  
			            for (int j = i + 1; j < total; j++)   
			            {  	
			            	if(where.equals("X")) {
			            		 if (a[i].getX() < a[j].getX()){  
					                    temp = a[i];  
					                    a[i] = a[j];  
					                    a[j] = temp;  
					                }
			            	}else {
			            		if (a[i].getY() < a[j].getY()){  
				                    temp = a[i];  
				                    a[i] = a[j];  
				                    a[j] = temp;  
				                }
			            	}
			            }  
			        }  
				if(total > 0)
			       return a[total-1]; 
				else
				   return new Quadrato(0,0,0,0,0,0); 
		}

		public int getDraggedImgByNave(String nome) {
			
			for (int j = 0; j < listaDraggedImgs.size(); j++)  {
				if(listaDraggedImgs.get(j).getName().equals(nome)) {
					return j;
				}
			}
			
			return 0;
		}
		
		//Elimina le navi che non erano inserite ma solo fluttuanti sulla griglia
		public void checkFloatShips() {
			for (int j = 0; j < listaDraggedImgs.size(); j++)  {
				if(listaDraggedImgs.get(j).getParent() != null) {
					if(listaDraggedImgs.get(j).isVisible()) {
						if(!listaDraggedImgs.get(j).isInserita()) {
							
							for(Quadrato qd : listaDraggedImgs.get(j).getListaQuad()) {
								qd.setState("Normal");
							}
							
							
							listaDraggedImgs.get(j).setVisible(false);
							listaDraggedImgs.get(j).getNave().setBorderState("non inserita");
							this.remove(listaDraggedImgs.get(j));
						}
					}
				}
			}
			
			this.repaint();
		}
		
		public void checkSelectedButNotInserted()  {
			for(int j = 0; j < listaNavi.size(); j++) {
				if(listaNavi.get(j).getBorderState().equals("selezionata")) {
					listaNavi.get(j).setBorderState("non inserita");
					listaNavi.get(j).repaint();
				}
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
			NaveInserita draggedImg = listaDraggedImgs.get(getDraggedImgByNave(naveClickata));
			
			if(draggedImg != null && mf.myData.game.getStato().equals("Iniziale")){
				if(shouldIdraw) {
					if(mayIwrite) {
						
						//Riproduciamo il suono di inserimento della nave
						try{
					  	      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/nave_posizionata2.wav"));
					  	      Clip clip = AudioSystem.getClip();
					  	      clip.open(audioInputStream);
					  	      FloatControl gainControl = 
					  	    		    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					  	    		gainControl.setValue(+1.0f);
					  	      
					  	      clip.start( );
						  	}catch(Exception ex){  
						  		ex.printStackTrace();
						  	}
						
						
						//controlliamo se la nave era già inserita precedentemente, in tal caso la rimuoviamo
						for(int j = 0; j< naviInserite.size(); j++) {
							if(naviInserite.get(j).getNave().getNome().equals(draggedImg.getName())) {
								naviInserite.remove(j);
								break;
							}
						}
					   
						draggedImg.setInserita(true);
						draggedImg.setListaQuad(quadratiIntersecati);
						draggedImg.getNave().setBorderState("posizionata");
						for(Quadrato quad : quadratiIntersecati) {
							quad.setNaveAssociata(draggedImg.getNave());
						}
						naviInserite.add(draggedImg);
						
						//settiamo tutti i quadrati dovuti
						for(Quadrato tmpQuad : quadratiIntersecati) {
							tmpQuad.setScritto(true);
							mf.myData.getMyFlotta().add(tmpQuad);
						}
						
						shouldIdraw = false;
						mayIwrite = false;
						
						naveClickata = null;
						
					}else {
						//System.out.println("mayIwrite is false");
					}
				}else {
					//System.out.println("shouldIdraw is false");
				}
			}else {
				//System.out.println("draggedImg is null");
			}
			
			//se la partita è iniziata, ed è il mio turno
			if(mf.myData.game.getStato().equals("iniziato") && mf.myData.game.getTurno().getSESSION().equals(mf.myData.getMe().getSESSION())) {
				
				Quadrato quadFound = null;
				
				//troviamo il quadrato clickato
				for(Quadrato jcTmp : listaComps) {
					if(jcTmp instanceof Quadrato) {
						
						if(jcTmp.getBounds().contains(e.getPoint())) {

							// jcTmp è il quadrato clicckato
							quadFound = jcTmp;
							
							break;
						}
					}
				}
				
				if(quadFound != null) {
					
					if(quadFound.isClickato() == false) {
						
						stackQuad.add(quadFound); // ogni volta che clicko un quadrato lo aggiungo allo stack, così quando ricevo la risposta del server, so che quella risposta è inidirizzato all'ultimo elemento aggiunto allo stack (in modo da allegerire il pacchetto di risposta del server, poichè non mantiene informazioni riguardanti il quadrato specifico)
						
						//aggiorniamo il model dicendo che non è più il nostro turno
						mf.myData.game.setTurno(mf.myData.game.getAvversario(mf.myData.getMe()));
						
						//aggiorniamo la Caption 
						mf.lblCaption.setText("<html><center><div style=\"border: 1px solid white; padding: 3px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+mf.getDimensionRatio(6)+"\" style=\"color: white; font-family: " + mf.font.getFamily() + ";\">Stai giocando contro "+mf.myData.game.getAvversario(mf.myData.getMe()).getUsername()+" - In attesa del server... </font></div></center></html>");
						
						try {
							mf.clientThread.send(new Pacchetto(mf.myData.game,quadFound,null,true));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}else {
						mf.updateLog(new LogMessage("Game","Hai già clickato questo quadrato",dtf.format(LocalTime.now())));
					}
				}else {
					mf.updateLog(new LogMessage("Game","Non hai clickato un quadrato",dtf.format(LocalTime.now())));
				}
				
			}else {
				mf.updateLog(new LogMessage("Game","Stai premendo su un game non iniziato o non è il tuo turno",dtf.format(LocalTime.now())));
			}
			
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
			if(mf.myData.game.getTurno().getSESSION().equals(mf.myData.getMe().getSESSION()) && mf.myData.game.getStato().equals("iniziato")) { // se è il mio turno 
				this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}else {
				this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		
		public void setQuadRed(String quad) {
			for(Quadrato jcTmp : listaComps) {
				if(jcTmp instanceof Quadrato) {
					
					if(jcTmp.getName().equals(quad)) {
						jcTmp.setState("red");
					}
				}
			}
		}
		
		public void setQuadGreen(String quad) {
			for(Quadrato jcTmp : listaComps) {
				if(jcTmp instanceof Quadrato) {
					
					if(jcTmp.getName().equals(quad)) {
						jcTmp.setState("green");
					}
				}
			}
		}
		
		public void updateView(boolean myTurn) {
			int i = 0;
			while(i < this.getComponentCount()) {
				if(this.getComponent(i) instanceof Quadrato){
					Quadrato tmpQuad = ((Quadrato)this.getComponent(i));
					tmpQuad.updateView(myTurn);
				}
				i++;
			}
			
			for(Nave tmpNv : listaNavi) {
				if(myTurn) {
					tmpNv.getDraggedImg().setVisible(false);
				}else {
					tmpNv.getDraggedImg().setVisible(true);
				}
			}
		}
		
		public ArrayList<NaveInserita> getNaviInserite() {
			return naviInserite;
		}

		public ArrayList<Nave> getNaviDaInserire() {
			return naviDaInserire;
		}

		public void setNaviDaInserire(ArrayList<Nave> naviDaInserire) {
			this.naviDaInserire = naviDaInserire;
		}

		public boolean isMyTurno() {
			return myTurno;
		}

		public void setMyTurno(boolean myTurno) {
			this.myTurno = myTurno;
			
			if(mf.buttonCheck_ViewGriglia.isClicked()) { //attivo
				this.updateView(myTurno);
			}
			
		}

		public ArrayList<Quadrato> getStackQuad() {
			return stackQuad;
		}

		public void setStackQuad(ArrayList<Quadrato> stackQuad) {
			this.stackQuad = stackQuad;
		}



		public void setNaviInserite(ArrayList<NaveInserita> naviInserite) {
			this.naviInserite = naviInserite;
		}



		public String getNaveClickata() {
			return naveClickata;
		}



		public void setNaveClickata(String naveClickata) {
			if(this.naveClickata != null)
				this.oldNaveClickata = new String(this.naveClickata.toString());
			this.naveClickata = naveClickata;
		}

		public String getOldNaveClickata() {
			return oldNaveClickata.toString();
		}



		public ArrayList<NaveInserita> getListaDraggedImgs() {
			return listaDraggedImgs;
		}



		public void setListaDraggedImgs(ArrayList<NaveInserita> listaDraggedImgs) {
			this.listaDraggedImgs = listaDraggedImgs;
		}


		public MainFrame getMf() {
			return mf;
		} 
	
}
