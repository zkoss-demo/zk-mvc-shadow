package zk.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.xel.util.SimpleResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Templates;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.ListModelList;
import org.zkoss.zuti.zul.Apply;
import org.zkoss.zuti.zul.CollectionTemplate;
import org.zkoss.zuti.zul.CollectionTemplateResolver;

public class Crud<T> extends Apply implements AfterCompose {
	private static final long serialVersionUID = 1L;

	private Component crudRoot;
	private Set<T> editedItems = new HashSet<>();
	private Supplier<T> newItemSupplier;
	private ListModelList<T> items;
	
	public Crud() {
		this.setTemplateURI("crudTemplate.zul");
		this.setDynamicValue(true);
	}
	
	@Override
	public void afterCompose() {
		super.afterCompose();
		this.crudRoot = this.getFirstInsertion();
		crudRoot.addEventListener("onCreateItem", e -> create(newItemSupplier));
		crudRoot.addEventListener("onEditItem", e -> edit(toItem(e)));
		crudRoot.addEventListener("onSaveItem", e -> save(toItem(e)));
		crudRoot.addEventListener("onCancelItem", e -> cancel(toItem(e)));
		crudRoot.addEventListener("onDeleteItem", e -> items.remove(toItem(e)));
	}

	public void init(ListModelList<T> items, Supplier<T> newItemSupplier) {
		this.items = items;
		this.newItemSupplier = newItemSupplier;
		CollectionTemplate collectionTemplate = new CollectionTemplate(true);
		collectionTemplate.setTemplateResolver((CollectionTemplateResolver<T>)this::templateForItem);
		collectionTemplate.setModel(items);
		collectionTemplate.apply(crudRoot);
	}
	
	public void setTemplateRenderFunction(String templateName, TemplateRenderFunction<T, Component> renderFunction) {
		this.setTemplate(templateName, renderFunction);
	}
	
	public boolean isEdited(T item) {
		return editedItems.contains(item);
	}

	public Template templateForItem(T item) {
		return Templates.lookup(this, isEdited(item) ? "editable" : "readonly");
	}
	
	public  Component[] createReadonlyControls(T item) {
		return createCrudControls(item, "crudReadonlyControls");
	}
	
	public  Component[] createEditableControls(T item) {
		return createCrudControls(item, "crudEditableControls");
	}

	private Component[] createCrudControls(T item, String templateName) {
		HashMap<String, Object> params = new HashMap<>(2);
		params.put("item", item);
		params.put("crud", crudRoot);
		return Templates.lookup(this, templateName).create(null, null, new SimpleResolver(params), null);
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