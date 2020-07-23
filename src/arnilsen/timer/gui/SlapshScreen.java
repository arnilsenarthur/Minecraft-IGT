package arnilsen.timer.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.UIManager;

/**
 * Splash Screen Class
 * @author Árnilsen Arthur/Infobros2000
 *
 */
public class SlapshScreen extends JFrame 
{
	//Main components
	private JPanel contentPane;
	public JProgressBar progressBar_1;
	
	/**
	 * Splash Screen main constructor
	 */
	public SlapshScreen() 
	{	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setSize(200, 100);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		setResizable(false);
		setUndecorated(true);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("info"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(10, 175, 380, 14);
		contentPane.add(progressBar);
		
		progressBar_1 = new JProgressBar();
		progressBar_1.setBounds(10, 75, 180, 14);
		contentPane.add(progressBar_1);
		
		JLabel lblNewLabel = new JLabel("Minecraft InGame Timer v1.1");
		lblNewLabel.setFont(new Font("Calibri", Font.BOLD, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 180, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("By Árnilsen Arthur/Infobros2000");
		lblNewLabel_1.setFont(new Font("Calibri", Font.BOLD, 12));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(10, 36, 180, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblLoadingStuff = new JLabel("Loading stuff....");
		lblLoadingStuff.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoadingStuff.setFont(new Font("Calibri", Font.BOLD, 12));
		lblLoadingStuff.setBounds(10, 62, 180, 14);
		contentPane.add(lblLoadingStuff);
	}
}
