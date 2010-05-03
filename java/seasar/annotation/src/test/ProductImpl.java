package test;

import org.seasar.framework.container.annotation.tiger.*;

@Component(name="product1")
public class ProductImpl implements Product {

	private Data data;

	@Binding
	public void setData(Data data) {
		this.data = data;
	}

	public void printOut() {
		if (this.data != null) {
			System.out.printf("name: %s, point: %d", this.data.getName(), this.data.getPoint());
		}
	}

}