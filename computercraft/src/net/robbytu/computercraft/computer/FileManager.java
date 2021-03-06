package net.robbytu.computercraft.computer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import net.robbytu.computercraft.CCMain;
import net.robbytu.computercraft.database.ComputerData;

public class FileManager {
	private static File computersDir = new File(CCMain.instance.getDataFolder().getAbsolutePath() + File.separator + "computers" + File.separator);
	
	public static void newComputerEvent(ComputerData data) {
		File computerDir = new File(computersDir, Integer.toString(data.getId()));
		if(!computerDir.exists()) {
			computerDir.mkdir();
		}
		
		// TODO: Copy defaults
	}
	
	public static void deleteComputerEvent(ComputerData data) {
		//File computersDir = new File(CCMain.instance.getDataFolder().getAbsolutePath() + File.separator + "computers");
		if(!computersDir.exists()) computersDir.mkdir();
		
		File computerDir = new File(computersDir, Integer.toString(data.getId()));
		if(computerDir.exists()) {
			removeDirectory(computerDir);
		}
	}
	
	private static String getValidPath(String path, int CID) {
		String[] split = path.split(Pattern.quote(File.separator));
		String newPath = computersDir + File.separator + CID;
		
		for (int i = 0; i < split.length; i++) {
			String pathPart = split[i];
			if (!pathPart.isEmpty()) {
				if (pathPart.equals("..")) {
					newPath = newPath.substring(0, newPath.lastIndexOf(File.separator));
				}
				else {
					newPath += File.separator + pathPart;
				}
			}
		}
		
		if (newPath.startsWith(computersDir.getAbsolutePath() + File.separator + CID)) {
			return newPath;
		}
		return "";
	}
	
	public static String getDir(String path, int CID) {
		String newPath = getValidPath(path, CID);
		
		if (!newPath.equals("")) {
			newPath += File.separator;
			
			File file = new File(computersDir, CID + File.separator + path); 
			if (file.isDirectory()) {
				return newPath.replace(computersDir.getAbsolutePath() + File.separator + CID, "");
			}
		}
		return "";
	}
	
	public static boolean isDir(String path, int CID) {
		return !getDir(path, CID).isEmpty();
	}
	
	public static boolean mkDir(String path, String name, int CID) {
		if (getDir(path + File.separator + name, CID) != "") { //FIXME getDir only is != "" if directory already exists
			File file = new File(computersDir, CID + File.separator + path + File.separator + name); // Potentially dangerous
	
			if(file.exists() && file.isDirectory()) return true;
			else if(file.exists()) return false;
			else if(file.mkdir()) return true;
		}
		return false;
	}
	
	public static File getFile(String path, String name, int CID) {
		String newPath = getValidPath(path + File.separator + name, CID);
		
		if (!newPath.isEmpty()) {
			File file = new File(computersDir, CID + File.separator + path + File.separator + name); // Potentially dangerous
			if (file.exists()) 
				return file;
		}
		return null;
	}
	
	public static String getFileAsString(String path, String name, int CID) {
		StringBuilder strFile = new StringBuilder();
		File file = getFile(path, name, CID);
		
		if (file != null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				
				String output = reader.readLine();

				while(output != null) {
					if (!strFile.toString().isEmpty())
						strFile.append("\n");
					strFile.append(output);
					output = reader.readLine();
				}
				
				return strFile.toString();
			} 
			catch (FileNotFoundException e) {	}
			catch (IOException e) { }
		}
		
		return null;
	}
	
	public static boolean fileExists(String path, String name, int CID) {
		return (getFile(path, name, CID) != null);
	}
	
	public static boolean mkFile(String path, String name, int CID) {
		String newPath = getValidPath(path + File.separator + name, CID);
		if (!newPath.equals("")) {
			File newFile = new File(newPath);
			if (!newFile.exists()) {
				try {
					return newFile.createNewFile();
				} 
				catch (IOException e) { }
			}
		}
		
		return false;
	}
	
	public static void printList(String path, int CID) {
		String[] rom_files = null;
		String[] files = null;
		if(path.equals(File.separator)) {
			// root, also do rom
			File rom = new File(CCMain.instance.getDataFolder().getAbsolutePath() + File.separator + "rom");
			rom_files = rom.list();
			if(rom_files != null) {
				for(int i = 0; i < rom_files.length; i++) {
					if(rom_files[i] != "boot.lua") {
						// Don't print the os file
						CCMain.instance.ComputerThreads.get(CID).gui.addEntry(rom_files[i]);
					}
				}
			}
		}
		
		File file = new File(computersDir, CID + File.separator + path); // Potentially dangerous
		if(file.exists() && file.getAbsolutePath().startsWith(computersDir.getAbsolutePath() + CID)) {
			files = file.list();
			if(files != null) {
				for(int i = 0; i < files.length; i++) {
					CCMain.instance.ComputerThreads.get(CID).gui.addEntry(files[i]);
				}
			}
			else {
				CCMain.instance.ComputerThreads.get(CID).gui.addEntry("No such directory.");
			}
		}
		
		String total = "";
		if(rom_files != null) {
			total += rom_files.length + " files on ROM, ";
		}
		
		if(files == null) {
			total += "0 files in folder";
		}
		else {
			total += files.length + " files in folder";
		}

		CCMain.instance.ComputerThreads.get(CID).gui.addEntry(total);
	}
	
	public static String rm(String path, int CID) {
		if(path == File.separator) {
			return "RM_ROOT_ACCESS_DENIED";
		}
		else {
			String dir = getDir(path, CID);
			
			if (!dir.isEmpty() && !dir.equals(File.separator)) {
				File file = new File(computersDir, CID + File.separator + path); // Potentially dangerous
				if(!file.exists()) {
					return "RM_DOES_NOT_EXIST";
				}
				else if(file.isDirectory()) {
					removeDirectory(file);
					return "RM_DIR_OK";
				}
				else if(!file.isDirectory() && file.delete()) {
					return "RM_FILE_OK";
				}
			}
			return "RM_FAIL";
		}
	}
	
	
	
	public static void removeDirectory(File directory) {
		if(!directory.isDirectory()) return;
		String[] files = directory.list();
		
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				File f = new File(directory, files[i]);
				if(f.isDirectory()) {
					removeDirectory(f);
				}
				else {
					f.delete();
				}
			}
		  }

		  directory.delete();
	}
}
