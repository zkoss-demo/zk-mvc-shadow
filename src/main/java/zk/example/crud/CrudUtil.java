package zk.example.crud;

import java.util.function.BiConsumer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.impl.InputElement;

public class CrudUtil {

	public static <T, V>void handleFieldChange(String eventName, Component target, BiConsumer<T, V> setter) {
		target.addEventListener(eventName, (ForwardEvent event) -> updateField(event, setter));
	}
	
	public static <T, V>void updateField(ForwardEvent event, BiConsumer<T, V> setter) {
		InputElement inputElement = (InputElement) event.getOrigin().getTarget();
		@SuppressWarnings("unchecked")
		V value = (V)(inputElement.getRawValue());
		@SuppressWarnings("unchecked")
		T bean = (T)event.getData();
		setter.accept(bean, value);
	}

}
