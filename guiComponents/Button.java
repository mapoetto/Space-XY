package guiComponents;

import java.awt.Color;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.io.File;

public class Button extends JLabel implements MouseListener{
	
	private String text;
	private int width;
	private int height;
	private int fontSize;
	private ImageIcon background_icon;
	private ImageIcon backgroundHOVER_icon;
	public boolean interagibile;
	
	public int x,y;
	/*
	 * 
	 * L'immagine è grande: 322x136
	 * quindi ha un rapporto moltiplicativo di 1x0.42236
	 * 
	 */
	
	public Button(String text, int width, int x, int y, boolean zero) {
		this(text,width, x, y);
		if(!zero) {
			this.setBounds(x, y, this.width, this.height);
		}
	}
	
	public Button(String text, int width, int x, int y) {
		super(text);
		
		this.x = x;
		this.y = y;
		
		this.text = text;
		this.width = (width<100) ? 101 : width; //il minimo consentito è 100
		this.height = (int)(this.width*0.42236);
		double fattoreMolt1 = this.width / 10; //vede quanti 10 ci sono nel width (10 è la tolleranza, infatti valori che oscillano in un raggio di dimensione 10 hanno lo stesso font)
		this.fontSize = 1 + (int)(fattoreMolt1 * 1.7);
		
		this.setPreferredSize(new Dimension(this.width, height));
		
		this.setBounds(0, 0, this.width, this.height);
		
		BufferedImage background_image = null, backgroundHOVER_image = null;
		byte[] background_imageByte, backgroundHOVER_imageByte;
		
		Decoder decoder = Base64.getDecoder();
		
		background_imageByte = decoder.decode(Constants.background_Image_button64encoded);
		backgroundHOVER_imageByte = decoder.decode(Constants.background_ImageHOVER_button64encoded);
		
		ByteArrayInputStream font_InputStream = new ByteArrayInputStream(decoder.decode(Constants.ttf_Font_file));
		
		ByteArrayInputStream background_InputStream = new ByteArrayInputStream(background_imageByte);
		ByteArrayInputStream backgroundHOVER_InputStream = new ByteArrayInputStream(backgroundHOVER_imageByte);
		
		try {
			background_image = ImageIO.read(background_InputStream);
			backgroundHOVER_image = ImageIO.read(backgroundHOVER_InputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		background_image = (BufferedImage)getScaledImage(background_image, this.width, height);
		backgroundHOVER_image =(BufferedImage)getScaledImage(backgroundHOVER_image, this.width, height);
		
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, font_InputStream);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			background_InputStream.close();
			backgroundHOVER_InputStream.close();
			font_InputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		background_icon=new ImageIcon(background_image);
		backgroundHOVER_icon=new ImageIcon(backgroundHOVER_image);
		
		this.setIcon(background_icon);
		
		this.setHorizontalTextPosition(JLabel.CENTER);
		this.setVerticalTextPosition(JLabel.CENTER);
		
		
		/*
		 * FONTSIZE:
		 * 
		 * 100 = 18
		 * 200 = 35
		 * 300 = 52
		 * 
		 * + 17 ogni 100
		 */
		
		
		this.setFont(font.deriveFont(Font.BOLD, fontSize));
		this.setForeground(Color.black);
		this.addMouseListener(this);
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	public ActionListener getFadeOutPerferformer(int provenienza) {
		Button label = this;
		label.setInteragibile(false);
		Page page = (Page)label.getParent();
		page.checkComponentsReady();
		
		ActionListener taskPerformer = new ActionListener() {
	   	      int count=0;
	   	      public void actionPerformed(ActionEvent evt) {
	   	    	 Rectangle r = label.getBounds();
	   	    	 JLayeredPane parent = (JLayeredPane)label.getParent();

	   	    	 if(provenienza == 1) { //1 = esce verso l'alto
		   	         if(r.getY() < 0 ) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(0, 0, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setVisible(false);
		   	            page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX(), (int)r.getY()-(20*count), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 2) { //2 = esce verso il basso
		   	         if(r.getY() > parent.getHeight()) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(0, 0, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setVisible(false);
		   	            page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX(), (int)r.getY()+(20*count), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 3) { //3 = esce verso destra
		   	         if(r.getX() > parent.getWidth()) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(0, 0, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setVisible(false);
		   	            page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX()+(20*count), (int)r.getY(), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 4) { //4 = esce verso sinistra
		   	         if(r.getX() < 0) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(0, 0, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setVisible(false);
		   	            page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX()-(20*count), (int)r.getY(), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }
	   	    	 
	   	    	page.checkComponentsReady();
	   	    	 
	   	         count++;
	   	      }
	   	}; 
   	  	return taskPerformer;
	}
	
	
	public ActionListener getFadeInPerferformer(int provenienza, int destinazioneX, int destinazioneY) {
		Button label = this;
		
		label.setInteragibile(false);
		Page page = (Page)label.getParent();
		page.checkComponentsReady();
		
		ActionListener taskPerformer = new ActionListener() {
	   	      int count=0;
	   	      public void actionPerformed(ActionEvent evt) {
	   	    	 Rectangle r = label.getBounds();
	   	    	 label.setVisible(true);
	   	    	 JLayeredPane parent = (JLayeredPane)label.getParent();
	   	    	 
	   	    	 if(provenienza == 1) { //1 = viene dall'alto
	   	    		label.setBounds(new Rectangle(destinazioneX, 0, label.getWidth(), label.getHeight()));
		   	         if(r.getY() > destinazioneY) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(destinazioneX, destinazioneY, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setInteragibile(true);
		   	               page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle(destinazioneX, 0+(20*count), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 2) { //2 = viene dal basso
	   	    		label.setBounds(new Rectangle(destinazioneX, parent.getHeight(), label.getWidth(), label.getHeight()));
		   	         if(r.getY() < destinazioneY) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(destinazioneX, destinazioneY, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setInteragibile(true);
		   	               page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle(destinazioneX, parent.getHeight()-(20*count), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 3) { //3 = viene da destra
	   	    		label.setBounds(new Rectangle(parent.getWidth(), destinazioneY, label.getWidth(), label.getHeight()));
		   	         if(r.getX() < destinazioneX) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(destinazioneX, destinazioneY, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setInteragibile(true);
		   	               page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX()-(20*count), destinazioneY, label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 4) { //4 = viene da sinistra
	   	    		label.setBounds(new Rectangle(0, destinazioneY, label.getWidth(), label.getHeight()));
		   	         if(r.getX() > destinazioneX) {// finchè è visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(destinazioneX, destinazioneY, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setInteragibile(true);
		   	               page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle(0+(20*count), destinazioneY, label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }
	   	    	 
	   	    	 
	   	    	page.checkComponentsReady();
	   	         
	   	         count++;
	   	      }
	   	}; 
   	  	return taskPerformer;
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(interagibile) {
			//System.out.println("You clicked me");
	    	try{
	  	      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/clicked.wav"));
	  	      Clip clip = AudioSystem.getClip(); 
	  	      clip.open(audioInputStream);
	  	      FloatControl gainControl = 
	  	    		    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	  	    		gainControl.setValue(-20.0f); // Reduce volume by 10 decibels.
	  	      
	  	      clip.start( );
		  	}catch(Exception ex){  
		  		ex.printStackTrace();
		  	}
	    	
			this.setForeground(Color.black);
	    	this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    	this.setIcon(background_icon);
	    	
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

	@Override //HOVER
	public void mouseEntered(MouseEvent e) {
		if(interagibile) {
			this.setForeground(Color.white);
	    	this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    	this.setIcon(backgroundHOVER_icon);      
	    	try{
	    	      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/sound.wav"));
	    	      Clip clip = AudioSystem.getClip();
	    	      clip.open(audioInputStream);
	    	      FloatControl gainControl = 
	    	    		    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	    	    		gainControl.setValue(-20.0f); // Reduce volume by 10 decibels.
	    	      
	    	      clip.start( );
	    	}catch(Exception ex){  
	    		ex.printStackTrace();
	    	}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(interagibile) {
			this.setForeground(Color.black);
	    	this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    	this.setIcon(background_icon);
		}
	}

	public boolean isInteragibile() {
		return interagibile;
	}

	public void setInteragibile(boolean interagibile) {
		this.interagibile = interagibile;
	}
}
