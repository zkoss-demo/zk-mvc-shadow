package zk.example;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.impl.InputElement;

public class CrudHandler<T> {
	private Set<T> editedItems = new HashSet<>();
	private ListModelList<T> items;
	private Component crudRoot;
	public CrudHandler(Component crudRoot, ListModelList<T> items, Supplier<T> creator) {
		this.crudRoot = crudRoot;
		this.items = items;
		crudRoot.addEventListener("onCreateItem", e -> create(creator));
		crudRoot.addEventListener("onEditItem", e -> edit(toItem(e)));
		crudRoot.addEventListener("onSaveItem", e -> save(toItem(e)));
		crudRoot.addEventListener("onCancelItem", e -> cancel(toItem(e)));
		crudRoot.addEventListener("onDeleteItem", e -> items.remove(toItem(e)));
	}

	protected void create(Supplier<T> creator) {
		T item = creator.get();
		items.add(item);
		startEdit(item);
	}

	protected void edit(T item) {
		startEdit(item);
	}

	protected void save(T itemProxy) {
		finishEdit(itemProxy, true);
	}
	
	protected void cancel(T itemProxy) {
		finishEdit(itemProxy, false);
	}

	private void startEdit(T originalItem) {
		T itemProxy = ProxyHelper.createProxyIfAny(originalItem);
		editedItems.add(itemProxy);
		items.set(items.indexOf(originalItem), itemProxy);
	}
	
	private void finishEdit(T itemProxy, boolean saveChanges) {
		FormProxyObject itemFormProxy = (FormProxyObject)itemProxy;
		if(saveChanges) {
			itemFormProxy.submitToOrigin(null);
		} else {
			itemFormProxy.resetFromOrigin();
		}
		T originalItem = (T) itemFormProxy.getOriginObject();
		editedItems.remove(itemProxy);
		items.set(items.indexOf(itemProxy), originalItem);
	}
	
	public boolean isSelected(T item) {
		return editedItems.contains(item);
	}
	
	public <V>boolean listen(String eventName, BiConsumer<T, V> consumer) {
		return crudRoot.addEventListener(eventName, event -> applyChange(consumer, (ForwardEvent)event));
	}
	
	private <V> void applyChange(BiConsumer<T, V> consumer, ForwardEvent event) {
		InputElement inputElement = (InputElement)event.getOrigin().getTarget();
		consumer.accept(toItem(event), (V)inputElement.getRawValue());
	}

	protected T toItem(Event event) {
		return (T)event.getData();
	}
}