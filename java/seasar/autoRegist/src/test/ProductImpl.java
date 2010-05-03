package test;

public class ProductImpl implements Product {

	private Data data;

	public void setData(Data data) {
		this.data = data;
	}

	public void printOut() {
		if (this.data != null) {
			System.out.printf("name: %s, point: %d", this.data.getName(), this.data.getPoint());
		}
	}

}