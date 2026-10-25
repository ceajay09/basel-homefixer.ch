package com.example.myproject.repository;

import com.example.myproject.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for {@link Account} instances. Provides basic CRUD operations due to the extension of {@link MongoRepository}.
 * Includes custom methods for finding an account by email and ID.
 */
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

	Account findByEmail(String email);
	java.util.List<Account> findByEmailIgnoreCase(String email);

	Optional<Account> findById(String id);

	void deleteById(String id);
}
