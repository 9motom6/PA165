package cz.fi.muni.pa165.dao;

import cz.fi.muni.pa165.entity.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ProductDaoImpl implements ProductDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void create(Product product) {
        entityManager.persist(product);
    }

    @Override
    public List<Product> findAll() {
        return entityManager.createQuery("select p from Product p", Product.class)
                .getResultList();
    }

    @Override
    public Product findById(Long id) {
        return entityManager.find(Product.class, id);
    }

    @Override
    public void remove(Product product) {
        if ( entityManager.contains(product)){
            entityManager.remove(product);
        }else {
            entityManager.remove(entityManager.merge(product));
        }
        entityManager.remove(entityManager.contains(product) ? product : entityManager.merge(product));
    }

    @Override
    public List<Product> findByName(String name) {
        try {
            return entityManager.createQuery("select p from Product p where name = :name", Product.class)
                    .setParameter("name", name).getResultList();
        } catch (NoResultException nrf) {
            return null;
        }
    }
}
