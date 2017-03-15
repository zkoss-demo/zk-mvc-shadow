package zk.example.foreach;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zuti.zul.CollectionTemplate;

public class CollectionTemplateRendererComposer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire("#namesContainer")
	private Component namesContainer;
	private CollectionTemplate namesList;
	private ListModelList<String> namesModel;
	private Set<String> goodFriends;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		namesList = new CollectionTemplate(true);
		namesModel = new ListModelList<String>(new String[] {"Chris", "Elisabeth", "Aaron", "Berta", "Daniel"});
		goodFriends = new HashSet<>(Arrays.asList("Aaron", "Daniel"));
		
		namesList.setModel(namesModel);
//		namesList.setTemplateResolver(new CollectionTemplateResolver<String>() {
//			@Override
//			public Template resolve(String nameFromModel) {
//				//could decide to return a different template based on the model object 
//				return new RenderedTemplate<String>() {
//					@Override
//					public Component render(String name) {
//						return renderNameTag(name);
//					}
//				};
//			}
//		});
		namesList.setTemplateResolver(nameFromModel -> (RenderedTemplate<String>) this::renderNameTag); //short version of the commented out below
		namesList.apply(namesContainer);
		
		comp.addEventListener("onClearAll", event -> namesModel.clear());
		comp.addEventListener("onSortAsc", event -> namesModel.sort(String.CASE_INSENSITIVE_ORDER));
		comp.addEventListener("onSortDesc", event -> namesModel.sort(String.CASE_INSENSITIVE_ORDER.reversed()));
		comp.addEventListener("onAddName", (EventListener<ForwardEvent>)this::addName);
	}

	public Component renderNameTag(String name) {
		Span nameTag = new Span();
		nameTag.setSclass("nameTag");
		nameTag.setDraggable("true");
		nameTag.setDroppable("true");
		nameTag.setAttribute("name", name);
		nameTag.addEventListener(Events.ON_DROP, (DropEvent event) -> dropName(name, (String)event.getDragged().getAttribute("name")));
		nameTag.appendChild(new Label(name));
		if(!goodFriends.contains(name)) {
			A removeLink = new A();
			removeLink.setIconSclass("z-icon-times");
			removeLink.addEventListener(Events.ON_CLICK, event -> namesModel.remove(name));
			nameTag.appendChild(removeLink);
		}
		return nameTag;
	}
	
	private void addName(ForwardEvent event) {
		Textbox nameInput = (Textbox) event.getOrigin().getTarget();
		Optional.ofNullable(nameInput.getValue())
			.filter(v -> !namesModel.contains(v)) //ignore duplicates
			.ifPresent(namesModel::add);
		nameInput.setValue("");
	}

	private void dropName(String droppedOnName, String draggedName) {
		namesModel.remove(draggedName); 
		namesModel.add(namesModel.indexOf(droppedOnName), draggedName);
	}
	
	@FunctionalInterface 
	public static interface RenderedTemplate<T> extends Template {
		Component render(T item);
		@Override
		default  Component[] create(Component parent, Component insertBefore, VariableResolver resolver, @SuppressWarnings("rawtypes") Composer composer) {
			@SuppressWarnings("unchecked")
			Component itemComp = render((T)resolver.resolveVariable("each"));
			parent.insertBefore(itemComp, insertBefore);
			return new Component[] {itemComp};
		}
		@Override
		default Map<String, Object> getParameters() {
			return null;
		}
	}	
}
