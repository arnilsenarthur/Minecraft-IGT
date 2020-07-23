package arnilsen.timer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import arnilsen.timer.external.ComponentResizer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Color;

public class ScreenRecognitionConfidence extends JFrame {

	public static ScreenRecognitionConfidence screen;
	
	private JPanel contentPane;

	int pX;
	int pY;
	
	public JProgressBar loading_pb,nether_pb,end_pb;
	
	/**
	 * Create the frame.
	 */
	public ScreenRecognitionConfidence() {
		screen = this;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 180);
		setUndecorated(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblLoadingScreen = new JLabel("Loading Screen:");
		
		loading_pb = new JProgressBar();
		loading_pb.setStringPainted(true);
		loading_pb.setForeground(Color.RED);
		loading_pb.setEnabled(false);
		
		nether_pb = new JProgressBar();
		nether_pb.setStringPainted(true);
		nether_pb.setForeground(Color.RED);
		
		JLabel lblNetherEnvoiriment = new JLabel("Nether Environment:");
		
		end_pb = new JProgressBar();
		end_pb.setStringPainted(true);
		end_pb.setForeground(Color.RED);
		
		JLabel lblTheEndEnvironment = new JLabel("The End Environment:");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addComponent(lblLoadingScreen)
							.addComponent(lblNetherEnvoiriment, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblTheEndEnvironment, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addComponent(loading_pb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(nether_pb, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
						.addComponent(end_pb, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
					.addGap(0))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblLoadingScreen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(loading_pb, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNetherEnvoiriment)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(nether_pb, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblTheEndEnvironment)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(end_pb, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		setAlwaysOnTop(true);
		
		
		ComponentResizer cr = new ComponentResizer();
		cr.registerComponent(this);
		
		cr.setMinimumSize(new Dimension(100,this.getHeight()));
		cr.setMaximumSize(new Dimension(10000,this.getHeight()));
		
		this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                // Get x,y and store them
                pX = me.getX();
                pY = me.getY();

            }

             public void mouseDragged(MouseEvent me) {

            	 ScreenRecognitionConfidence.this.setLocation(ScreenRecognitionConfidence.this.getLocation().x + me.getX() - pX,
            			 ScreenRecognitionConfidence.this.getLocation().y + me.getY() - pY);
            }
        });

		
		this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
            		
            	if(cr.resizing)
            		return;
            	
            	ScreenRecognitionConfidence.this.setLocation(ScreenRecognitionConfidence.this.getLocation().x + me.getX() - pX,
            			ScreenRecognitionConfidence.this.getLocation().y + me.getY() - pY);
            }
        });
	}
}
