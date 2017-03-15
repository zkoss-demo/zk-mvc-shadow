package zk.example.apply;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;

public class CustomerComposer extends SelectorComposer<Component> {
	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		super.doBeforeComposeChildren(comp);
		comp.setAttribute("currentCustomer", new Customer(123, "McNillies", "Pending Bill"));
		comp.setAttribute("lastCustomer", new Customer(202, "Homington Inc.", "Fully Paid"));
	}
}
