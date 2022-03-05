import javax.swing.JFrame;

public interface IMenuNavigation {

	public void navigateToAdmin(JFrame frame);
	
	public void navigateToMainMenu(JFrame frame);
	
	public void navigateToCustomerMenu(JFrame frame, Customer cust);
}
