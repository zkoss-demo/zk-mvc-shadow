package zk.example;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.ListModelList;
import org.zkoss.zuti.zul.CollectionTemplate;

public class CollectionTemplateComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	private ListModelList<Person> personModel;
	private CrudHandler<Person> crudHandler;
	private Component crud;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.crud = comp;
		super.doAfterCompose(comp);

		initPersonModel();

		crudHandler = new CrudHandler<>(comp, personModel, Person::new);
		crudHandler.listen("onChangeName", Person::setName);
		crudHandler.listen("onChangeAge", Person::setAge);
		
		CollectionTemplate collectionTemplate = new CollectionTemplate(true);
		collectionTemplate.setTemplateResolver(this::templateForItem);
		collectionTemplate.setModel(personModel);
		collectionTemplate.apply(comp);
	}

	private Template templateForItem(Object item) {
		return crud.getTemplate(crudHandler.isSelected((Person) item) ? "editable" : "readonly");
	}

	private void initPersonModel() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("Peter", 32));
		persons.add(new Person("Jenny", 33));
		persons.add(new Person("Martin", 48));
		personModel = new ListModelList<>(persons);
	} 
}
