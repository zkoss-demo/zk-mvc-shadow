package zk.example.crud;

import java.util.Map;

import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.Template;

@FunctionalInterface 
interface TemplateRenderFunction<T> extends Template {
	Component render(T item);
	@Override
	default  Component[] create(Component parent, Component insertBefore, VariableResolver resolver, @SuppressWarnings("rawtypes") Composer composer) {
		@SuppressWarnings("unchecked")
		Component itemComp = render((T)resolver.resolveVariable("each"));
		parent.insertBefore(itemComp, insertBefore);
		return new Component[] {itemComp};
	}
	@Override
	default Map<String, Object> getParameters() {
		return null;
	}
}