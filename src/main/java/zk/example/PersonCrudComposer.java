package zk.example;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;

import zk.example.crud.Crud;
import zk.example.crud.CrudModel;
import zk.example.crud.CrudUtil;

public class PersonCrudComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	private ListModelList<Person> personModel;

	@Wire("::shadow#personCrud")
	private Crud<Person> personCrud;

	@Override
	public void doAfterCompose(Component main) throws Exception {
		super.doAfterCompose(main);
		initPersonModel();
		personCrud.setModel(new CrudModel<Person>(personModel, Person::new));
		
		CrudUtil.handleFieldChange("onNameChange", main, Person::setName);
		CrudUtil.handleFieldChange("onAgeChange", main, Person::setAge);
	}
	
	private void initPersonModel() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("Peter", 32));
		persons.add(new Person("Jenny", 33));
		persons.add(new Person("Martin", 48));
		personModel = new ListModelList<>(persons);
	}	
}
