package guiComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class PaginaNavi{
	
	private JFrame frame2 = new JFrame();
	public NaveInserita draggedImg = null;
	private ArrayList<Nave> naviDaInserire = new ArrayList<Nave>(); //lista che serve per vedere se tutte le navi sono state inserite
	private Griglia griglia;
	
	
	public PaginaNavi (Griglia griglia, int x, int y, int unit�) {
		
		this.griglia = griglia;
		
		frame2.setSize(500,500);
		frame2.setBounds(x, y, 500, 500);
		frame2.setResizable(false);
		frame2.setIconImage(new ImageIcon(this.getClass().getResource("/ICONA_png.png")).getImage());
		frame2.setTitle("SPACE XY IMGS");
		
		frame2.setLayout(new WrapLayout());
		
		ImageIcon icon_nave1 = new ImageIcon(this.getClass().getResource("/nave1.png"));
		Nave nave1 = new Nave("nave1",griglia, "/nave1.png", this, new Dimension((unit�*4),(unit�*2)), new ImageIcon(icon_nave1.getImage().getScaledInstance((unit�*4), (unit�*2), 1)), 8);
		
		griglia.listaDraggedImgs.add(nave1.getDraggedImg());
		
		
		ImageIcon icon_nave2 = new ImageIcon(this.getClass().getResource("/nave3.png"));
		Nave nave2 = new Nave("nave2",griglia, "/nave3.png", this, new Dimension((unit�*8),(unit�*2)), new ImageIcon(icon_nave2.getImage().getScaledInstance((unit�*8), (unit�*2), 1)), 16);
		
		griglia.listaDraggedImgs.add(nave2.getDraggedImg());
		
		
		ImageIcon icon_nave3 = new ImageIcon(this.getClass().getResource("/naverossa.png"));
		Nave nave3 = new Nave("nave3",griglia, "/naverossa.png", this, new Dimension((unit�*4),(unit�*2)), new ImageIcon(icon_nave3.getImage().getScaledInstance((unit�*4), (unit�*2), 1)), 8);
		
		griglia.listaDraggedImgs.add(nave3.getDraggedImg());
		
		
		ImageIcon icon_nave4 = new ImageIcon(this.getClass().getResource("/razzo1.png"));
		Nave nave4 = new Nave("nave4",griglia, "/razzo1.png", this, new Dimension((unit�*2),(unit�*2)), new ImageIcon(icon_nave4.getImage().getScaledInstance((unit�*2), (unit�*2), 1)), 4);
		
		griglia.listaDraggedImgs.add(nave4.getDraggedImg());
		
		
		JLabel ruotaButton = new JLabel();
		ruotaButton.setText("RUOTA");
		ruotaButton.setPreferredSize(new Dimension(100,100));
		ruotaButton.setIcon(new ImageIcon(new ImageIcon((this.getClass().getResource("/ruota.png"))).getImage().getScaledInstance(100, 100, 1)));
		ruotaButton.setOpaque(false);
		ruotaButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
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
			    	
			    	ruotaButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			    	
			    	
			    	if(draggedImg != null) {
						draggedImg.ruota();
					}else {
						//System.out.println("non c'� nulla da ruotare");
					}
			}
			
			public void mousePressed(MouseEvent e) {}

			public void mouseReleased(MouseEvent e) { }

			@Override
			public void mouseEntered(MouseEvent e) {
				ruotaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				ruotaButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		
			
		});
		
		naviDaInserire.add(nave1);
		naviDaInserire.add(nave2);
		naviDaInserire.add(nave3);
		naviDaInserire.add(nave4);
		
		/*
		ArrayList<NaveInserita> tmpList = new ArrayList<NaveInserita>();
		
		for(Nave nv : naviDaInserire) {
			nv.getDraggedImg().setName(nv.getNome());
			tmpList.add(nv.getDraggedImg());
		}
		
		griglia.setListaDraggedImgs(tmpList);
		*/
		
		griglia.listaNavi.add(nave1);
		griglia.listaNavi.add(nave2);
		griglia.listaNavi.add(nave3);
		griglia.listaNavi.add(nave4);
		
		this.griglia.getNaviDaInserire().add(nave1);
		this.griglia.getNaviDaInserire().add(nave2);
		this.griglia.getNaviDaInserire().add(nave3);
		this.griglia.getNaviDaInserire().add(nave4);
		
		griglia.setNaviDaInserire(naviDaInserire);
		
		
		JLabel label = new JLabel();
		
		try {
            label = new JLabel(new ImageIcon(ImageIO.read(this.getClass().getResource("/backgroundHANGAR2.jpg"))));
            frame2.setContentPane(label);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		JLayeredPane lyp = new JLayeredPane();
		lyp.setPreferredSize(new Dimension(500,500));
		lyp.setOpaque(false);
		lyp.setLayout(null);
		
		lyp.add(nave1,6,0);
		lyp.add(nave2,5,0);
		lyp.add(nave3,4,0);
		lyp.add(nave4,3,0);
		lyp.add(ruotaButton,2,0);
		
		nave2.setBounds(40,10,(unit�*8),(unit�*2));
		nave1.setBounds(40,160,(unit�*4),(unit�*2));
		nave3.setBounds(250,160,(unit�*4),(unit�*2));
		nave4.setBounds(100,300,(unit�*2),(unit�*2));
		ruotaButton.setBounds(300,300,100,100);
		
		label.add(lyp);
		
		frame2.setLayout(new WrapLayout());
	}


	public JFrame getFrame2() {
		return frame2;
	}


	public Griglia getGriglia() {
		return griglia;
	}


	public void setGriglia(Griglia griglia) {
		this.griglia = griglia;
	}

}
