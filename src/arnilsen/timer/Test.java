package arnilsen.timer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class Test {

	public static void main(String[] args) {
		File file = new File("C:\\Users\\infob\\Desktop\\backdirt.png");
        BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Integer> it = new ArrayList<Integer>();
		
        // Getting pixel color by position x and y 
		for(int x = 0; x < image.getWidth(); x ++)
			for(int y = 0; y < image.getHeight(); y ++)
			{
		        int clr = image.getRGB(x, y);  
		        if(!it.contains(clr))
		        	it.add(clr);
			}
		
		Integer[] nb = it.toArray(new Integer[0]);
		Arrays.sort(nb);
		
		System.out.println(Arrays.toString(nb));

	}

}
