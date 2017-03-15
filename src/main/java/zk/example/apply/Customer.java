package zk.example.apply;

public class Customer {
	private int id;
	private String name;
	private String status;

	public Customer(int id, String name, String status) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getStatus() {
		return status;
	}
}
