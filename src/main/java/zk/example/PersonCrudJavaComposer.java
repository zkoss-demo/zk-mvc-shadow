package zk.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

public class PersonCrudJavaComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	private ListModelList<Person> personModel;

	@Wire("::shadow#personCrud")
	private Crud<Person> personCrud;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		initPersonModel();
		personCrud.setTemplate("readonly", (PersonTemplate)(this::readonlyPerson));
		personCrud.setTemplate("editable", (PersonTemplate)(this::editablePerson));
		personCrud.init(personModel, Person::new);
	}
	
	private void initPersonModel() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("Peter", 32));
		persons.add(new Person("Jenny", 33));
		persons.add(new Person("Martin", 48));
		personModel = new ListModelList<>(persons);
	} 

	private Div readonlyPerson(Person person) {
		Div div = new Div();
		div.setSclass("personItem readonly");
		div.appendChild(new Label(person.getName() + ", " + person.getAge()));
		
		Stream.of(personCrud.createReadonlyControls(person)).forEach(div::appendChild);
		return div;
	}
	
	private Div editablePerson(Person person) {
		Div div = new Div();
		div.setSclass("personItem editable");
		Textbox nameBox = new Textbox(person.getName());
		div.appendChild(nameBox);
		Intbox ageBox = new Intbox();
		ageBox.setValue(person.getAge());
		div.appendChild(ageBox);
		
		nameBox.addEventListener("onChange", (event) -> person.setName(nameBox.getValue()));
		ageBox.addEventListener("onChange", (event) -> person.setAge(ageBox.getValue()));
		
		div.addEventListener("onOK", (event) -> personCrud.save(person));
		div.addEventListener("onCancel", (event) -> personCrud.cancel(person));
		
		Stream.of(personCrud.createEditableControls(person)).forEach(div::appendChild);
		return div;
	}
	
	interface PersonTemplate extends Template {
		Component render(Person person);
		@Override
		default  Component[] create(Component parent, Component insertBefore, VariableResolver resolver, @SuppressWarnings("rawtypes") Composer composer) {
			Component personComp = render((Person)resolver.resolveVariable("each"));
			parent.insertBefore(personComp, insertBefore);
			return new Component[] {personComp};
		}
		@Override
		default Map<String, Object> getParameters() {
			return null;
		}
	}
}
