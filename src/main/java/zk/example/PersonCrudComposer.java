package zk.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.zkoss.lang.Generics;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.impl.InputElement;

public class PersonCrudComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	private ListModelList<Person> personModel;

	@Wire("::shadow#personCrud")
	private Crud<Person> personCrud;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		initPersonModel();
		personCrud.init(personModel, Person::new);
	}
	
	private void initPersonModel() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("Peter", 32));
		persons.add(new Person("Jenny", 33));
		persons.add(new Person("Martin", 48));
		personModel = new ListModelList<>(persons);
	}
	
	@Listen("onNameChange=#main") 
	public void onNameChange(ForwardEvent event) {
		updateField(event, Person::setName);
	}
	
	@Listen("onAgeChange=#main") 
	public void onAgeChange(ForwardEvent event) {
		updateField(event, Person::setAge);
	}
	
	private <T>void updateField(ForwardEvent event, BiConsumer<Person, T> setter) {
		Person person = (Person)event.getData();
		InputElement inputElement = (InputElement) event.getOrigin().getTarget();
		T value = Generics.cast(inputElement.getRawValue());
		setter.accept(person, (T)value);
	}
	
}
