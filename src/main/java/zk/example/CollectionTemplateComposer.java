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
import org.zkoss.zuti.zul.Apply;

public class CollectionTemplateComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	private ListModelList<Person> personModel;
	private CrudHandler<Person> crudHandler;

	@Wire("::shadow#crudApply")
	private Apply crudApply;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		initPersonModel();

		crudApply.setTemplate("readonly", (PersonTemplate)(this::readonlyPerson));
		crudApply.setTemplate("editable", (PersonTemplate)(this::editablePerson));

		crudHandler = new CrudHandler<Person>(crudApply, personModel, Person::new);
		crudHandler.render();
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
		
		Stream.of(crudHandler.createReadonlyControls(person)).forEach(div::appendChild);
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
		
		div.addEventListener("onOK", (event) -> crudHandler.save(person));
		div.addEventListener("onCancel", (event) -> crudHandler.cancel(person));
		
		Stream.of(crudHandler.createEditableControls(person)).forEach(div::appendChild);
		return div;
	}
	
	interface PersonTemplate extends Template {
		Component render(Person person);
		@Override
		default  Component[] create(Component parent, Component insertBefore, VariableResolver resolver, Composer composer) {
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
