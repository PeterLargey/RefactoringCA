import java.util.ArrayList;

public class CustomerCurrentAccount extends CustomerAccount 
{
	ATMCard atm;
	double overdraft;
	
public CustomerCurrentAccount()
{
	super();
	this.atm = null;
	this.overdraft = 0.00;
	
}

public CustomerCurrentAccount(ATMCard atm, double overdraft, String number, double balance, ArrayList<AccountTransaction> transactionList)
{
	super(number, balance, transactionList);	
	this.atm = atm;
	this.overdraft = overdraft;
}

public double getOverdraft() {
	return overdraft;
}

public void setOverdraft(double overdraft) {
	this.overdraft = overdraft;
}

public ATMCard getAtm()
{
	return this.atm;
}

public void setAtm(ATMCard atm)
{
	this.atm = atm;
}

}