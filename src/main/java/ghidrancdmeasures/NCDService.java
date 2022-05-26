package ghidrancdmeasures;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ghidrancdmeasures.NCDResult.NCDPairResult;

public class NCDService {

	private static final String BASE_DIR_NAME = "ncddiff";

	public NCDResult compute(List<Path> paths) {
		return NCDResult.of(paths.stream().flatMap(p -> ncd(p, paths).stream()) // for each file, create the ncd with
																				// all other files
				.collect(Collectors.toUnmodifiableList()));
	}

	private List<NCDPairResult> ncd(Path path, List<Path> paths) {
		return paths.stream().filter(p -> !p.equals(path)) // avoid the same file
				.map(p -> ncd(path, p)) // create NCD metric
				.collect(Collectors.toUnmodifiableList());
	}

	private NCDPairResult ncd(Path path1, Path path2) {
		Double ncd = null;

		Path dir = path1.getParent().resolve(BASE_DIR_NAME);
		try {
			dir.toFile().mkdirs();

			Path zip1 = createZip(dir, path1);
			Long size1 = Files.size(zip1);

			Path zip2 = createZip(dir, path2);
			Long size2 = Files.size(zip2);

			Path concatFile = concatPaths(dir, path1, path2);
			Path zipConcat = createZip(dir, concatFile);
			Long sizeConcat = Files.size(zipConcat);

			ncd = (1.00 * sizeConcat - Math.min(size1, size2)) / (Math.max(size1, size2) * 1.00);

			deleteDirectory(dir);

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return NCDPairResult.of(path1, path2, ncd);
	}

	private void deleteDirectory(Path path) throws IOException {  
		Files.walk(path)
		    .sorted(Comparator.reverseOrder())
		    .map(Path::toFile)
		    .forEach(File::delete);
	}

	private Path createZip(Path dir, Path path) throws IOException {
		String basename = getBasename(path);
		Path zip = dir.resolve(Path.of(basename + ".zip"));

		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;
		FileInputStream fis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(zip.toFile()));
			zos = new ZipOutputStream(bos);
			fis = new FileInputStream(path.toFile());
			ZipEntry zipEntry = new ZipEntry(path.getFileName().toString());
			zos.putNextEntry(zipEntry);

			byte[] buffer = new byte[1024];
			int count;

			while ((count = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, count);
			}

		} finally {
			if (bos != null) {
				bos.close();
			}
			if (zos != null) {
				zos.flush();
				zos.close();
			}
			if (fis != null) {
				fis.close();
			}
		}

		return zip;
	}

	private Path concatPaths(Path dir, Path path1, Path path2) throws IOException {
		Path out = dir.resolve(Path.of(getBasename(path1) + "_" + getBasename(path2)));

		try (FileChannel outChannel = FileChannel.open(out, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

			try (FileChannel in = FileChannel.open(path1, StandardOpenOption.READ)) {
				for (long p = 0, l = in.size(); p < l;)
					p += in.transferTo(p, l - p, outChannel);
			}

			try (FileChannel in = FileChannel.open(path2, StandardOpenOption.READ)) {
				for (long p = 0, l = in.size(); p < l;)
					p += in.transferTo(p, l - p, outChannel);
			}
		}
		return out;
	}

	private String getBasename(Path path) {
		String filename = path.getFileName().toString();
		String[] tokens = filename.split("\\.(?=[^\\.]+$)");
		return tokens[0];
	}

}