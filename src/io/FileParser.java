package io;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingWorker;

import core.Vec3;
import core.Render;
import guicomponents.ProgressBar;
import objects.ObjectRoot;
import objects.PlanarRoot;
import objects.Polygon;
import objects.Shape;

public class FileParser {
	Render c;
	public enum ParseMode {Build, Fix};
	int vertCount;
	int faceCount;
	List<Vec3> vertices;
	List<Vec3> faces;
	boolean showDialogue = false;
	ProgressBar pB;

	int inc = 50; //Used to amplify the distances in the object file - for fixing only

	public FileParser(ParseMode m, File f, Render c) {
		this.c = c;
		vertices = new ArrayList<Vec3>();
		faces = new ArrayList<Vec3>();
		if (f!=null) {
			try {
				if (m==ParseMode.Build) {
					parseObjectFile(f);
				}
				else if (m==ParseMode.Fix) fixObjectFile(f);
			} catch (IOException e) {System.out.println("Problem loading object file");}
		}

		if (showDialogue) System.out.println("Terminating...");
	}

	public void parseObjectFile(File f) throws FileNotFoundException {
		//Initial parse for progress bar
		Scanner s = new Scanner(f);
		if (showDialogue) System.out.println("counting items...");
		while (s.hasNext()) {
			String line = s.nextLine();
			if (line.length()>0) {
				if (line.charAt(0)=='v') vertCount++;
				else if (line.charAt(0)=='f') faceCount++;
			}
		}
		s.close();
		if (showDialogue) System.out.println(vertCount+" vertices, "+faceCount+" faces");
		pB = new ProgressBar(c, 3, "Importing Object", new String[] {"Parsing vertices", "Building faces", "Constructing object"}, new int[] {vertCount, faceCount, 100});
		pB.run();

		SwingWorker<?, ?> sW = new SwingWorker<Object, Object>() {
			
			@Override
			protected Object doInBackground() throws Exception {
				Scanner s = new Scanner(f);
				Scanner temp;
				int count = 0;
				boolean doingFaces = false;
				if (showDialogue) System.out.println("parsing vertices.......0");
				
				while (s.hasNext()) {
					String line = s.nextLine();
					count++;

					if (showDialogue) {
						for (int i=0; i<Integer.toString(count-1).length(); i++) System.out.print("\b");
						System.out.print(count);
					}

					if (line.length()>0&&(line.charAt(0)=='v'||line.charAt(0)=='f')) {
						pB.increase(1);
						//try {Thread.sleep(1);} catch (InterruptedException e) {}
						temp = new Scanner(line);

						try {
							temp.next(); //To skip the char
							int a = Integer.parseInt(temp.next());
							int b = Integer.parseInt(temp.next());
							int c = Integer.parseInt(temp.next());

							if (line.charAt(0)=='v') vertices.add(new Vec3(a, b, c));
							else if (line.charAt(0)=='f') {
								if (!doingFaces) {
									pB.increaseStage();
									doingFaces = true;
									if (showDialogue) System.out.print("\nbuilding faces.......0");
								}
								faces.add(new Vec3(a, b, c));
							}
						}
						catch (NumberFormatException e) {if (showDialogue) System.out.println("mismatch");}

						if (temp!=null) temp.close();
					}
				}

				s.close();
				if (showDialogue) System.out.println("\nfinished build.\n");
				pB.increaseStage();
				pB.increase(100);
				try {Thread.sleep(200);} catch (InterruptedException e) {}
				//pB.increaseStage();
				

				buildObject();
				return null;
			}
		};
		
		sW.execute();
	}

	public void buildObject() {
		List<PlanarRoot> polys = new ArrayList<>();
		if (showDialogue) System.out.println(vertices.size()+" vertices and "+faces.size()+" faces");

		int i = 0;
		Color col = new Color(0, 255, 0);
		for (Vec3 f : faces) {
			i++;
			if (f.x>0&&f.x<vertices.size()&&
					f.y>0&&f.y<vertices.size()&&
					f.z>0&&f.z<vertices.size()) {
				//if (col==null||col.getRed()==255) col = new Color(0, 0, 255);
				//else col = new Color(255, 0, 255);
				polys.add(new Polygon(vertices.get(f.x-1), vertices.get(f.y-1), vertices.get(f.z-1), c.getLayer("Dev"), col, c));
			}
		}
		pB.increaseStage();
		c.polyPool.addAll(polys);
		ObjectRoot o = new Shape(new Vec3(0, 0, 0), new Vec3(0, 0, 0), c.getLayer("Dev"), col, null, c);
		c.objectPool.add(o);
	}

	public void fixObjectFile(File f) throws FileNotFoundException, IOException {
		Scanner s = new Scanner(f);
		Scanner temp = null;
		List<String> lines = new ArrayList<>();
		int count = 0;
		boolean doingFaces = false;
		System.out.print("starting fix...\nparsing vertices.......0");

		while (s.hasNext()) {
			String line = s.nextLine();
			String fLine = "";
			temp = new Scanner(line);
			count++;

			for (int i=0; i<Integer.toString(count-1).length(); i++) System.out.print("\b");
			//System.out.print(count);

			if (line.contains("v")) {
				try {
					temp.next();
					int tokenA = (int) -(Float.parseFloat(temp.next())*inc);
					int tokenB = (int) -(Float.parseFloat(temp.next())*inc);
					int tokenC = (int) -(Float.parseFloat(temp.next())*inc);

					fLine += tokenA+" "+tokenC+" "+tokenB;
				}
				catch (NumberFormatException e) {continue;}

				lines.add("v "+fLine);
				if (temp!=null) temp.close();
			}
			else if (line.contains("f")) {
				if (!doingFaces) {
					doingFaces = true;
					System.out.print("\nparsing faces.......0");
				}
				if (line.contains("/")) { //In double format
					try {
						temp.next();
						String tokenA = temp.next();
						String tokenB = temp.next();
						String tokenC = temp.next();

						String tempS = "";
						for (int i=0; i<tokenA.length(); i++) {
							if (tokenA.charAt(i)=='/') {
								tokenA = tempS;
								break;
							}
							else tempS += tokenA.charAt(i);
						}

						tempS = "";
						for (int i=0; i<tokenB.length(); i++) {
							if (tokenB.charAt(i)=='/') {
								tokenB = tempS;
								break;
							}
							else tempS += tokenB.charAt(i);
						}

						tempS = "";
						for (int i=0; i<tokenC.length(); i++) {
							if (tokenC.charAt(i)=='/') {
								tokenC = tempS;
								break;
							}
							else tempS += tokenC.charAt(i);
						}

						fLine += (int) (Float.parseFloat(tokenA))+" "+
								(int) (Float.parseFloat(tokenB))+" "+
								(int) (Float.parseFloat(tokenC));
					}
					catch (NumberFormatException e) {continue;}

					lines.add("f "+fLine);
					if (temp!=null) temp.close();
				}

				else lines.add(line); //In regular format
			}
		}

		System.out.println("\nwriting to obj file...");
		FileWriter fW = new FileWriter(new File("assets/objectfiles/fixed.txt"));
		for (String l : lines) {
			fW.write(l+"\n");
		}

		fW.close();
		System.out.println("finished fix.");
	}

	/*public static void main(String[] args) {
		String name = "boat";
		new FileParser(ParseMode.Fix, new File( "assets/objectfiles/"+name+".txt"), null);
	}*/
}
