package com.prs.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.prs.business.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	List<User> findByUserNameAndPassword(String userName, String password);
}
