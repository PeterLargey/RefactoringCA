import javax.swing.JFrame;
import javax.swing.JOptionPane;

public interface IGUIResponses {
	
	public void noCustomersInCustomerList(JFrame frame);
	
	public String inputPane(JFrame frame, String message);
	
	public int confirmDialog();
	
	public void adminNoCustomerAccounts(JFrame frame);
	
	public void adminNoCurrentAccounts(JFrame frame);
	
	public void customerNoAccount(JFrame frame);
	
	public void lockedATMCard(JFrame frame);
	
	public void successfulPinEntry(JFrame frame);
	
	public void incorrectPinEntry(JFrame frame, int count);
	
	public void enterNumericalValue(JFrame frame);
	
	public void lodgementNotification(JFrame frame, String message);
	
	public void balanceNotification(JFrame frame, String message, String type);
	
	public void withdrawalNotification(JFrame frame, String message);
	
	public void maxWithdrawalNotification(JFrame frame);
	
	public void insufficientFundsNotification(JFrame frame);
	
	public int incorrectPassword();

}
