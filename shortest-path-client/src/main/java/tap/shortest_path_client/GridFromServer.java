package tap.shortest_path_client;

public class GridFromServer {
	private int n;
	public int[][] matrix;
	private int id;
	public GridFromServer(int[][] matrix, int id) {
		this.matrix=matrix;
		this.n=matrix.length;
		this.setId(id);
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n=n;
	}
	public boolean isEnabled(int i, int j) {
		try {
			return matrix[i][j]>0;
		}catch(Exception exc) {
			return false;
		}
	}
	public String getName(int i, int j) {
		return i+"_"+j;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
