package guiComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import comunicationObj.CmdCommands;
import comunicationObj.Pacchetto;
import comunicationObj.Utente;
import view.MainFrame;

import javax.swing.JComponent;

public class Dialogue extends JPanel{
	
	private int width;
	private int height;
	private int X;
	private int Y;
	private BufferedImage bk_sinistra, bk_destra = null;
	private boolean interagibile = false;
	private Image tmpImage = null;
	private ArrayList<JComponent> listaComp = new ArrayList<JComponent>();
	private Utente ut = null;
	private MainFrame mf = null;
	private Font font = null;
	
	public Dialogue(int width, int height, int x, int y, Utente ut, MainFrame mf, Font font){
		super();
		
		this.width = width;
		this.height = height;
		this.X = x;
		this.Y = y;
		this.ut = ut;
		this.mf = mf;
		this.font = font;
		
		Decoder decoder = Base64.getDecoder();
		
		byte[] bk_sinistra_Byte, bk_destra_Byte;
		
		bk_sinistra_Byte = decoder.decode(Constants.background_Image_Dialogue_left);
		bk_destra_Byte = decoder.decode(Constants.background_Image_Dialogue_right);
		
		ByteArrayInputStream bk_sinistra_Stream = new ByteArrayInputStream(bk_sinistra_Byte);
		ByteArrayInputStream bk_destra_Stream = new ByteArrayInputStream(bk_destra_Byte);
		
		try {
			bk_sinistra = ImageIO.read(bk_sinistra_Stream);
			bk_destra = ImageIO.read(bk_destra_Stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private Image drawBackground(int width, int height){
		
		//creiamo un'immagine di base
	    BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();
	    
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    
	    //inseriamo i due spigoli negli spigoli dell'immagine
	    
	    int Coordinata_X_bk_destra = ((int)(resizedImg.getWidth() - bk_destra.getWidth()));
	    int Coordinata_Y_bk_destra = ((int)(resizedImg.getHeight() - bk_destra.getHeight()));
	    
	    g2.drawImage(bk_sinistra, 0, 0, bk_sinistra.getWidth(), bk_sinistra.getHeight(), null);
	    g2.drawImage(bk_destra,Coordinata_X_bk_destra, Coordinata_Y_bk_destra, bk_destra.getWidth(), bk_destra.getHeight(), null);
	    //se c'è dello spazio vuoto tra i due spigoli, colmiamolo
	    
	    g2.setColor(Color.decode("#275e72"));
	    
	    if((bk_sinistra.getWidth() + bk_destra.getWidth()) < width) {
	    	g2.fill(new Rectangle(bk_sinistra.getWidth(), 0, resizedImg.getWidth() - (bk_sinistra.getWidth() + bk_destra.getWidth()), resizedImg.getHeight()));
	    	g2.fill(new Rectangle(0, 50, resizedImg.getWidth() - (bk_sinistra.getWidth() + bk_destra.getWidth()), resizedImg.getHeight()));
	    	g2.fill(new Rectangle(bk_sinistra.getWidth() + resizedImg.getWidth() - (bk_sinistra.getWidth() + bk_destra.getWidth()), 0, width, resizedImg.getHeight()-50));
	    	 //System.out.println("Primo IF");
	    }
	    
	    if((bk_sinistra.getHeight() + bk_destra.getHeight()) < height) {
	    	g2.fill(new Rectangle(0, bk_sinistra.getHeight(), resizedImg.getWidth(), height - bk_sinistra.getHeight() - bk_destra.getHeight()));
	    	g2.fill(new Rectangle(0, resizedImg.getHeight() - bk_destra.getHeight(), resizedImg.getWidth() - bk_destra.getWidth(), bk_destra.getHeight()));
	    	//System.out.println("Secondo IF");
	    }
	    
	    g2.drawImage(bk_destra, ((int)(resizedImg.getWidth() - bk_destra.getWidth())), ((int)(resizedImg.getHeight() - bk_destra.getHeight())), bk_destra.getWidth(), bk_destra.getHeight(), null);

	    g2.dispose();
	    
	    //System.out.println("Ho disegnato con queste grandezze: W:" + width + " e H:" + height);
	    
	    return resizedImg;
	    
	}
	
	public void initialize() {

		JTextPane frase = new JTextPane();
		
		frase = new JTextPane();
		frase.setVisible(false);
		frase.setPreferredSize(new Dimension(mf.getDimensionRatio(535), mf.getDimensionRatio(180)));
    	frase.setContentType("text/html");
	   	frase.setOpaque(false);
	   	frase.setEditable(false);
	   	frase.setFont(font.deriveFont(Font.BOLD, (int)(((int)frase.getWidth()/10) * 1.7))); //(int)(fattoreMolt1 * 1.7);
	   	frase.setForeground(Color.red);
	   	frase.setBounds(mf.getDimensionRatio(40), mf.getDimensionRatio(45), mf.getDimensionRatio(535), mf.getDimensionRatio(180));
	   	frase.setText("<html><center><div style=\"border: 2px solid white; padding: 5px; background-color: rgba(5,5,5, 0.5);\"><font size=\""+mf.getDimensionRatio(9)+"\" style=\"color: white; font-family: " + font.getFamily() + ";\">Comandante, l'utente <b>" + ut.getUsername() + "</b> vuole sfidarla, accetta la sfida?</font></div></center></html>");
		
		Button YESButton = new Button("Si", mf.getDimensionRatio(100), mf.getDimensionRatio(150), mf.getDimensionRatio(230), false);
		YESButton.interagibile = true;
		YESButton.setVisible(false);
		YESButton.setPreferredSize(new Dimension(mf.getDimensionRatio(389), mf.getDimensionRatio(611)));
		YESButton.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			try {
					mf.getClientThread().send(new Pacchetto(CmdCommands.answerYES,ut,null,true));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			mf.addMyPages(true); //risposta positiva
    		}
    	});
		
		Button NOButton = new Button("No", mf.getDimensionRatio(100), mf.getDimensionRatio(350), mf.getDimensionRatio(230), false);
		NOButton.interagibile = true;
		NOButton.setVisible(false);
		NOButton.setPreferredSize(new Dimension(mf.getDimensionRatio(501), mf.getDimensionRatio(611)));
		NOButton.addMouseListener(new MouseAdapter() {
    		public void mouseClicked(MouseEvent e) {
    			try {
					mf.getClientThread().send(new Pacchetto(CmdCommands.answerNO,ut,null,true));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			mf.addMyPages(false); //risposta negativa
    		}
    	});
		
		this.add(YESButton);
		this.add(NOButton);
		this.add(frase);
		
		this.setOpaque(false);
		this.setVisible(true);
		
		int width = 200;
		int height = 130;
		
		this.setPreferredSize(new Dimension(width, height));
		
		int widthParent = this.width;
		int heightParent = this.height;
		
		int coordX = this.X + ((int)widthParent/2) - ((int)width/2);
		int coordY = this.Y + ((int)heightParent/2) - ((int)height/2);
		
		this.setBounds(coordX, coordY, width, height);
		
		//prendi la metà del totale, poi la metà dell'immagine e sottrai
		
		while(width <= this.width) {
			
			try {
				Thread.sleep(0030);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			tmpImage = drawBackground(width, height);
			
			this.setPreferredSize(new Dimension(width, height));
			
			coordX = this.X + ((int)widthParent/2) - ((int)width/2);
			coordY = this.Y +((int)heightParent/2) - ((int)height/2);
			
			this.setBounds(coordX, coordY, width, height);
			
			this.getParent().repaint();
			
			width = width + 10;
		}
		
		while(height <= this.height) {
			
			try {
				Thread.sleep(0030);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			tmpImage = drawBackground(width, height);
			
			this.setPreferredSize(new Dimension(width, height));
			
			coordX = this.X + ((int)widthParent/2) - ((int)width/2);
			coordY = this.Y +((int)heightParent/2) - ((int)height/2);
			
			this.setBounds(coordX, coordY, width, height);
			
			this.getParent().repaint();
			
			height = height + 10;
		}
		
		this.setPreferredSize(new Dimension(this.width, this.height));
		this.setBounds(this.X, this.Y, this.width, this.height);
		
		setAllVisible();
	}
	
	private void setAllVisible() { //una volta finita l'animazione del Dialogue puo mostrare i suoi componenti
		System.out.println("la visibilità: " + SwingUtilities.isEventDispatchThread());
		
		this.setLayout(null);
		
		int i = 0;
		while (i < this.getComponents().length) {
			
			 this.getComponent(i).setVisible(true);
			
			i++;
		}
		
	}
	
	@Override
	  protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	    g.drawImage(tmpImage, 0, 0, null);
	}


	
	
	

	
}
