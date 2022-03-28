package guicomponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import core.Render;
import core.Screen.ScreenMode;
import io.FileParser;
import io.FileParser.ParseMode;
import objects.ObjectRoot;

public class ObjectsTab extends Tab {
	private static final long serialVersionUID = 9129658172388351089L;
	final static int numOfObjects = new File("assets/objectfiles").list().length-1;
	List<File> files;
	List<String> fileNames;


	public ObjectsTab(Render c) {
		super("Objects", 450, 200, 130, 130, 3, numOfObjects, KeyEvent.VK_O, c);
		referenceFiles();
	}

	public void referenceFiles() {
		files = new ArrayList<File>();
		fileNames = new ArrayList<String>();

		File f = new File("assets/objectfiles");
		String[] names = f.list();
		for (String s : names) {
			if (!s.equals(".DS_Store")) {
				files.add(new File("assets/objectfiles/"+s));
				
				String temp = "";
				for (int i=0; i<s.length(); i++) {
					if (s.charAt(i)!='.') temp += s.charAt(i);
					else break;
				}
				fileNames.add(temp);
			}
		}
	}

	@Override
	public void doClick(int x, int y) {
		this.frame.setVisible(false);
		Render.io.notifyMenuBar(name, false);
		
		int ix = x/tileW;
		int iy = (y-headH)/tileW;
		int i = ix+(iy*numInRow);

		if (i>=0&&i<files.size()) {
			/*c.cleanPolyPool(null, false);
			c.objectPool = new ArrayList<ObjectRoot>();*/
			File file = files.get(i);
			if (file!=null) new FileParser(ParseMode.Build, file, c);
		}
	}

	@Override
	public void drawContents(Graphics2D g) {
		int dX = 2; //Little bit of wiggle room
		int dY = headH+2;

		int z = 0;
		for (int i=0; i<files.size(); i++, z++) {
			if (z==numInRow) {
				dY += tileW;
				dX = 2;
				z = 0;
			}

			//Name
			g.setColor(new Color(0, 0, 0, 100));
			if (Render.s.mode==ScreenMode.Night) g.setColor(new Color(255, 255, 255, 200));
			g.setFont(new Font("Verdana", Font.PLAIN, (int) (tileW*0.3)));
			g.drawString(Integer.toString(i+1), (int) (dX+tileW*0.1), (int) (dY+tileW*0.4));
			g.setFont(new Font("Verdana", Font.PLAIN, (int) (tileW*0.12)));
			g.drawString(fileNames.get(i), (int) (dX+tileW*0.1), (int) (dY+tileW*0.9));

			//Draw border
			g.setColor(new Color(120, 120, 120));
			g.drawRect(dX, dY, tileW, tileW);
			dX += tileW;
		}
	}
}
