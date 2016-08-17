package zk.example.crud;

import java.util.HashMap;
import java.util.function.BiFunction;

import org.zkoss.xel.util.SimpleResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Templates;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zuti.zul.Apply;
import org.zkoss.zuti.zul.CollectionTemplate;
import org.zkoss.zuti.zul.CollectionTemplateResolver;

public class Crud<T> extends Apply implements AfterCompose {
	private static final long serialVersionUID = 1L;

	private Component crudRoot;
	private CrudModel<T> crudModel;

	private CollectionTemplate collectionTemplate;
	
	public Crud() {
		this.setDynamicValue(true);
	}
	
	@Override
	public void afterCompose() {
		super.afterCompose();
		this.crudRoot = this.getFirstInsertion();
		crudRoot.addEventListener("onCreateItem", e -> crudModel.create());
		crudRoot.addEventListener("onEditItem", e -> crudModel.edit(toItem(e)));
		crudRoot.addEventListener("onSaveItem", e -> crudModel.save(toItem(e)));
		crudRoot.addEventListener("onCancelItem", e -> crudModel.cancel(toItem(e)));
		crudRoot.addEventListener("onDeleteItem", e -> crudModel.delete(toItem(e)));

		collectionTemplate = new CollectionTemplate(true);
		collectionTemplate.setTemplateResolver((CollectionTemplateResolver<T>)this::templateForItem);
	}

	public CrudModel<T> getModel() {
		return this.crudModel;
	}
	
	public void setModel(CrudModel<T> crudModel) {
		this.crudModel = crudModel;
		collectionTemplate.setModel(crudModel.getItemsModel());
		collectionTemplate.apply(this.crudRoot.query("#crudItems"));
	}

	public void setTemplateRenderFunction(String templateName, BiFunction<T, Crud<T>, Component> crudRenderFunction) {
		this.setTemplate(templateName, (TemplateRenderFunction<T>)(item -> crudRenderFunction.apply(item, this)));
	}
	
	public Template templateForItem(T item) {
		return Templates.lookup(this, crudModel.isEdited(item) ? "editable" : "readonly");
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
	
	@SuppressWarnings("unchecked")
	protected T toItem(Event event) {
		return (T)event.getData();
	}
}