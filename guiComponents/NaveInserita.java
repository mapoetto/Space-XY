package guiComponents;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class NaveInserita extends JLabel{
	
	private boolean inserita = false;
	private ArrayList<Quadrato> listaQuad = new ArrayList<Quadrato>();
	private Nave nave = null;
	private String orientamento;
	private Icon iconaDefault;
	private BufferedImage imgRuotata = null;
	private NaveInserita instanceOfMe = this;
	
	public NaveInserita() {
		super();
		orientamento = "orizzontale";
		iconaDefault = this.getIcon();
	}
	
	public NaveInserita(NaveInserita draggedImg) {
		instanceOfMe = draggedImg;
	}
	
	public void ruota() {
		if(orientamento.equals("orizzontale")) {
			//mettila verticale
			
			imgRuotata = null;
			
			try {
				imgRuotata = rotateClockwise90(ImageIO.read(this.getClass().getResource(nave.getUrl_icon())));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("old bounds-> " + this.getBounds());
			this.setPreferredSize(new Dimension(this.getHeight(), this.getWidth()));
			this.setSize(new Dimension(this.getHeight(), this.getWidth()));
			System.out.println("new bounds-> " + this.getBounds());
			
			this.setIcon(new ImageIcon(imgRuotata.getScaledInstance(this.getWidth(), this.getHeight(), 1)));
			System.out.println("newest bounds-> " + this.getBounds());
			this.repaint();
			orientamento = "verticale";
		}else {
			//mettila orizzontale
			imgRuotata = null;
			try {
				imgRuotata = ImageIO.read(this.getClass().getResource(nave.getUrl_icon()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.setBounds(this.getX(), this.getY(), this.getHeight(), this.getWidth());
			this.setIcon(new ImageIcon(imgRuotata.getScaledInstance(this.getWidth(), this.getHeight(), 1)));
			this.repaint();
			orientamento = "orizzontale";
		}
	}
	
	public static BufferedImage rotateClockwise90(BufferedImage src) {
	    int width = src.getWidth();
	    int height = src.getHeight();

	    BufferedImage dest = new BufferedImage(height, width, src.getType());

	    Graphics2D graphics2D = dest.createGraphics();
	    graphics2D.translate((height - width) / 2, (height - width) / 2);
	    graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
	    graphics2D.drawRenderedImage(src, null);

	    return dest;
	}
	
	public boolean isInserita() {
		return inserita;
	}

	public void setInserita(boolean inserita) {
		this.inserita = inserita;
	}

	public ArrayList<Quadrato> getListaQuad() {
		return listaQuad;
	}

	public void setListaQuad(ArrayList<Quadrato> listaQuad) {
		this.listaQuad = listaQuad;
	}

	public Nave getNave() {
		return nave;
	}

	public void setNave(Nave nave) {
		this.nave = nave;
	}

	public NaveInserita getInstanceOfMe() {
		return instanceOfMe;
	}
	
}
