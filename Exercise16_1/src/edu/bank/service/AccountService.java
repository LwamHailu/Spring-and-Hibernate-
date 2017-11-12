package edu.bank.service;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.bank.dao.AccountDAO;
import edu.bank.dao.IAccountDAO;
import edu.bank.domain.Account;
import edu.bank.domain.Customer;
import edu.bank.jms.IJMSSender;
import edu.bank.jms.JMSSender;
import edu.bank.logging.ILogger;
import edu.bank.logging.Logger;

public class AccountService implements IAccountService {
	private SessionFactory sessionFactory;
	private IAccountDAO accountDAO;
	private ICurrencyConverter currencyConverter;
	private IJMSSender jmsSender;
	private ILogger logger;

	public AccountService() {
		accountDAO = new AccountDAO();
		currencyConverter = new CurrencyConverter();
		jmsSender = new JMSSender();
		logger = new Logger();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public IAccountDAO getAccountDAO() {
		return accountDAO;
	}

	public void setAccountDAO(IAccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Account createAccount(long accountNumber, String customerName) {
		Account account = new Account(accountNumber);
		Customer customer = new Customer(customerName);
		account.setCustomer(customer);

		accountDAO.saveAccount(account);

		logger.log(
				"createAccount with parameters accountNumber= " + accountNumber + " , customerName= " + customerName);
		return account;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deposit(long accountNumber, double amount) {

		Account account = accountDAO.loadAccount(accountNumber);
		account.deposit(amount);
		accountDAO.updateAccount(account);

		logger.log("deposit with parameters accountNumber= " + accountNumber + " , amount= " + amount);
		if (amount > 10000) {
			jmsSender.sendJMSMessage("Deposit of $ " + amount + " to account with accountNumber= " + accountNumber);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Account getAccount(long accountNumber) {

		Account account = accountDAO.loadAccount(accountNumber);

		return account;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Collection<Account> getAllAccounts() {

		Collection<Account> accounts = accountDAO.getAccounts();

		return accounts;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void withdraw(long accountNumber, double amount) {

		Account account = accountDAO.loadAccount(accountNumber);
		account.withdraw(amount);
		accountDAO.updateAccount(account);

		logger.log("withdraw with parameters accountNumber= " + accountNumber + " , amount= " + amount);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void depositEuros(long accountNumber, double amount) {

		Account account = accountDAO.loadAccount(accountNumber);
		double amountDollars = currencyConverter.euroToDollars(amount);
		account.deposit(amountDollars);
		accountDAO.updateAccount(account);

		logger.log("depositEuros with parameters accountNumber= " + accountNumber + " , amount= " + amount);
		if (amountDollars > 10000) {
			jmsSender.sendJMSMessage("Deposit of $ " + amount + " to account with accountNumber= " + accountNumber);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void withdrawEuros(long accountNumber, double amount) {

		Account account = accountDAO.loadAccount(accountNumber);
		double amountDollars = currencyConverter.euroToDollars(amount);
		account.withdraw(amountDollars);
		accountDAO.updateAccount(account);

		logger.log("withdrawEuros with parameters accountNumber= " + accountNumber + " , amount= " + amount);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void transferFunds(long fromAccountNumber, long toAccountNumber, double amount, String description) {

		Account fromAccount = accountDAO.loadAccount(fromAccountNumber);
		Account toAccount = accountDAO.loadAccount(toAccountNumber);
		fromAccount.transferFunds(toAccount, amount, description);
		accountDAO.updateAccount(fromAccount);
		accountDAO.updateAccount(toAccount);
		logger.log("transferFunds with parameters fromAccountNumber= " + fromAccountNumber + " , toAccountNumber= "
				+ toAccountNumber + " , amount= " + amount + " , description= " + description);

		if (amount > 10000) {
			jmsSender.sendJMSMessage("TransferFunds of $ " + amount + " from account with accountNumber= " + fromAccount
					+ " to account with accountNumber= " + toAccount);
		}
	}
}
