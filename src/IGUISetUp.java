import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public interface IGUISetUp {

	public JFrame setUpFrame(String name);
	
	public JButton createUserMenuButton(String name);
	
	public Container setUpMenuStartContainer(JFrame frame, JPanel userType, JPanel continuePanel);
	
	public Container setUpAdminContainer(JFrame frame, JLabel label, JPanel account, JPanel charges, JPanel interest, JPanel editCust, JPanel nav, JPanel summary, JPanel deleteCust, JPanel overdraft, JPanel deleteAcc, JPanel returnPanel);
	
	public JPanel setUpPanel(FlowLayout flowLayout);
	
	public Container setUpSelectedAdminOptionScreen(JFrame frame, JPanel boxPanel, JPanel buttonPanel);
	
	public JTextField limitedCharacterTextField();
	
	public Container setUpAdminNavigateButton(JFrame frame, JPanel gridPanel, JPanel buttonPanel ,JPanel cancelPanel);
	
	public Container setUpAdminNavigateListAllButton(JFrame frame, JPanel textPanel);
	
	public Container setUpCustomerContainer(JFrame frame, JPanel labelPanel, JPanel boxPanel, JPanel buttonPanel);
	
	public Container setUpCustomerAccountContainer(JFrame frame, JLabel label, JPanel statementPanel, JPanel lodgementPanel, JPanel withdrawPanel, JPanel returnPanel);
	
	public Container setUpCustomerStatementContainer(JFrame frame, JPanel textPanel);
	
	public Container setUpCreateNewCustomerContainer(JFrame frame, JPanel infoPanel, JPanel buttonPanel);
	
}
