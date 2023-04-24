package org.xbase.com.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.environment.EnvironmentSettings;

public class IOUtil {
	public IOUtil() {
		super();
	}

	public static boolean writeToFile(String path, String fileName, String content) {
		
		if("".equals(content)) {
			System.out.println(MessageConstants.WARNING + fileName +" Content is empty");
		}
		boolean success = true;
		FileWriter fw = null;
		BufferedWriter bw = null;
		File outputFile = null;
		try {
			String uri = null;
			if (path != null) {
				uri = path + File.separatorChar + fileName;
			} else {
				uri = fileName;
			}
			outputFile = new File(uri);
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}

			fw = new FileWriter(outputFile.getAbsoluteFile());
			if (EnvironmentSettings.DEBUGMODE) {
				System.out.println(MessageConstants.DEBUG + "Output file -> " + outputFile.getAbsoluteFile());
			}

			bw = new BufferedWriter(fw);
			bw.write(content);

		} catch (IOException ioe) {
			success = false;
			ioe.printStackTrace();
			System.out.println(MessageConstants.ERROR + "IOE occurred in catch block. " + ioe.getCause());
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
					System.out.println(MessageConstants.ERROR + "IOE occurred in finally block.");
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
					System.out.println(MessageConstants.ERROR + "IOE occurred in finally block.");
				}
			}
		}
		return success;
	}

	public static String readFromFile(String path, String fileName) {
		StringBuilder contents = new StringBuilder();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			String uri = null;
			if (path != null) {
				uri = path + File.separatorChar + fileName;
			} else {
				uri = fileName;
			}

			File inputFile = new File(uri);
			if (!inputFile.exists()) {
				return contents.toString();
			}

			fr = new FileReader(inputFile.getAbsoluteFile());
			if (EnvironmentSettings.DEBUGMODEV) {
				System.out.println(MessageConstants.DEBUGV + "Input file -> " + inputFile.getAbsoluteFile());
			}
			br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				contents.append(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
					System.out.println(MessageConstants.ERROR + "IOE occurred in finally block.");
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
					System.out.println(MessageConstants.ERROR + "IOE occurred in finally block.");
				}
			}
		}
		return contents.toString();
	}

	public static String readFileAsString(String fileName) {
		File databaseFile = new File(fileName);
		if (!databaseFile.exists()) {
			PrintUtil.log(MessageConstants.ERROR + databaseFile.toPath().toString() + " is not present." + MessageConstants.EXITING);
			System.exit(1);
		}
		String fileContent = null;
		try {
			fileContent = new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + " reading " + fileName + PatternConstants.LINESEPERATOR + MessageConstants.EXITING);
			PrintUtil.log(e.getMessage());
			System.exit(1);
		}
		return fileContent;
	}
	
	public static boolean dirExists(String path) {
		File inputFile = null;
		try {
			inputFile = new File(path);
			if (!(inputFile.exists() && inputFile.isDirectory())) {
				return false;
			}

		} catch (Exception ioe) {
			System.out.println(MessageConstants.ERROR + "IOE occurred in catch block.");
		}
		return true;
	}
}
