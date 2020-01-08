package guiComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class Nave extends JLabel implements Serializable, MouseListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7819994588448374445L;
	private transient NaveInserita draggedImg = null;
	private String nome;
	private transient Griglia griglia;
	private ArrayList<Quadrato> quadratiIntersecati = new ArrayList<Quadrato>();
	private ArrayList<Quadrato> quadratiColpiti = new ArrayList<Quadrato>();
	private String url_icon;
	private transient PaginaNavi pgNavi;
	private String state;
	private String borderState;
	private int grandezzaQuadrati;
	
	public Nave(String name, Griglia griglia, String url_icon, PaginaNavi pgNavi, Dimension preferred, Icon ico, int quantiQuadrati){
		super();
		this.griglia = griglia;
		nome = name;
		this.addMouseListener(this);
		this.url_icon = url_icon;
		this.pgNavi = pgNavi;
		this.grandezzaQuadrati = quantiQuadrati;
		this.setName(nome);
		this.setNome(nome);
		
		this.setPreferredSize(new Dimension((int)preferred.getWidth(), (int)preferred.getHeight()));
		this.setIcon(ico);
		
		TitledBorder titled = new TitledBorder(BorderFactory.createLineBorder(Color.decode("#da4242"), 2), "NON INSERITA", TitledBorder.CENTER, TitledBorder.CENTER, griglia.font.deriveFont(Font.BOLD, 18), Color.WHITE);
	    this.setBorder(titled);
		
	    borderState = "non inserita";
	    
		draggedImg = new NaveInserita();
		draggedImg.setNave(this);
		draggedImg.setName(nome);
		draggedImg.setIcon(this.getIcon());
		draggedImg.setBounds(0, 0, (int)preferred.getWidth(), (int)preferred.getHeight());
		draggedImg.setPreferredSize(preferred);
		draggedImg.setIcon(ico);
		draggedImg.setVisible(true);
		
		//Togliamo dei pixel all'icon altrimenti avrebbe sforato i border
		Image tmpImage = iconToImage(this.getIcon());
		this.setIcon(new ImageIcon(tmpImage.getScaledInstance(this.getIcon().getIconWidth()-10, this.getIcon().getIconHeight()-18, 1)));
		
	}
	
	public void setBorderState(String state) {
		
		if(state.equals("selezionata")) { //BLACK
			borderState = "selezionata";
			TitledBorder titled = new TitledBorder(BorderFactory.createLineBorder(Color.decode("#000000"), 2), "SELEZIONATA", TitledBorder.CENTER, TitledBorder.CENTER, griglia.font.deriveFont(Font.BOLD, 18), Color.WHITE);
		    this.setBorder(titled);
		}else if(state.equals("posizionata")) { //GREEN
			borderState = "posizionata";
			TitledBorder titled = new TitledBorder(BorderFactory.createLineBorder(Color.decode("#32b746"), 2), "POSIZIONATA", TitledBorder.CENTER, TitledBorder.CENTER, griglia.font.deriveFont(Font.BOLD, 18), Color.WHITE);
		    this.setBorder(titled);
		}else if(state.equals("non inserita")) { //RED
			borderState = "non inserita";
			TitledBorder titled = new TitledBorder(BorderFactory.createLineBorder(Color.decode("#da4242"), 2), "NON INSERITA", TitledBorder.CENTER, TitledBorder.CENTER, griglia.font.deriveFont(Font.BOLD, 18), Color.WHITE);
		    this.setBorder(titled);
		}
		
		this.repaint();
		
		this.state = state;
	}
	
	public String getBorderState() {
		
		return borderState;
		
	}
	
	private static Image iconToImage(Icon icon) {
		   if (icon instanceof ImageIcon) {
		      return ((ImageIcon)icon).getImage();
		   } 
		   else {
		      int w = icon.getIconWidth();
		      int h = icon.getIconHeight();
		      GraphicsEnvironment ge = 
		        GraphicsEnvironment.getLocalGraphicsEnvironment();
		      GraphicsDevice gd = ge.getDefaultScreenDevice();
		      GraphicsConfiguration gc = gd.getDefaultConfiguration();
		      BufferedImage image = gc.createCompatibleImage(w, h);
		      Graphics2D g = image.createGraphics();
		      icon.paintIcon(null, g, 0, 0);
		      g.dispose();
		      return image;
		   }
		 }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		griglia.checkSelectedButNotInserted();
		
		setBorderState("selezionata");
		
		griglia.setNaveClickata(nome);
		
		pgNavi.draggedImg = draggedImg;
		
		griglia.setShouldIdraw(true);
		
		for(int i = 0; i < griglia.naviInserite.size(); i++) {
			NaveInserita tmpNave = griglia.naviInserite.get(i);
			
			
			//Elimina le navi che non erano inserite ma solo fluttuanti sulla griglia
			griglia.checkFloatShips();
			
			if(tmpNave.getName().equals(this.getNome())) {
				if(tmpNave.isInserita()) {
					System.out.println("Quadrati già interesecati che sto per rimuovere: "+tmpNave.getNave().getQuadratiIntersecati());
					for(Quadrato tmpQuad : tmpNave.getNave().getQuadratiIntersecati()) {
						
						for(int j =0; j  < griglia.getMf().myData.getMyFlotta().size(); j++) {
							if(tmpQuad.equals(griglia.getMf().myData.getMyFlotta().get(j))) {
								griglia.getMf().myData.getMyFlotta().remove(j);
							}
						}
						
						tmpQuad.setState("Normal");
						tmpQuad.setScritto(false);
						tmpQuad.setIcon(null);
						tmpQuad.setClickato(false);
						tmpQuad.setNaveAssociata(null);
					}
					System.out.println("Ho rimosso questa NaveInserita");
					
					griglia.naviInserite.remove(i);
					
					this.getDraggedImg().setInserita(false);
					this.getDraggedImg().setListaQuad(null);
					this.getDraggedImg().setVisible(false);
					this.getDraggedImg().getParent().remove(this.getDraggedImg());
					
					griglia.repaint();
					
					break;
					
				}
				tmpNave.setInserita(false);
				this.quadratiIntersecati.removeAll(this.quadratiIntersecati);
			}
		}
		
		setBorderState("selezionata");
		
	}

	@Override
	public void mousePressed(MouseEvent e) {

		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public NaveInserita getDraggedImg() {
		return draggedImg;
	}

	public void setDraggedImg(NaveInserita draggedImg) {
		this.draggedImg = draggedImg;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public ArrayList<Quadrato> getQuadratiIntersecati() {
		return quadratiIntersecati;
	}

	public void setQuadratiIntersecati(ArrayList<Quadrato> quadratiIntersecati) {
		this.quadratiIntersecati = quadratiIntersecati;
	}

	public String getUrl_icon() {
		return url_icon;
	}

	public ArrayList<Quadrato> getQuadratiColpiti() {
		return quadratiColpiti;
	}

	public void setQuadratiColpiti(ArrayList<Quadrato> quadratiColpiti) {
		this.quadratiColpiti = quadratiColpiti;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}

	public int getGrandezzaQuadrati() {
		return grandezzaQuadrati;
	}
	
	
	
}
