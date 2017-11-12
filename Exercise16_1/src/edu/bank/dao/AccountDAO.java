package edu.bank.dao;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.bank.domain.Account;

public class AccountDAO implements IAccountDAO {

	private SessionFactory sessionFactory;

	@Transactional(propagation = Propagation.REQUIRED)
	public void saveAccount(Account account) {

		sessionFactory.getCurrentSession().save(account);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAccount(Account account) {

		sessionFactory.getCurrentSession().saveOrUpdate(account);

	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Account loadAccount(long accountnumber) {
		return (Account) sessionFactory.getCurrentSession().load(Account.class, accountnumber);

	}

	@Transactional(propagation = Propagation.REQUIRED)
	@SuppressWarnings("unchecked")
	public Collection<Account> getAccounts() {
		Query query = sessionFactory.getCurrentSession().createQuery("From Account");
		return query.list();
	}

}
