package zk.example.crud;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

public class CrudModel<T> {

	private Set<T> editedItems = new HashSet<>();
	private Supplier<T> newItemSupplier;
	private ListModelList<T> items;

	public CrudModel(ListModelList<T> items, Supplier<T> newItemSupplier) {
		super();
		this.newItemSupplier = newItemSupplier;
		this.items = items;
	}

	public void create() {
		T item = newItemSupplier.get();
		items.add(item);
		startEdit(item);
	}

	public void edit(T item) {
		startEdit(item);
	}

	public void save(T itemProxy) {
		finishEdit(itemProxy, true);
	}
	
	public void cancel(T itemProxy) {
		finishEdit(itemProxy, false);
	}

	public void delete(T item) {
		items.remove(item);
	}

	public boolean isEdited(T item) {
		return editedItems.contains(item);
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

	public ListModel<T> getItemsModel() {
		return items;
	}
}
