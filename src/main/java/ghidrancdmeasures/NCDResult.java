package ghidrancdmeasures;

import java.nio.file.Path;
import java.util.List;

public class NCDResult {
	
	public static NCDResult of(List<NCDPairResult> pairs) {
		return new NCDResult(pairs);
	}
	public static NCDResult of(NCDPairResult... pairs) {
		return NCDResult.of(List.of(pairs));
	}
	
	
	public static class NCDPairResult {
		public static NCDPairResult of(Path p1, Path p2, Double similarity) {
			return new NCDPairResult(p1,p2,similarity);
		}

		private Path p1, p2;
		private Double similarity;
		
		private NCDPairResult(Path p1, Path p2, Double similarity) {
			super();
			this.p1 = p1;
			this.p2 = p2;
			this.similarity = similarity;
		}
		
		public Double getSimilarity() {
			return similarity;
		}
		public void setSimilarity(Double similarity) {
			this.similarity = similarity;
		}
		public Path getP1() {
			return p1;
		}
		public void setP1(Path p1) {
			this.p1 = p1;
		}
		public Path getP2() {
			return p2;
		}
		public void setP2(Path p2) {
			this.p2 = p2;
		}
	}
	

	private final List<NCDPairResult> pairs;
	
	private NCDResult(List<NCDPairResult> pairs) {
		super();
		this.pairs = pairs;
	}

	public List<NCDPairResult> getPairs() {
		return pairs;
	}
	
}