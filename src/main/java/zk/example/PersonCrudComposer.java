package zk.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
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
		
		handleFieldChange("onNameChange", Person::setName);
		handleFieldChange("onAgeChange", Person::setAge);
	}
	
	private void initPersonModel() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("Peter", 32));
		persons.add(new Person("Jenny", 33));
		persons.add(new Person("Martin", 48));
		personModel = new ListModelList<>(persons);
	}

	private <T, V>void handleFieldChange(String eventName, BiConsumer<T, V> setter) {
		this.getSelf().addEventListener(eventName, (ForwardEvent event) -> updateField(event, setter));
	}
	
	private <T, V>void updateField(ForwardEvent event, BiConsumer<T, V> setter) {
		InputElement inputElement = (InputElement) event.getOrigin().getTarget();
		@SuppressWarnings("unchecked")
		V value = (V)(inputElement.getRawValue());
		@SuppressWarnings("unchecked")
		T bean = (T)event.getData();
		setter.accept(bean, value);
	}
	
}
