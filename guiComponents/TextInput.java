package guiComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class TextInput extends JTextField{
	
	private Decoder decoder = Base64.getDecoder();
	private int width;
	private int height;
	
	public TextInput(int width) {
		super();

		this.width = (width<100) ? 101 : width; //il minimo consentito � 100
		this.height = (int)(this.width*0.29759);
		
		this.setOpaque(false);
		this.setForeground(Color.white);
		
		ByteArrayInputStream font_InputStream = new ByteArrayInputStream(decoder.decode(Constants.ttf_Font_file));
		
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, font_InputStream);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.setFont(font.deriveFont(22f));
		
		try {
			font_InputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Caret caret = this.getCaret();
		caret.setMagicCaretPosition(new Point(10,10));
		
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	}
	
	
    @Override
    protected void paintComponent(Graphics g) {
    	
		BufferedImage background_image = null;
		byte[] background_imageByte;
		
		Decoder decoder = Base64.getDecoder();
		
		background_imageByte = decoder.decode(Constants.background_Image_TextInput);
		
		ByteArrayInputStream background_InputStream = new ByteArrayInputStream(background_imageByte);
		
		try {
			background_image = ImageIO.read(background_InputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		background_image = (BufferedImage)getScaledImage(background_image, this.width, height);
		
        g.drawImage(background_image, 0, 0, this);
         
 		try {
			background_InputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        super.paintComponent(g);
    }
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();
	    
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
}
