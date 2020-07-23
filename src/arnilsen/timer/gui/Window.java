package arnilsen.timer.gui;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.xml.bind.DatatypeConverter;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import arnilsen.timer.Data;
import arnilsen.timer.Layout;
import arnilsen.timer.Main;
import arnilsen.timer.Data.Stat;
import arnilsen.timer.Data.Stat.StatIconType;
import arnilsen.timer.external.ComponentResizer;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.Color;

/**
 * Main window class
 * @author infob
 *
 */
public class Window {

	//Timer labels
	public JLabel labelTimer;
	public JLabel labelLastRealTimer;
	
	//Player labels
	public JLabel playerHead;
	
	//Other components
	public JFrame frame;
	BackPanel top_panel;
	BackPanel time_panel;

	//Apply layout on screen (for future gui layout edition)
	public void applyLayout(Layout layout)
	{
		labelTitle.setText(layout.title);
		labelTitle.setForeground(layout.title_color);
		labelSubtitle.setText(layout.subtitle);
		labelSubtitle.setForeground(layout.subtitle_color);
		
		top_panel.apply(layout.title_background);
		time_panel.apply(layout.timer_background);
		
		playerHead.setForeground(layout.username_color);
		frame.getContentPane().setBackground(layout.background_color);
		
		stats_panel.apply(layout.stats_background);
		
		stats_panel.setSize(stats_panel.getWidth(), 25);
		
	}
	
	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the application.
	 */
	public Window(List<Stat> stats_count,Layout l,Data d) {
		initialize(stats_count,l,d);
	}
	
	int pX;
	int pY;
	private JLabel labelTitle;
	private JLabel labelSubtitle;
	private BackPanel stats_panel;
	
	public List<Stat> stats;
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(List<Stat> stats_count,Layout l,Data d) 
	{
		
		this.stats = stats_count;
		frame = new JFrame();
		frame.setBounds(100, 100, 261, 139 + 25 * stats_count.size() + (l.show_skull_data ? 0 : -27));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setResizable(true);
		
		class PopUpDemo extends JPopupMenu {

			private static final long serialVersionUID = 1L;
			JMenuItem anItem;
		    public PopUpDemo() {
		        anItem = new JMenuItem("Exit");
		        anItem.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent e) {
						System.exit(0);
						
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						System.exit(0);
					}
				});
		        add(anItem);
		    }
		}
		
		class PopClickListener extends MouseAdapter {
		    public void mousePressed(MouseEvent e) {
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    public void mouseReleased(MouseEvent e) {
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    private void doPop(MouseEvent e) {
		        PopUpDemo menu = new PopUpDemo();
		        menu.show(e.getComponent(), e.getX(), e.getY());
		    }
		}

		frame.addMouseListener(new PopClickListener());
		
		//Add resizing functionality to screen
		ComponentResizer cr = new ComponentResizer();
		cr.registerComponent(frame);
		
		cr.setMinimumSize(new Dimension(100,frame.getHeight()));
		cr.setMaximumSize(new Dimension(10000,frame.getHeight()));
		
		top_panel = new BackPanel(Color.red,Color.blue,GradientDirection.VERTICAL);
		
		time_panel = new BackPanel(Color.red,Color.blue,GradientDirection.HORIZONTAL);
		
		playerHead = new JLabel("...");
		playerHead.setIcon(new ImageIcon(Window.class.getResource("/res/icons/enderman.png")));
		playerHead.setFont(new Font("Calibri", Font.BOLD, 16));
		playerHead.setVisible(l.show_skull_data);
		
		stats_panel = new BackPanel(Color.red,Color.blue,GradientDirection.VERTICAL);
		stats_panel.setLayout(null);
		
		int i = 0;
		for(Stat s: stats)
		{
			//Create labels for stats
			JLabel j = new JLabel("");
			j.setBounds(2,i * 25,500,25);
			stats_panel.add(j);
			i ++;
			j.setFont(new Font("Calibri", Font.BOLD, 16));
			s.j_label = j;
			s.updateLabel(l,d);
			
			if(s.icon != null)
			{
				//Load icon of a stat
				if(s.icon.type == StatIconType.RESOURCE)
				{
					j.setIcon(new ImageIcon(Window.class.getResource("/res/icons/" + s.icon.value + ".png")));
					
					
				}
				else if(s.icon.type == StatIconType.SKULL || s.icon.type == StatIconType.SKULL_3D)
				{
					try {
						j.setIcon(new ImageIcon(Main.getImageFromUUID(s.icon.value, s.icon.type == StatIconType.SKULL_3D)));
						
						BufferedImage bi = new BufferedImage(
							    j.getIcon().getIconWidth(),
							    j.getIcon().getIconHeight(),
							    BufferedImage.TYPE_INT_RGB);
						Graphics g = bi.createGraphics();
						// paint the Icon to the BufferedImage.
						j.getIcon().paintIcon(null, g, 0,0);
						g.dispose();
						
						bi = Main.makeRoundedCorner(bi, 6);
						
						j.setIcon(new ImageIcon(bi));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(s.icon.type == StatIconType.BASE_64)
				{
					// create a buffered image
					BufferedImage image = null;
					byte[] imageByte;
					
					
					imageByte = DatatypeConverter.parseBase64Binary(s.icon.value);
					ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
					try {
						image = ImageIO.read(bis);
						bis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					image = resize(image, 24, 24);
					j.setIcon(new ImageIcon(image));
				}
			}
			
			
		}
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(top_panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGap(2)
							.addComponent(playerHead, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
						.addComponent(stats_panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
						.addComponent(time_panel, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
					.addGap(0))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(top_panel, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addGap(1)
					.addComponent(playerHead, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addComponent(stats_panel, GroupLayout.PREFERRED_SIZE, 25 * stats_count.size(), GroupLayout.PREFERRED_SIZE)
					.addComponent(time_panel, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
		);
		GroupLayout gl_stats_panel = new GroupLayout(stats_panel);
		gl_stats_panel.setHorizontalGroup(
			gl_stats_panel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 261, Short.MAX_VALUE)
		);
		gl_stats_panel.setVerticalGroup(
			gl_stats_panel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 74, Short.MAX_VALUE)
		);
		stats_panel.setLayout(gl_stats_panel);
		
		//cr.setSnapSize(new Dimension(10, 10));
		//cr.setMaximumSize(new Dimension(...));
		//cr.setMinimumSize(new Dimension(...));
		
		labelTimer = new JLabel("<html>0:00<font size='5'>.0</font></html>");
		labelTimer.setVerticalAlignment(SwingConstants.BOTTOM);
		labelTimer.setFont(new Font("Calibri", Font.BOLD, 48));
		labelTimer.setHorizontalAlignment(SwingConstants.RIGHT);
		
		labelLastRealTimer = new JLabel("Last Real Update: 0:00.0");
		labelLastRealTimer.setFont(new Font("Calibri", Font.BOLD, 12));
		labelLastRealTimer.setHorizontalAlignment(SwingConstants.RIGHT);
		{
			GroupLayout gl_panel = new GroupLayout(time_panel);
			gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_panel.createSequentialGroup()
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel.createSequentialGroup()
								.addContainerGap(106, Short.MAX_VALUE)
								.addComponent(labelLastRealTimer))
							.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
								.addGap(106)
								.addComponent(labelTimer, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)))
						.addContainerGap())
			);
			gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup()
						.addComponent(labelTimer, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(labelLastRealTimer)
						.addContainerGap())
			);
			time_panel.setLayout(gl_panel);
		}
		
		
		labelTitle = new JLabel("Minecraft Any%");
		labelTitle.setVerticalAlignment(SwingConstants.BOTTOM);
		labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		labelTitle.setFont(new Font("Calibri", Font.BOLD, 14));
		
		labelSubtitle = new JLabel("Minecraft Any%");
		labelSubtitle.setVerticalAlignment(SwingConstants.TOP);
		labelSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
		labelSubtitle.setFont(new Font("Calibri", Font.BOLD, 12));
		{
			GroupLayout gl_panel = new GroupLayout(top_panel);
			gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
					.addComponent(labelSubtitle, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
					.addComponent(labelTitle, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
			);
			gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_panel.createSequentialGroup()
						.addComponent(labelTitle, GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
						.addComponent(labelSubtitle, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
			);
			top_panel.setLayout(gl_panel);
		}
		frame.getContentPane().setLayout(groupLayout);
		
		
		
		frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                // Get x,y and store them
                pX = me.getX();
                pY = me.getY();

            }

             public void mouseDragged(MouseEvent me) {

                frame.setLocation(frame.getLocation().x + me.getX() - pX,
                        frame.getLocation().y + me.getY() - pY);
            }
        });

		
		frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
            		
            	if(cr.resizing)
            		return;
            	
                frame.setLocation(frame.getLocation().x + me.getX() - pX,
                        frame.getLocation().y + me.getY() - pY);
            }
        });
	}
	
	/**
	 * Background with gradient support
	 * @author infob
	 *
	 */
	public static class BackPanel extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Color a,b;
		GradientDirection dir;
		
		public BackPanel(Color a,Color b,GradientDirection direction) 
		{
			this.a = a;
			this.b = b;
			this.dir = direction;
		}
		
		public void apply(PanelBackground pb)
		{
			this.a = pb.a;
			this.b = pb.b;
			this.dir = pb.dir;
		}
		
		 @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        Graphics2D g2d = (Graphics2D) g;
		        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		        int w = getWidth();
		        int h = getHeight();
		        Color color1 = a;
		        Color color2 = b;
		        GradientPaint gp = new GradientPaint(0, 0, color1, dir == GradientDirection.HORIZONTAL ? w : 0,dir == GradientDirection.VERTICAL ? h : 0, color2);
		        g2d.setPaint(gp);
		        g2d.fillRect(0, 0, w, h);
		    }
	}
	
	public static enum GradientDirection
	{
		HORIZONTAL,
		VERTICAL,
		NONE
	}
	
	public static class PanelBackground
	{
		public Color a,b;
		public GradientDirection dir;
		public PanelBackground(GradientDirection g,Color a,Color b)
		{
			this.a = a;
			this.b = b;
			
			if(g == GradientDirection.NONE)
				this.b = a;
			
			this.dir = g;
		}
		
		@Override
		public String toString() {
			return dir + " gradient of " + a + " <-> " + b; 
		}
	}
	
	/**
	 * Resize an image
	 * @param img
	 * @param newW
	 * @param newH
	 * @return
	 */
	public static BufferedImage resize(BufferedImage img, int newW, int newH) 
	{ 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
}
