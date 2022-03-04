import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public interface GUISetUP {

	public JFrame setUpFrame(String name);
	
	public JButton createUserMenuButton(String name);
	
	public Container setUpMenuStartContainer(JFrame frame, JPanel userType, JPanel continuePanel);
	
	public Container setUpAdminContainer(JFrame frame, JLabel label, JPanel account, JPanel charges, JPanel interest, JPanel editCust, JPanel nav, JPanel summary, JPanel deleteCust, JPanel overdraft, JPanel deleteAcc, JPanel returnPanel);
	
	public JPanel setUpPanel(FlowLayout flowLayout);
}
