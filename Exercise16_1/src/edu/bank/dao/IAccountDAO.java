package edu.bank.dao;
import java.util.Collection;

import edu.bank.domain.Account;




public interface IAccountDAO {
	public void saveAccount(Account account);
	public void updateAccount(Account account);
	public Account loadAccount(long accountnumber);
	public Collection<Account> getAccounts();
}
