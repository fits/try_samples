package fits.sample;

import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "Order")
public class Order {
	@Id
	private String id;
	@Column(name = "user_id")
	private String userId;
	@me.prettyprint.hom.annotations.Column(name = "lines")
	private List<OrderLine> lines;

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<OrderLine> getLines() {
		return this.lines;
	}
	public void setLines(List<OrderLine> lines) {
		this.lines = lines;
	}
}
