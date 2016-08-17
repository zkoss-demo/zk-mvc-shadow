package zk.problem;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zuti.zul.CollectionTemplate;
import org.zkoss.zuti.zul.CollectionTemplateResolver;

public class ProblemComposer extends SelectorComposer<Div>{
	
	@Wire
	private Div tagList;
	private ListModelList<String> tagsModel = new ListModelList<>();
	
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		tagsModel.add("ajax");
		tagsModel.add("new");
		tagsModel.add("framework");
		tagsModel.add("whooha");
		
		CollectionTemplate tagItems = new CollectionTemplate(true);
		tagItems.setTemplateResolver(new CollectionTemplateResolver<String>() {
			@Override
			public Template resolve(String arg0) {
				return tagList.getTemplate(arg0.contains("new") ? "tag1" : "tag2");
			}
		});
		tagItems.setModel(tagsModel);
		tagItems.apply(tagList);
	}
	
}
