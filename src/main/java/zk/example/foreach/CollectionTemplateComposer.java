package zk.example.foreach;

import java.util.Optional;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Templates;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zuti.zul.CollectionTemplate;
import org.zkoss.zuti.zul.CollectionTemplateResolver;

public class CollectionTemplateComposer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire("#namesContainer")
	private Component namesContainer;
	private CollectionTemplate namesList;
	private ListModelList<String> namesModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		namesList = new CollectionTemplate(true);
		namesModel = new ListModelList<String>(new String[] {"Chris", "Elisabeth", "Aaron", "Berta", "Daniel"});
		namesList.setModel(namesModel);
		namesList.setTemplate("nameTag");
		namesList.apply(namesContainer);
		
		comp.addEventListener("onClearAll", event -> namesModel.clear());
		comp.addEventListener("onSortAsc", event -> namesModel.sort(String.CASE_INSENSITIVE_ORDER));
		comp.addEventListener("onSortDesc", event -> namesModel.sort(String.CASE_INSENSITIVE_ORDER.reversed()));
		comp.addEventListener("onAddName", (EventListener<ForwardEvent>)this::addName);
		comp.addEventListener("onDropName", (EventListener<ForwardEvent>)this::dropName);
		comp.addEventListener("onRemoveName", event -> namesModel.remove((String) event.getData()));
	}

	private void addName(ForwardEvent event) {
		Textbox nameInput = (Textbox) event.getOrigin().getTarget();
		Optional.ofNullable(nameInput.getValue())
			.filter(v -> !namesModel.contains(v)) //ignore duplicates
			.ifPresent(namesModel::add);
		nameInput.setValue("");
	}

	private void dropName(ForwardEvent event) {
		DropEvent dropEvent = (DropEvent) event.getOrigin();
		String draggedName = (String) dropEvent.getDragged().getAttribute("name");
		String droppedOnName = (String) event.getData();
		namesModel.remove(draggedName); 
		namesModel.add(namesModel.indexOf(droppedOnName), draggedName);
	}
}
