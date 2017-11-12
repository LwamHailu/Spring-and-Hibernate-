package edu.bank.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Customer  {
	@Id
	@GeneratedValue
	long customerId;
	
//    @Column(name="name")
	private String name;
	public Customer(){
	}
	
	public Customer(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
}
