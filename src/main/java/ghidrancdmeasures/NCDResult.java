package ghidrancdmeasures;

import java.io.File;
import java.util.List;

public class NCDResult {
	
	public static NCDResult of(List<NCDPairwiseResult> pairs) {
		return new NCDResult(pairs);
	}
	public static NCDResult of(NCDPairwiseResult... pairs) {
		return NCDResult.of(List.of(pairs));
	}
	
	
	public static class NCDPairwiseResult {
		public static NCDPairwiseResult of(File f1, File f2, Double similarity) {
			return new NCDPairwiseResult(f1,f2,similarity);
		}

		private File f1, f2;
		private Double similarity;
		
		private NCDPairwiseResult(File f1, File f2, Double similarity) {
			super();
			this.f1 = f1;
			this.f2 = f2;
			this.similarity = similarity;
		}
		
		public File getF1() {
			return f1;
		}
		public File getF2() {
			return f2;
		}
		public Double getSimilarity() {
			return similarity;
		}
	}
	

	public final List<NCDPairwiseResult> pairs;
	
	private NCDResult(List<NCDPairwiseResult> pairs) {
		super();
		this.pairs = pairs;
	}

	public List<NCDPairwiseResult> getPairs() {
		return pairs;
	}
	
}