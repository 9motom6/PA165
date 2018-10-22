package cz.fi.muni.pa165.tasks;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.PostLoad;
import javax.validation.ConstraintViolationException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import cz.fi.muni.pa165.PersistenceSampleApplicationContext;
import cz.fi.muni.pa165.entity.Category;
import cz.fi.muni.pa165.entity.Product;

 
@ContextConfiguration(classes = PersistenceSampleApplicationContext.class)
public class Task02 extends AbstractTestNGSpringContextTests {
	private Category electro;
	private Category kitchen;

	private Product flashlight;
	private Product kitchenRobot;
	private Product plate;

	@PersistenceUnit
	private EntityManagerFactory emf;

	@BeforeClass
	public void onlyOnce(){
		electro = new Category();
		electro.setName("Electro");

		kitchen = new Category();
		kitchen.setName("Kitchen");

		flashlight = new Product();
		flashlight.setName("Flashlight");
		flashlight.addCategory(electro);

		kitchenRobot = new Product();
		kitchenRobot.setName("Kitchen robot");
		kitchenRobot.addCategory(electro);
		kitchenRobot.addCategory(kitchen);

		plate = new Product();
		plate.setName("Plate");
		plate.addCategory(kitchen);

		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(electro);
		entityManager.persist(kitchen);
		entityManager.persist(flashlight);
		entityManager.persist(kitchenRobot);
		entityManager.persist(plate);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void electroTest(){
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		Category cat = entityManager.find(Category.class, electro.getId());
		assertContainsProductWithName(cat.getProducts(), "Flashlight");
		assertContainsProductWithName(cat.getProducts(), "Kitchen robot");
		entityManager.close();
	}

	@Test
	public void kitchenTest(){
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		Category cat = entityManager.find(Category.class, kitchen.getId());
		assertContainsProductWithName(cat.getProducts(), "Plate");
		assertContainsProductWithName(cat.getProducts(), "Kitchen robot");
		entityManager.close();
	}

	@Test
	public void flashlightTest(){
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		Product cat = entityManager.find(Product.class, flashlight.getId());
		assertContainsCategoryWithName(cat.getCategories(), "Electro");
		entityManager.close();
	}

	@Test
	public void kitchenRobotTest(){
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		Product cat = entityManager.find(Product.class, kitchenRobot.getId());
		assertContainsCategoryWithName(cat.getCategories(), "Electro");
		assertContainsCategoryWithName(cat.getCategories(), "Kitchen");
		entityManager.close();
	}

	@Test
	public void plateTest(){
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		Product cat = entityManager.find(Product.class, plate.getId());
		assertContainsCategoryWithName(cat.getCategories(), "Kitchen");
		entityManager.close();
	}


	private void assertContainsCategoryWithName(Set<Category> categories,
			String expectedCategoryName) {
		for(Category cat: categories){
			if (cat.getName().equals(expectedCategoryName))
				return;
		}
			
		Assert.fail("Couldn't find category "+ expectedCategoryName+ " in collection "+categories);
	}
	private void assertContainsProductWithName(Set<Product> products,
			String expectedProductName) {
		
		for(Product prod: products){
			if (prod.getName().equals(expectedProductName))
				return;
		}
			
		Assert.fail("Couldn't find product "+ expectedProductName+ " in collection "+products);
	}

	@Test(expectedExceptions=ConstraintViolationException.class)
	public void testDoesntSaveNullName(){
		Product nullProduct = new Product();
		nullProduct.setName(null);
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(nullProduct);
		entityManager.getTransaction().commit();
		entityManager.close();
		Product product = entityManager.find(Product.class, nullProduct.getId());
		Assert.assertNull(product);
	}

	
}
