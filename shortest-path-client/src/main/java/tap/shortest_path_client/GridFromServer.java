package tap.shortest_path_client;

public class GridFromServer {
	private int n;
	public int[][] matrix;
	
	public GridFromServer(int[][] matrix) {
		this.matrix=matrix;
		this.n=matrix.length;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n=n;
	}
	public boolean isEnabled(int i, int j) {
		return matrix[i][j]>0;
	}
	public String getName(int i, int j) {
		return i+"_"+j;
	}

}
