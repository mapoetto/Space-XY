package guiComponents;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;

public class ToolTip extends JTextPane{
	
	private Image background;
	private String htmlText;
	private int widthHTMLTABLE,width, height;
	private Font font;
	
	public ToolTip(String htmlText, int width){
		super();
		
		this.width = (width<100) ? 101 : width; //il minimo consentito è 100
		this.height = (int)(this.width*0.61649);
		
		int widthHTMLTABLE = (int)(this.width * 0.13820);
		
		this.widthHTMLTABLE = widthHTMLTABLE;
		
		this.setPreferredSize(new Dimension(this.width, height));
		
		Decoder decoder = Base64.getDecoder();
		ByteArrayInputStream font_InputStream = new ByteArrayInputStream(decoder.decode(Constants.ttf_Font_file));
		font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, font_InputStream);
			this.setFont(font);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.htmlText = htmlText;
		this.setContentType("text/html");
		this.setEditable(false);
		this.setOpaque(false);
		this.setText("<html><table><tr><td style=\"width: " + widthHTMLTABLE + "px;\">&nbsp;</td><td style=\"font-family: " + font.getFamily() + " ;\">" + this.htmlText + "</td></tr></table></html>");
		this.setVisible(true);
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
    protected void paintComponent(Graphics g) {

    	
		Decoder decoder = Base64.getDecoder();
		
		BufferedImage background_image = null;
		byte[] background_imageByte;
		
		background_imageByte = decoder.decode(Constants.background_Image_ToolTip);
		
		ByteArrayInputStream background_InputStream = new ByteArrayInputStream(background_imageByte);
		
		try {
			background_image = ImageIO.read(background_InputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		background=getScaledImage(background_image, width, height);
		g.drawImage(background, 0, 0, this);

        super.paintComponent(g);
    }
	
	public String getHtmlText() {
		return htmlText;
	}

	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
		this.setText("<html><table><tr><td style=\"width: " + this.widthHTMLTABLE + "px;\">&nbsp;</td><td style=\"font-family: " + font.getFamily() + " ;\">" + this.htmlText + "</td></tr></table></html>");
	}
	
}
