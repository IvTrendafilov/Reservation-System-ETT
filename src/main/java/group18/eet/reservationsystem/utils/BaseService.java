package group18.eet.reservationsystem.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This class is used to provide basic create, read, update, delete functionalities to a service
 * In case of requiring additional checks, overwrite the methods to add your functionality
 * @param <T> - The entity class for which the service is responsible for
 * @param <R> - The repository class, which the service uses
 */

public class BaseService<T, R extends JpaRepository<T, Long>> {

    private final R repository;

    public BaseService(R repository) {
        this.repository = repository;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T findOrNull(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public T create(T entity) {
        return repository.save(entity);
    }

    public Page<T> findPaginated(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Page<T> findPaginatedAndSorted(int page, int size, Sort sort) {
        return repository.findAll(PageRequest.of(page, size, sort));
    }
}
