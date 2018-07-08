package br.com.meslin.onibus.aux;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import br.com.meslin.onibus.aux.model.Region;

public class StaticLibrary {
	public static final long interval = 5000;	// in ms (interval to create a thread)
	public static final long variance = 20;		// in % (variance to interval to create a thread)

	public StaticLibrary() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Handles files, jar entries, and deployed jar entries in a zip file (EAR).
	 * 
	 * @return The date if it can be determined, or null if not.
	 */
	public static Date getClassBuildTime() {
		Date d = null;
		Class<?> currentClass = new Object() {}.getClass().getEnclosingClass();
		URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
		if (resource != null) {
			if (resource.getProtocol().equals("file")) {
				try {
					d = new Date(new File(resource.toURI()).lastModified());
				} catch (URISyntaxException ignored) {
				}
			} else if (resource.getProtocol().equals("jar")) {
				String path = resource.getPath();
				d = new Date(new File(path.substring(5, path.indexOf("!"))).lastModified());
			} else if (resource.getProtocol().equals("zip")) {
				String path = resource.getPath();
				File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
				// long jfodLastModifiedLong = jarFileOnDisk.lastModified ();
				// Date jfodLasModifiedDate = new Date(jfodLastModifiedLong);
				try (JarFile jf = new JarFile(jarFileOnDisk)) {
					ZipEntry ze = jf.getEntry(path.substring(path.indexOf("!") + 2));	// Skip the ! and the /
					long zeTimeLong = ze.getTime();
					Date zeTimeDate = new Date(zeTimeLong);
					d = zeTimeDate;
				} catch (IOException | RuntimeException ignored) {
				}
			}
		}
		return d;
	}

	
	
	/**
	 * Reads the filenames file<br>
	 * This file has a filename per line<br>
	 * Each filename represents a region (group) on the map<br>
	 * @param name name of the file with filenames
	 * @return list of filenames
	 */
	public static List<String> readFilenamesFile(String name) {
		// read the file composed by a filename per line
		BufferedReader br = null;
		List<String> filenames = new ArrayList<String>();
		
		try
		{
			br = new BufferedReader(new FileReader(name));
			String filename;
			while((filename = br.readLine()) != null)
			{
				filenames.add(filename.trim());
			}
		}
		catch (IOException e)
		{
			System.err.println("Date = " + new Date());
			e.printStackTrace();
		}
		finally {
			if(br != null)
			{
				try {
					br.close();
				}
				catch (IOException e)
				{
					System.err.println("Date = " + new Date());
					e.printStackTrace();
				}
			}
		}
		return filenames;
	}
	/**
	 * Reads a region from a given file<br>
	 * @param filename	name of the file describing a region
	 * @param regionNumber number of the region
	 * @return a region
	 */
	public static Region readRegion(String filename, int regionNumber) {
		// reads a region. A region is described by an X, Y coordinate per line
		Region region = new Region();
		region.setNumber(regionNumber);
//		System.err.println("[" + this.getClass().getName() + ".SelecionaGrupo] " + " criando região número " + region.getNumero());
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(filename));
			String line;
			while((line = br.readLine()) != null)
			{
				Coordinate coordinate = new Coordinate(
						Double.parseDouble(line.substring(0, line.indexOf(" ")).trim()),
						Double.parseDouble(line.substring(line.indexOf(" ")).trim())
						);
				region.add(coordinate);
			}
		}
		catch (IOException e)
		{
			System.err.println("Date = " + new Date());
			e.printStackTrace();
		}
		finally {
			if(br != null)
			{
				try {
					br.close();
				}
				catch (IOException e)
				{
					System.err.println("Date = " + new Date());
					e.printStackTrace();
				}
			}
		}
		return region;
	}
}
