package ghidrancdmeasures;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ghidrancdmeasures.NCDResult.NCDPairwiseResult;

public class NCDService {
	
	
	public NCDResult compute(List<File> files) throws IOException {
		List<NCDPairwiseResult> results = new ArrayList<>();
		for (File f1: files) {
			FileInputStream fis1 = new FileInputStream(f1);
			FileChannel fc1 = fis1.getChannel();
			long fc1Start = fc1.position();
			
			for (File f2: files) {
				if (Files.isSameFile(f1.toPath(), f2.toPath())) { continue; }
				
				FileInputStream fis2 = new FileInputStream(f2);
				Double similarity = 1-NCD(f1.getName(), fis1, f2.getName(), fis2);
				results.add(NCDPairwiseResult.of(f1, f2, similarity));
				
				fis2.close();			// close inner inputStream
				fc1.position(fc1Start); // position outer inputStream to start
			}
			
			fis1.close(); // close outer inputStream
		}

		return NCDResult.of(results);
	}
	
	private Double NCD(String fileName1, FileInputStream fis1, String fileName2, FileInputStream fis2) throws IOException {
		long size1 = zipSize(fileName1, fis1);
		long size2 = zipSize(fileName2, fis2);
		
		long concatSize = 0; // TODO
		
		return (concatSize - Math.min(size1, size2)) / Math.max(size1, size2) * 1.00 ;
	}

	
	// DOES NOT CLOSE THE INPUT STREAM!
	private long zipSize(String fileName, InputStream is) throws IOException {
		String zipName = "/tmp/" + fileName + ".zip";
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipName));
		ZipOutputStream zos = new ZipOutputStream(bos);
		ZipEntry entry = new ZipEntry(fileName);
		zos.putNextEntry(entry);
		
		// create the zip for the inputStream
		byte[] b = new byte[1024];
		int count;
		
		while ((count = is.read(b))>0) {
			zos.write(b, 0, count);
		}
		// close all outputStreams
		zos.flush();
		zos.close();
		bos.close();
		
		
		// return zip size
		Path zipPath = Paths.get(zipName);
		long zipSize = Files.size(zipPath);
		Files.delete(zipPath);
		return zipSize;
	}
}