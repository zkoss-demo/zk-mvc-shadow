package zk.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

import zk.example.crud.Crud;

public class PersonCrudJavaComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	private ListModelList<Person> personModel;

	private Crud<Person> personCrud;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		personCrud = new Crud<>();
		personCrud.setTemplateURI("/WEB-INF/zul/crud/crudTemplate.zul");
		personCrud.setShadowHost(comp, null);
		personCrud.afterCompose();

		initPersonModel();

		personCrud.setTemplateRenderFunction("readonly", this::renderReadonlyPerson);
		personCrud.setTemplateRenderFunction("editable", this::renderEditablePerson);
		personCrud.init(personModel, Person::new);
	}
	
	private void initPersonModel() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("Peter", 32));
		persons.add(new Person("Jenny", 33));
		persons.add(new Person("Martin", 48));
		personModel = new ListModelList<>(persons);
	} 

	private Component renderReadonlyPerson(Person person, Crud<Person> crud) {
		Div div = new Div();
		div.setSclass("personItem readonly");
		div.appendChild(new Label(person.getName() + ", " + person.getAge()));
		
		Stream.of(crud.createReadonlyControls(person)).forEach(div::appendChild);
		return div;
	}
	
	private Div renderEditablePerson(Person person, Crud<Person> crud) {
		Div div = new Div();
		div.setSclass("personItem editable");
		Textbox nameBox = new Textbox(person.getName());
		div.appendChild(nameBox);
		Intbox ageBox = new Intbox();
		ageBox.setValue(person.getAge());
		div.appendChild(ageBox);
		
		nameBox.addEventListener("onChange", (event) -> person.setName(nameBox.getValue()));
		ageBox.addEventListener("onChange", (event) -> person.setAge(ageBox.getValue()));
		
		div.addEventListener("onOK", (event) -> crud.save(person));
		div.addEventListener("onCancel", (event) -> crud.cancel(person));
		
		Stream.of(crud.createEditableControls(person)).forEach(div::appendChild);
		return div;
	}
}
