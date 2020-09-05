package guiComponents;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class CheckButton extends JLabel implements MouseListener, MouseMotionListener{
	
	private ImageIcon background_icon;
	private ImageIcon backgroundHOVER_icon;
	private String tipText; //in formato HTML
	private boolean clicked;
	private ToolTip toolTip = null;
	private int width, height;
	public boolean interagibile;
	private String red = "#930000";
	private String green = "#00a23b";
	public int x,y;
	
	private boolean addedToolTip = false;
	
	public CheckButton(boolean state, int width, int x, int y, String tipText) {
		super(tipText);
		
		this.tipText = "<br /><br />" + tipText;
		this.x = x;
		this.y = y;
		
		clicked = state;
		this.width = (width<40) ? 41 : width; //il minimo consentito � 40
		this.height = (int)(this.width*0.73913);
		
		if(tipText != null) {
			if(clicked)
				toolTip = new ToolTip("<b>Stato: <font color=\"" + green + "\">Attivo</font></b><br />" + this.tipText, 300);
			else
				toolTip = new ToolTip("<b>Stato: <font color=\"" + red + "\">NON Attivo</font></b> <br />" + this.tipText, 300);
			
			toolTip.setVisible(false);
		}
		
		this.setPreferredSize(new Dimension(this.width, height));
		
		this.setBounds(this.x, this.y, this.width, this.height);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		Decoder decoder = Base64.getDecoder();
		
		BufferedImage background_image = null, backgroundHOVER_image = null;
		byte[] background_imageByte, backgroundHOVER_imageByte;
		
		background_imageByte = decoder.decode(Constants.background_Image_UnChecked);
		backgroundHOVER_imageByte = decoder.decode(Constants.background_Image_Checked);
		
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
		
		background_icon=new ImageIcon(background_image);
		backgroundHOVER_icon=new ImageIcon(backgroundHOVER_image);
		
		if(!clicked)
			this.setIcon(background_icon);
		else
			this.setIcon(backgroundHOVER_icon);
		
		
		try {
			background_InputStream.close();
			backgroundHOVER_InputStream.close();
			font_InputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		CheckButton label = this;
		
		label.setInteragibile(false);
		Page page = (Page)label.getParent();
		page.checkComponentsReady();
		
		ActionListener taskPerformer = new ActionListener() {
	   	      int count=0;
	   	      public void actionPerformed(ActionEvent evt) {
	   	    	 Rectangle r = label.getBounds();
	   	    	 JLayeredPane parent = (JLayeredPane)label.getParent();

	   	    	 if(provenienza == 1) { //1 = esce verso l'alto
		   	         if(r.getY() < 0 ) {// finch� � visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(0, 0, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setVisible(false);
		   	            page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX(), (int)r.getY()-(20*count), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 2) { //2 = esce verso il basso
		   	         if(r.getY() > parent.getHeight()) {// finch� � visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(0, 0, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setVisible(false);
		   	            page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX(), (int)r.getY()+(20*count), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 3) { //3 = esce verso destra
		   	         if(r.getX() > parent.getWidth()) {// finch� � visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(0, 0, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setVisible(false);
		   	            page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX()+(20*count), (int)r.getY(), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 4) { //4 = esce verso sinistra
		   	         if(r.getX() < 0) {// finch� � visibile | r.getX() < 0 condizione per andare verso sinistra
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
		CheckButton label = this;
		
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
	   	    		label.setInteragibile(false);
		   	         if(r.getY() > destinazioneY) {// finch� � visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(destinazioneX, destinazioneY, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setInteragibile(true);
		   	               page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle(destinazioneX, 0+(20*count), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 2) { //2 = viene dal basso
	   	    		label.setBounds(new Rectangle(destinazioneX, parent.getHeight(), label.getWidth(), label.getHeight()));
	   	    		label.setInteragibile(false);
		   	         if(r.getY() < destinazioneY) {// finch� � visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(destinazioneX, destinazioneY, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setInteragibile(true);
		   	               page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle(destinazioneX, parent.getHeight()-(20*count), label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 3) { //3 = viene da destra
	   	    		label.setBounds(new Rectangle(parent.getWidth(), destinazioneY, label.getWidth(), label.getHeight()));
	   	    		label.setInteragibile(false);
		   	         if(r.getX() < destinazioneX) {// finch� � visibile | r.getX() < 0 condizione per andare verso sinistra
		   	        	 	label.setBounds(new Rectangle(destinazioneX, destinazioneY, label.getWidth(), label.getHeight()));
		   	               ((Timer)evt.getSource()).stop();
		   	               label.setInteragibile(true);
		   	               page.checkComponentsReady();
		   	         }else {
		   	        	 label.setBounds(new Rectangle((int)r.getX()-(20*count), destinazioneY, label.getWidth(), label.getHeight()));
		   	         }
	   	    	 }else if(provenienza == 4) { //4 = viene da sinistra
	   	    		label.setBounds(new Rectangle(0, destinazioneY, label.getWidth(), label.getHeight()));
	   	    		label.setInteragibile(false);
		   	         if(r.getX() > destinazioneX) {// finch� � visibile | r.getX() < 0 condizione per andare verso sinistra
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
	
	public void clicckami() {
		// TODO Auto-generated method stub
		if(interagibile) {
			clicked = !clicked;
			if(clicked) {
				toolTip.setHtmlText("<b>Stato: <font color=\"" + green + "\">Attivo</font></b> <br />" + this.tipText);
				toolTip.repaint();
				this.setIcon(backgroundHOVER_icon); 
			}else {
				toolTip.setHtmlText("<b>Stato: <font color=\"" + red + "\">NON Attivo</font></b> <br />" + this.tipText);
				toolTip.repaint();
				this.setIcon(background_icon);  
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		this.clicckami();
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
		if(interagibile) {
			if(!addedToolTip) {
				this.getParent().add(toolTip, 5, 0);
				addedToolTip = true;
			}
			
	
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
	
			if(clicked) 
				this.setIcon(background_icon);  
			else
				this.setIcon(backgroundHOVER_icon);  
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		if(interagibile) {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			if(toolTip != null) {
				toolTip.setVisible(false);
			}
			
			if(clicked) 
				this.setIcon(backgroundHOVER_icon);  
			else
				this.setIcon(background_icon);
		}
	}

	public ToolTip getToolTip() {
		return toolTip;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if(interagibile) {
			if(e.getSource() instanceof CheckButton){
				if(!addedToolTip) {
					this.getParent().add(toolTip, 5, 0);
					addedToolTip = true;
				}
				if(toolTip != null) {
					toolTip.getWidth();
					toolTip.setBounds(this.getX() + e.getX(), (this.getY() + e.getY()) - toolTip.getPreferredSize().height, toolTip.getPreferredSize().width, toolTip.getPreferredSize().height);
					toolTip.setVisible(true);
				}
			}
		}
	}

	public boolean isInteragibile() {
		return interagibile;
	}

	public void setInteragibile(boolean interagibile) {
		this.interagibile = interagibile;
	}

	public boolean isClicked() {
		return clicked;
	}
	
	
	
}
