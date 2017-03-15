package zk.example.foreach;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.ListModelList;

public class NamesComposer extends SelectorComposer<Component> {

	private int counter = 1;
	private ListModelList<String> namesModel = new ListModelList<>(new String[]{"Chris", "Elisabeth", "Aaron", "Berta", "Daniel"});

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	public void doBeforeComposeChildren(Component comp) {
		comp.setAttribute("namesModel", namesModel);
	}

	@Listen("onClick=#addBtn")
	public void addName() {
		namesModel.add("New Name " + counter++);
	}

	@Listen("onRemoveName=#main")
	public void removeName(ForwardEvent event) {
		namesModel.remove(event.getData());
	}
}
