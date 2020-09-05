package guiComponents;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;

import view.MainFrame;

public class LateralPanel extends JPanel{
	
	private String type;
	public int height;
	public int width;
	private Image background;
	private JTextPane primoPane = new JTextPane();
	private JTextPane secondoPane = new JTextPane();
	private JTextPane terzoPane = new JTextPane();
	private Font font;
	private MainFrame mf;
	private boolean entrata = false;
	private String primaStringa="0", secondaStringa="0", terzaStringa="N.D.";
	
	public LateralPanel(String type, int quadratoWidth, MainFrame mf){
		
		this.mf = mf;
		
		this.add(primoPane);
		this.add(secondoPane);
		this.add(terzoPane);
		
		this.setOpaque(false);
		this.setLayout(null);
		
		this.type = type;
		
		this.height = quadratoWidth*6; //il pannello sarà alto quanto 6 quadrati
		
		//170 width 300 height
		//170 : 300 = x : height
		//calcoliamo il width di conseguenza, mantenendo le giuste proporzioni
		
		this.width = (int)(170*height)/300;
		
		this.setPreferredSize(new Dimension(width,height));
		
		try {
			this.background = getScaledImage(ImageIO.read(this.getClass().getResource("/pannello.png")), width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Decoder decoder = Base64.getDecoder();
		ByteArrayInputStream font_InputStream = new ByteArrayInputStream(decoder.decode(Constants.ttf_Font_file));
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, font_InputStream);
			font = font.deriveFont(Font.PLAIN, 20);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//posizione STANDARD: 27 x 27 y
		primoPane.setBounds(mf.getDimensionRatio(21), mf.getDimensionRatio(22), mf.getDimensionRatio(114), mf.getDimensionRatio(58));
		primoPane.setContentType("text/html");
		primoPane.setEditable(false);
		primoPane.setOpaque(false);
		primoPane.setFont(font);
		primoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center><font color=\"white\">"+primaStringa+"</font> Navi <br />Colpite</center></font></html>");
		primoPane.setVisible(false);
		//posizione STANDARD: 17 x 113 y
		secondoPane.setBounds(mf.getDimensionRatio(11), mf.getDimensionRatio(109), mf.getDimensionRatio(144), mf.getDimensionRatio(58));
		secondoPane.setContentType("text/html");
		secondoPane.setEditable(false);
		secondoPane.setOpaque(false);
		secondoPane.setFont(font);
		secondoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center><font color=\"white\">"+secondaStringa+"</font> Navi <br />Affondate</center></font></html>");
		secondoPane.setVisible(false);
		
		//posizione STANDARD: 9 x 192 y
		terzoPane.setBounds(mf.getDimensionRatio(3), mf.getDimensionRatio(190), mf.getDimensionRatio(155), mf.getDimensionRatio(80));
		terzoPane.setContentType("text/html");
		terzoPane.setEditable(false);
		terzoPane.setOpaque(false);
		terzoPane.setFont(font);
		terzoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center>Precisione<br />colpi<br /> <font color=\"white\">N.D.</font></center></font></html>");
		terzoPane.setVisible(false);
		
		this.setVisible(false);
	}
	
	public void entra() {
		this.setBounds(-width, mf.getDimensionRatio(420), width, height);
		this.setVisible(true);
		Timer tim = new Timer(20, getFadeInPerferformer(4, 0, mf.getDimensionRatio(420)));
		tim.start();
	}
	
	protected void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, this);
        super.paintComponent(g);
    }
	
	public void setPrimoPaneNumber(String string) {
		primaStringa = string;
		primoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center><font color=\"white\">"+primaStringa+"</font> Navi <br />Colpite</center></font></html>");
		primoPane.repaint();
	}
	
	public void setSecondoPaneNumber(String string) {
		secondaStringa = string;
		secondoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center><font color=\"white\">"+secondaStringa+"</font> Navi <br />Affondate</center></font></html>");
		secondoPane.repaint();
	}
	
	public String getSecondaStringa() {
		return secondaStringa;
	}
	
	public void setTerzoPaneNumber(String string) {
		terzaStringa = string;
		terzoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center>Precisione<br />colpi<br /> <font color=\"white\">"+terzaStringa+"</font></center></font></html>");
		terzoPane.repaint();
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	public void resetAll() {
		this.setBounds(-width, mf.getDimensionRatio(420), width, height);
		this.setVisible(false);
		
		primaStringa = "0";
		secondaStringa = "0";
		terzaStringa = "N.D.";
		
		primoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center><font color=\"white\">0</font> Navi <br />Colpite</center></font></html>");
		primoPane.setVisible(false);
		secondoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center><font color=\"white\">0</font> Navi <br />Affondate</center></font></html>");
		secondoPane.setVisible(false);
		terzoPane.setText("<html> <font size=\""+mf.getDimensionRatio(6)+"\" style=\"font-family: " + font.getFamily() + " ;\"><center>Precisione<br />colpi<br /> <font color=\"white\">N.D.</font></center></font></html>");
		terzoPane.setVisible(false);
		
		entrata = false;
	}
	
	public ActionListener getFadeInPerferformer(int provenienza, int destinazioneX, int destinazioneY) {
		JPanel label = this;
		Rectangle r_old = label.getBounds();
		
		ActionListener taskPerformer = new ActionListener() {
	   	      int count=0;
	   	      public void actionPerformed(ActionEvent evt) {
	   	    	 Rectangle r = label.getBounds();
	   	    	 
	   	    	 if(provenienza == 4) { //4 = viene da sinistra
	   	    		label.setBounds(new Rectangle(label.getBounds().x, destinazioneY, label.getWidth(), label.getHeight()));
		   	         if(r.getX() >= destinazioneX) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(destinazioneX, destinazioneY, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               primoPane.setVisible(true);
		   	               secondoPane.setVisible(true);
		   	               terzoPane.setVisible(true);
		   	               entrata = true;
		   	               
		   	               System.out.println("Sono stoppato perchè "+r.getX()+" è maggiore di "+destinazioneX);
		   	         }else {
		   	        	 label.setBounds(new Rectangle(r_old.getBounds().x+(2*count), destinazioneY, label.getWidth(), label.getHeight()));
		   	        	 System.out.println("aggiungo alla x");
		   	         }
	   	    	 }
	   	         
	   	         count++;
	   	      }
	   	}; 
   	  	return taskPerformer;
	}

	public boolean isEntrata() {
		return entrata;
	}
	
}
