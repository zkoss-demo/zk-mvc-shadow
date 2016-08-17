package zk.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;

public class FormProxyTest {

	private static final String INITIAL_NAME = "Initial Name";
	private static final String UPDATED_NAME = "Updated Name";
	private static final Integer INITIAL_AGE = 10;
	private static final Integer UPDATED_AGE = 20;
	private Person person;
	private Person personProxy;
	private FormProxyObject personForm;
	
	@Before
	public void setup() {
		person = new Person(INITIAL_NAME, INITIAL_AGE);
		personProxy = ProxyHelper.createProxyIfAny(person);
		personForm = (FormProxyObject)personProxy;
	}
	
	@Test
	public void testSubmitChanges() {
		updatePerson();
		personForm.submitToOrigin(null); //submit the changes to the original object
		
		Assert.assertFalse(personForm.isFormDirty());
		assertUpdatedState(person);
		assertUpdatedState(personProxy);
	}

	@Test
	public void testCancelChanges() {
		updatePerson();
		personForm.resetFromOrigin(); //cancel the changes
		
		Assert.assertFalse(personForm.isFormDirty());
		//both objects should be back to initial state
		assertInitialState(person);
		assertInitialState(personProxy);
	}

	private void updatePerson() {
		assertInitialState(person);
		assertInitialState(personProxy);
		Assert.assertFalse(personForm.isFormDirty());

		personProxy.setName(UPDATED_NAME);
		personProxy.setAge(UPDATED_AGE);
		
		assertInitialState(person);
		assertUpdatedState(personProxy);
		Assert.assertTrue(personForm.isFormDirty());
	}

	private void assertInitialState(Person person) {
		Assert.assertEquals(INITIAL_NAME, person.getName());
		Assert.assertEquals(INITIAL_AGE, person.getAge());
	}
	
	private void assertUpdatedState(Person person) {
		Assert.assertEquals(UPDATED_NAME, person.getName());
		Assert.assertEquals(UPDATED_AGE, person.getAge());
	}
	
}

