package ghidrancdmeasures;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import ghidrancdmeasures.NCDResult.NCDPairResult;
import ghidrancdmeasures.util.PathHelper;
import ghidrancdmeasures.util.ZipHelper.ZipFunction;

public class NCDService {

	private static final String BASE_DIR_NAME = "ncddiff";
	
	private final ZipFunction zipFunction;
	
	public NCDService(ZipFunction zipFunction) {
		this.zipFunction = zipFunction;
	}

	public NCDResult compute(List<Path> paths) {
		return NCDResult.of(
				paths.stream()
				.flatMap(p -> ncd(p, paths).stream()) // NCD among all files
				.collect(Collectors.toUnmodifiableList()));
	}

	private List<NCDPairResult> ncd(Path path, List<Path> paths) {
		return paths.stream()
				//.filter(p -> !p.equals(path))
				.map(p -> ncd(path, p, zipFunction))
				.collect(Collectors.toUnmodifiableList());
	}

	private static NCDPairResult ncd(Path path1, Path path2, ZipFunction zipFunction) {
		Double similarity = null;
		
		Path dir = path1.getParent().resolve(BASE_DIR_NAME);
		try {
			dir.toFile().mkdirs();
			
			Path zip1 = zipFunction.create(dir, path1);
			Long size1 = Files.size(zip1);

			Path zip2 = zipFunction.create(dir, path2);
			Long size2 = Files.size(zip2);

			Path concatenated = PathHelper.concatPaths2(dir, path1, path2);
			Path zipConcat = zipFunction.create(dir, concatenated);
			Long sizeConcat = Files.size(zipConcat);

			Double ncd = (1.00 * sizeConcat - Math.min(size1, size2)) / (1.00 * Math.max(size1, size2));
			similarity = 1.00 - ncd;
			
			PathHelper.deleteDirectory(dir);

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return NCDPairResult.of(path1, path2, similarity);
	}

	



}