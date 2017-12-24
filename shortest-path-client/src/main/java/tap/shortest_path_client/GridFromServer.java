package tap.shortest_path_client;

import java.util.Arrays;

public class GridFromServer {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + Arrays.deepHashCode(matrix);
		result = prime * result + n;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridFromServer other = (GridFromServer) obj;
		if (id != other.id)
			return false;
		if (!Arrays.deepEquals(matrix, other.matrix))
			return false;
		if (n != other.n)
			return false;
		return true;
	}
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
