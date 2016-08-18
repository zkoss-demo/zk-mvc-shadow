package zk.example;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zuti.zul.CollectionTemplate;

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
		namesModel = Stream.of("chris", "elisabeth", "aaron", "berta", "daniel").collect(Collectors.toCollection(ListModelList<String>::new));
		namesList.setModel(namesModel);
		namesList.setTemplate("nameTag");
		namesList.apply(namesContainer);
		
		comp.addEventListener("onClearAll", event -> namesModel.clear());
		comp.addEventListener("onSortAsc", event -> Collections.sort(namesModel));
		comp.addEventListener("onSortDesc", event -> Collections.sort(namesModel, Collections.reverseOrder()));
		comp.addEventListener("onAddName", (EventListener<ForwardEvent>)this::addName);
		comp.addEventListener("onDropName", (EventListener<ForwardEvent>)this::dropName);
		comp.addEventListener("onRemoveName", event -> namesModel.remove((String) event.getData()));
	}

	private void addName(ForwardEvent event) {
		Textbox nameInput = (Textbox) event.getOrigin().getTarget();
		Optional.ofNullable(nameInput.getValue())
			.filter(v -> !v.isEmpty() && !namesModel.contains(v)) //ignore empty input and duplicate names
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
