package zk.example;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.xel.util.SimpleResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Templates;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.ListModelList;
import org.zkoss.zuti.zul.Apply;
import org.zkoss.zuti.zul.CollectionTemplate;
import org.zkoss.zuti.zul.CollectionTemplateResolver;

public class CrudHandler<T> {
	private Set<T> editedItems = new HashSet<>();
	private ListModelList<T> items;
	private Component crudApply;
	private Component crudRoot;
	
	public CrudHandler(Apply crudApply, ListModelList<T> items, Supplier<T> newItemSupplier) {
		this.crudApply = crudApply;
		this.crudRoot = crudApply.getFirstInsertion();
		this.items = items;
		crudRoot.addEventListener("onCreateItem", e -> create(newItemSupplier));
		crudRoot.addEventListener("onEditItem", e -> edit(toItem(e)));
		crudRoot.addEventListener("onSaveItem", e -> save(toItem(e)));
		crudRoot.addEventListener("onCancelItem", e -> cancel(toItem(e)));
		crudRoot.addEventListener("onDeleteItem", e -> items.remove(toItem(e)));
	}
	
	public void render() {
		CollectionTemplate collectionTemplate = new CollectionTemplate(true);
		collectionTemplate.setTemplateResolver((CollectionTemplateResolver<T>)this::templateForItem);
		collectionTemplate.setModel(items);
		collectionTemplate.apply(crudRoot);
	}

	public boolean isEdited(T item) {
		return editedItems.contains(item);
	}

	public Template templateForItem(T item) {
		return Templates.lookup(crudApply, isEdited(item) ? "editable" : "readonly");
	}
	
	public  Component[] createReadonlyControls(T item) {
		return createCrudControls(item, "crudReadonlyControls");
	}
	
	public  Component[] createEditableControls(T item) {
		return createCrudControls(item, "crudEditableControls");
	}

	private Component[] createCrudControls(T item, String templateName) {
		return Templates.lookup(crudApply, templateName).create(null, null, new SimpleResolver(Collections.singletonMap("item", item)), null);
	}
	
	private void create(Supplier<T> creator) {
		T item = creator.get();
		items.add(item);
		startEdit(item);
	}

	private void edit(T item) {
		startEdit(item);
	}

	public void save(T itemProxy) {
		finishEdit(itemProxy, true);
	}
	
	public void cancel(T itemProxy) {
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
		@SuppressWarnings("unchecked")
		T originalItem = (T) itemFormProxy.getOriginObject();
		editedItems.remove(itemProxy);
		items.set(items.indexOf(itemProxy), originalItem);
	}
	
	@SuppressWarnings("unchecked")
	protected T toItem(Event event) {
		return (T)event.getData();
	}
}