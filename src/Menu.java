
import java.awt.*;

import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.text.MaskFormatter;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Menu extends JFrame implements GUISetUP, GUIResponses {

	private static ArrayList<Customer> customerList = new ArrayList<Customer>();
	private int position = 0;
	private Customer customer = null;
	private CustomerAccount acc = new CustomerAccount();
	JFrame f, f1;
	JLabel firstNameLabel, surnameLabel, ppsLabel, dobLabel;
	JTextField firstNameTextField, surnameTextField, ppsTextField, dobTextField;
	JLabel customerIDLabel, passwordLabel;
	JTextField customerIDTextField, passwordTextField;
	Container content;
	
	static String ppsFromFile = "";
	static String surnameFromFile = "";
	static String firstNameFromFile = "";
	static String dobFromFile = "";
	static String custIDFromFile = "";
	static String passwordFromFile = "";

	public static void main(String[] args) {
		Menu driver = new Menu();
		readFromFile();		
		driver.menuStart();
	}

	public void menuStart() {

		f = setUpFrame("User Type");
		
		JPanel userTypePanel = new JPanel();
		final ButtonGroup userType = new ButtonGroup();
		JRadioButton radioButton;
		userTypePanel.add(radioButton = new JRadioButton("Existing Customer"));
		radioButton.setActionCommand("Customer");
		userType.add(radioButton);

		userTypePanel.add(radioButton = new JRadioButton("Administrator"));
		radioButton.setActionCommand("Administrator");
		userType.add(radioButton);

		userTypePanel.add(radioButton = new JRadioButton("New Customer"));
		radioButton.setActionCommand("New Customer");
		userType.add(radioButton);

		JPanel continuePanel = new JPanel();
		JButton continueButton = new JButton("Continue");
		continuePanel.add(continueButton);

		content = setUpMenuStartContainer(f, userTypePanel, continuePanel);
		
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String user = userType.getSelection().getActionCommand();

				// if user selects New Customer
				if (user.equals("New Customer")) {
					createNewCustomer();
				}

				// if user selects Admin
				if (user.equals("Administrator")) {
					loginAdmin();
				}
				
				// if user selects CUSTOMER
				if (user.equals("Customer")) {
					loginCustomer();
				}
			}
		});
		f.setVisible(true);
	}

	public void admin() {
		dispose();

		f = setUpFrame("Administrator Menu");
		f.setVisible(true);

		JPanel deleteCustomerPanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton deleteCustomer = createUserMenuButton("Delete Customer");
		deleteCustomerPanel.add(deleteCustomer);

		JPanel deleteAccountPanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton deleteAccount = createUserMenuButton("Delete Account");
		deleteAccountPanel.add(deleteAccount);

		JPanel bankChargesPanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton bankChargesButton = createUserMenuButton("Apply Bank Charges");
		bankChargesPanel.add(bankChargesButton);

		JPanel interestPanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton interestButton = createUserMenuButton("Apply Interest");
		interestPanel.add(interestButton);

		JPanel editCustomerPanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton editCustomerButton = createUserMenuButton("Edit existing Customer");
		editCustomerPanel.add(editCustomerButton);

		JPanel navigatePanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton navigateButton = createUserMenuButton("Navigate Customer Collection");
		navigatePanel.add(navigateButton);

		JPanel summaryPanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton summaryButton = createUserMenuButton("Display Summary Of All Accounts");
		summaryPanel.add(summaryButton);

		JPanel accountPanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton accountButton = createUserMenuButton("Add an Account to a Customer");
		accountPanel.add(accountButton);
		
		JPanel overdraftPanel = setUpPanel(new FlowLayout(FlowLayout.LEFT));
		JButton overdraftButton = createUserMenuButton("Update an Overdraft for a Current Account");
		overdraftPanel.add(overdraftButton);

		JPanel returnPanel = setUpPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton returnButton = new JButton("Exit Admin Menu");
		returnPanel.add(returnButton);

		JLabel label1 = new JLabel("Please select an option");
		
		content = setUpAdminContainer(f, label1, accountPanel, bankChargesPanel, 
				interestPanel, editCustomerPanel, navigatePanel, summaryPanel, deleteCustomerPanel,
				overdraftPanel, deleteAccountPanel, returnPanel);
		
		overdraftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				boolean loop = true;

				boolean found = false;
				
				if (customerList.isEmpty()) {
					
					noCustomersInCustomerList(f);

				} else {
					while(loop) {

						Object customerID = inputPane(f,"Customer ID of Customer You Wish to Apply an Overdraft to:");
						
						customer = searchForCustomer(customerID);
						if(customer != null) {
							found = true;
							loop = false;
						}
						
						if (found == false) {
							int reply = JOptionPane.showConfirmDialog(null, null, "User not found. Try again?",
									JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								loop = true;
							} else if (reply == JOptionPane.NO_OPTION) {
								f.dispose();
								loop = false;

								admin();
							}
						} else {
							f.dispose();
							f = setUpFrame("Administrator Menu");

							f.setVisible(true);
							
							JComboBox<String> box = new JComboBox<String>();
							for (int i = 0; i < customer.getAccounts().size(); i++) {
								if(customer.getAccounts().get(i) instanceof CustomerCurrentAccount) {
									box.addItem(customer.getAccounts().get(i).getNumber());
								}	
							}
							
							box.getSelectedItem();

							JPanel boxPanel = new JPanel();
							boxPanel.add(box);

							JPanel buttonPanel = new JPanel();
							JButton continueButton = new JButton("Apply Overdraft");
							JButton returnButton = new JButton("Return");
							buttonPanel.add(continueButton);
							buttonPanel.add(returnButton);
							content = f.getContentPane();
							content.setLayout(new GridLayout(2, 1));

							content.add(boxPanel);
							content.add(buttonPanel);
							
							if (customer.getAccounts().isEmpty()) {
								JOptionPane.showMessageDialog(f,
										"This customer has no accounts! \n The admin must add acounts to this customer.",
										"Oops!", JOptionPane.INFORMATION_MESSAGE);
								f.dispose();
								admin();
							} else if(box.getItemCount() == 0) {
								JOptionPane.showMessageDialog(f,
										"This customer has no current accounts! \n The admin must add a current acount to this customer.",
										"Oops!", JOptionPane.INFORMATION_MESSAGE);
								f.dispose();
								admin();
							} else {
								
								for (int i = 0; i < customer.getAccounts().size(); i++) {
									if (customer.getAccounts().get(i).getNumber() == box.getSelectedItem()) {
										acc = customer.getAccounts().get(i);
									}
								}
								
								continueButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ae) {
										
										double overdraft = 0.00;
										boolean correctValue = true;

										while (correctValue) {
											String overdraftInput = (String) JOptionPane.showInputDialog(null,
													"Please enter an overdraft amount");
											// Regex to ensure the user inputs a number followed by an optional decimal
											// point and following numbers
											if (!overdraftInput.matches("^[0-9]*\\.?[0-9]*$")) {

												JOptionPane.showMessageDialog(null,
														"Overdraft must contain a number and a decimal point", "Error",
														JOptionPane.OK_OPTION);
											} else {
												overdraft = Double.parseDouble(overdraftInput);
												((CustomerCurrentAccount) acc).setOverdraft(overdraft);
												correctValue = false;
												JOptionPane.showMessageDialog(f, "New Overdraft = " + ((CustomerCurrentAccount) acc).getOverdraft(),
														"Success!", JOptionPane.INFORMATION_MESSAGE);
											}
										}
															
																														
										f.dispose();
										admin();
									}
								});
								
								
								
								returnButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ae) {
										f.dispose();
										menuStart();
									}
								});
							}
							
						}
						
					}
				}
			}
		});

		bankChargesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				boolean loop = true;

				boolean found = false;

				if (customerList.isEmpty()) {
					noCustomersInCustomerList(f);

				} else {
					while (loop) {
						Object customerID = inputPane(f,"Customer ID of Customer You Wish to Apply Charges to:");

						customer = searchForCustomer(customerID);
						if(customer != null) {
							found = true;
							loop = false;
						}

						if (found == false) {
							int reply = JOptionPane.showConfirmDialog(null, null, "User not found. Try again?",
									JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								loop = true;
							} else if (reply == JOptionPane.NO_OPTION) {
								f.dispose();
								loop = false;

								admin();
							}
						} else {
							f.dispose();
							f = setUpFrame("Administrator Menu");
							f.setVisible(true);

							JComboBox<String> box = new JComboBox<String>();
							for (int i = 0; i < customer.getAccounts().size(); i++) {

								box.addItem(customer.getAccounts().get(i).getNumber());
							}

							box.getSelectedItem();

							JPanel boxPanel = new JPanel();
							boxPanel.add(box);

							JPanel buttonPanel = new JPanel();
							JButton continueButton = new JButton("Apply Charge");
							JButton returnButton = new JButton("Return");
							buttonPanel.add(continueButton);
							buttonPanel.add(returnButton);
							content = f.getContentPane();
							content.setLayout(new GridLayout(2, 1));

							content.add(boxPanel);
							content.add(buttonPanel);

							if (customer.getAccounts().isEmpty()) {
								JOptionPane.showMessageDialog(f,
										"This customer has no accounts! \n The admin must add acounts to this customer.",
										"Oops!", JOptionPane.INFORMATION_MESSAGE);
								f.dispose();
								admin();
							} else {

								for (int i = 0; i < customer.getAccounts().size(); i++) {
									if (customer.getAccounts().get(i).getNumber() == box.getSelectedItem()) {
										acc = customer.getAccounts().get(i);
									}
								}

								continueButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ae) {
										String euro = "\u20ac";

										if (acc instanceof CustomerDepositAccount) {

											JOptionPane.showMessageDialog(f,
													"25" + euro + " deposit account fee aplied.", "",
													JOptionPane.INFORMATION_MESSAGE);
											acc.setBalance(acc.getBalance() - 25);
											JOptionPane.showMessageDialog(f, "New balance = " + acc.getBalance(),
													"Success!", JOptionPane.INFORMATION_MESSAGE);
										}

										if (acc instanceof CustomerCurrentAccount) {

											JOptionPane.showMessageDialog(f,
													"15" + euro + " current account fee aplied.", "",
													JOptionPane.INFORMATION_MESSAGE);
											acc.setBalance(acc.getBalance() - 25);
											JOptionPane.showMessageDialog(f, "New balance = " + acc.getBalance(),
													"Success!", JOptionPane.INFORMATION_MESSAGE);
										}

										f.dispose();
										admin();
									}
								});

								returnButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ae) {
										f.dispose();
										menuStart();
									}
								});

							}
						}
					}
				}

			}
		});

		interestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				boolean loop = true;

				boolean found = false;

				if (customerList.isEmpty()) {
					noCustomersInCustomerList(f);

				} else {
					while (loop) {
					
						Object customerID = inputPane(f, "Customer ID of Customer You Wish to Apply Interest to:");

						customer = searchForCustomer(customerID);
						if(customer != null) {
							found = true;
							loop = false;
						}

						if (found == false) {
							int reply = JOptionPane.showConfirmDialog(null, null, "User not found. Try again?",
									JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								loop = true;
							} else if (reply == JOptionPane.NO_OPTION) {
								f.dispose();
								loop = false;

								admin();
							}
						} else {
							f.dispose();
							f = setUpFrame("Administrator Menu");
							f.setVisible(true);

							JComboBox<String> box = new JComboBox<String>();
							for (int i = 0; i < customer.getAccounts().size(); i++) {

								box.addItem(customer.getAccounts().get(i).getNumber());
							}

							box.getSelectedItem();

							JPanel boxPanel = new JPanel();

							JLabel label = new JLabel("Select an account to apply interest to:");
							boxPanel.add(label);
							boxPanel.add(box);
							JPanel buttonPanel = new JPanel();
							JButton continueButton = new JButton("Apply Interest");
							JButton returnButton = new JButton("Return");
							buttonPanel.add(continueButton);
							buttonPanel.add(returnButton);
							content = f.getContentPane();
							content.setLayout(new GridLayout(2, 1));

							content.add(boxPanel);
							content.add(buttonPanel);

							if (customer.getAccounts().isEmpty()) {
								JOptionPane.showMessageDialog(f,
										"This customer has no accounts! \n The admin must add acounts to this customer.",
										"Oops!", JOptionPane.INFORMATION_MESSAGE);
								f.dispose();
								admin();
							} else {

								for (int i = 0; i < customer.getAccounts().size(); i++) {
									if (customer.getAccounts().get(i).getNumber() == box.getSelectedItem()) {
										acc = customer.getAccounts().get(i);
									}
								}

								continueButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ae) {
										String euro = "\u20ac";
										double interest = 0;
										boolean loop = true;

										while (loop) {
											String interestString = JOptionPane.showInputDialog(f,
													"Enter interest percentage you wish to apply: \n NOTE: Please enter a numerical value. (with no percentage sign) \n E.g: If you wish to apply 8% interest, enter '8'");
											
											if (isNumeric(interestString)) {

												interest = Double.parseDouble(interestString);
												loop = false;

												acc.setBalance(
														acc.getBalance() + (acc.getBalance() * (interest / 100)));

												JOptionPane.showMessageDialog(f,
														interest + "% interest applied. \n new balance = "
																+ acc.getBalance() + euro,
														"Success!", JOptionPane.INFORMATION_MESSAGE);
											}

											else {
												JOptionPane.showMessageDialog(f, "You must enter a numerical value!",
														"Oops!", JOptionPane.INFORMATION_MESSAGE);
											}

										}

										f.dispose();
										admin();
									}
								});

								returnButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ae) {
										f.dispose();
										menuStart();
									}
								});

							}
						}
					}
				}

			}
		});

		editCustomerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				boolean loop = true;

				boolean found = false;

				if (customerList.isEmpty()) {
					noCustomersInCustomerList(f);

				} else {

					while (loop) {
						Object customerID = inputPane(f,"Enter Customer ID:");

						customer = searchForCustomer(customerID);
						if(customer != null) {
							found = true;
							loop = false;
						}

						if (found == false) {
							int reply = JOptionPane.showConfirmDialog(null, null, "User not found. Try again?",
									JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								loop = true;
							} else if (reply == JOptionPane.NO_OPTION) {
								f.dispose();
								loop = false;

								admin();
							}
						} else {
							loop = false;
						}

					}

					f.dispose();

					f = setUpFrame("Administrator Menu");

					firstNameLabel = new JLabel("First Name:", SwingConstants.LEFT);
					surnameLabel = new JLabel("Surname:", SwingConstants.LEFT);
					ppsLabel = new JLabel("PPS Number:", SwingConstants.LEFT);
					dobLabel = new JLabel("Date of birth", SwingConstants.LEFT);
					customerIDLabel = new JLabel("CustomerID:", SwingConstants.LEFT);
					passwordLabel = new JLabel("Password:", SwingConstants.LEFT);
					firstNameTextField = new JTextField(20);
					surnameTextField = new JTextField(20);
					ppsTextField = new JTextField(20);
					dobTextField = new JTextField(20);
					customerIDTextField = new JTextField(20);
					passwordTextField = new JTextField(20);

					JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

					JPanel cancelPanel = new JPanel();

					textPanel.add(firstNameLabel);
					textPanel.add(firstNameTextField);
					textPanel.add(surnameLabel);
					textPanel.add(surnameTextField);
					textPanel.add(ppsLabel);
					textPanel.add(ppsTextField);
					textPanel.add(dobLabel);
					textPanel.add(dobTextField);
					textPanel.add(customerIDLabel);
					textPanel.add(customerIDTextField);
					textPanel.add(passwordLabel);
					textPanel.add(passwordTextField);

					firstNameTextField.setText(customer.getFirstName());
					surnameTextField.setText(customer.getSurname());
					ppsTextField.setText(customer.getPPS());
					dobTextField.setText(customer.getDOB());
					customerIDTextField.setText(customer.getCustomerID());
					passwordTextField.setText(customer.getPassword());

					JButton saveButton = new JButton("Save");
					JButton cancelButton = new JButton("Exit");

					cancelPanel.add(cancelButton, BorderLayout.SOUTH);
					cancelPanel.add(saveButton, BorderLayout.SOUTH);

					content = f.getContentPane();
					content.setLayout(new GridLayout(2, 1));
					content.add(textPanel, BorderLayout.NORTH);
					content.add(cancelPanel, BorderLayout.SOUTH);

					f.setContentPane(content);
					f.setSize(340, 350);
					f.setLocation(200, 200);
					f.setVisible(true);
					f.setResizable(false);

					saveButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {

							customer.setFirstName(firstNameTextField.getText());
							customer.setSurname(surnameTextField.getText());
							customer.setPPS(ppsTextField.getText());
							customer.setDOB(dobTextField.getText());
							customer.setCustomerID(customerIDTextField.getText());
							customer.setPassword(passwordTextField.getText());

							JOptionPane.showMessageDialog(null, "Changes Saved.");
						}
					});

					cancelButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							f.dispose();

							admin();
						}
					});
				}
			}
		});

		summaryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				f.dispose();
				
				f = setUpFrame("Summary of Transactions");
				f.setVisible(true);

				JLabel label1 = new JLabel("Summary of all transactions: ");

				JPanel returnPanel = new JPanel();
				JButton returnButton = new JButton("Return");
				returnPanel.add(returnButton);

				JPanel textPanel = new JPanel();

				textPanel.setLayout(new BorderLayout());
				JTextArea textArea = new JTextArea(40, 20);
				textArea.setEditable(false);
				textPanel.add(label1, BorderLayout.NORTH);
				textPanel.add(textArea, BorderLayout.CENTER);
				textPanel.add(returnButton, BorderLayout.SOUTH);

				JScrollPane scrollPane = new JScrollPane(textArea);
				textPanel.add(scrollPane);

				for (int a = 0; a < customerList.size(); a++)// For each customer, for each account, it displays each
																// transaction.
				{
					for (int b = 0; b < customerList.get(a).getAccounts().size(); b++) {
						acc = customerList.get(a).getAccounts().get(b);
						for (int c = 0; c < customerList.get(a).getAccounts().get(b).getTransactionList().size(); c++) {

							textArea.append(acc.getTransactionList().get(c).toString());
							// Int total = acc.getTransactionList().get(c).getAmount(); //I was going to use
							// this to keep a running total but I couldnt get it working.

						}
					}
				}

				textPanel.add(textArea);
				content.removeAll();

				content = f.getContentPane();
				content.setLayout(new GridLayout(1, 1));
				// content.add(label1);
				content.add(textPanel);
				// content.add(returnPanel);

				returnButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						f.dispose();
						admin();
					}
				});
			}
		});

		navigateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				f.dispose();

				if (customerList.isEmpty()) {
					noCustomersInCustomerList(f);
				} else {

					JButton first, previous, next, last, cancel, findByAcc, findBySurname, listAll;
					JPanel gridPanel, buttonPanel, cancelPanel;

					content = getContentPane();

					content.setLayout(new BorderLayout());

					buttonPanel = new JPanel(new GridLayout(3, 3));
				
					gridPanel = new JPanel(new GridLayout(8, 2));
					cancelPanel = new JPanel();

					firstNameLabel = new JLabel("First Name:", SwingConstants.LEFT);
					surnameLabel = new JLabel("Surname:", SwingConstants.LEFT);
					ppsLabel = new JLabel("PPS Number:", SwingConstants.LEFT);
					dobLabel = new JLabel("Date of birth", SwingConstants.LEFT);
					customerIDLabel = new JLabel("CustomerID:", SwingConstants.LEFT);
					passwordLabel = new JLabel("Password:", SwingConstants.LEFT);
					firstNameTextField = new JTextField(20);
					surnameTextField = new JTextField(20);
					ppsTextField = new JTextField(20);
					dobTextField = new JTextField(20);
					customerIDTextField = new JTextField(20);
					passwordTextField = new JTextField(20);

					first = new JButton("First");
					previous = new JButton("Previous");
					next = new JButton("Next");
					last = new JButton("Last");
					cancel = new JButton("Cancel");
					findByAcc = new JButton("Find By Account Number");
					findBySurname = new JButton("Find By Surname");
					listAll = new JButton("List All");

					firstNameTextField.setText(customerList.get(0).getFirstName());
					surnameTextField.setText(customerList.get(0).getSurname());
					ppsTextField.setText(customerList.get(0).getPPS());
					dobTextField.setText(customerList.get(0).getDOB());
					customerIDTextField.setText(customerList.get(0).getCustomerID());
					passwordTextField.setText(customerList.get(0).getPassword());

					firstNameTextField.setEditable(false);
					surnameTextField.setEditable(false);
					ppsTextField.setEditable(false);
					dobTextField.setEditable(false);
					customerIDTextField.setEditable(false);
					passwordTextField.setEditable(false);

					gridPanel.add(firstNameLabel);
					gridPanel.add(firstNameTextField);
					gridPanel.add(surnameLabel);
					gridPanel.add(surnameTextField);
					gridPanel.add(ppsLabel);
					gridPanel.add(ppsTextField);
					gridPanel.add(dobLabel);
					gridPanel.add(dobTextField);
					gridPanel.add(customerIDLabel);
					gridPanel.add(customerIDTextField);
					gridPanel.add(passwordLabel);
					gridPanel.add(passwordTextField);

					buttonPanel.add(first);
					buttonPanel.add(previous);
					buttonPanel.add(next);
					buttonPanel.add(last);
					buttonPanel.add(findByAcc);
					buttonPanel.add(findBySurname);
					buttonPanel.add(listAll);
					
					cancelPanel.add(cancel);

					content.add(gridPanel, BorderLayout.NORTH);
					content.add(buttonPanel, BorderLayout.CENTER);
					content.add(cancelPanel, BorderLayout.AFTER_LAST_LINE);

					first.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							position = 0;
							firstNameTextField.setText(customerList.get(0).getFirstName());
							surnameTextField.setText(customerList.get(0).getSurname());
							ppsTextField.setText(customerList.get(0).getPPS());
							dobTextField.setText(customerList.get(0).getDOB());
							customerIDTextField.setText(customerList.get(0).getCustomerID());
							passwordTextField.setText(customerList.get(0).getPassword());
						}
					});

					previous.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {

							if (position < 1) {
								// don't do anything
							} else {
								position = position - 1;

								firstNameTextField.setText(customerList.get(position).getFirstName());
								surnameTextField.setText(customerList.get(position).getSurname());
								ppsTextField.setText(customerList.get(position).getPPS());
								dobTextField.setText(customerList.get(position).getDOB());
								customerIDTextField.setText(customerList.get(position).getCustomerID());
								passwordTextField.setText(customerList.get(position).getPassword());
							}
						}
					});

					next.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {

							if (position == customerList.size() - 1) {
								// don't do anything
							} else {
								position = position + 1;

								firstNameTextField.setText(customerList.get(position).getFirstName());
								surnameTextField.setText(customerList.get(position).getSurname());
								ppsTextField.setText(customerList.get(position).getPPS());
								dobTextField.setText(customerList.get(position).getDOB());
								customerIDTextField.setText(customerList.get(position).getCustomerID());
								passwordTextField.setText(customerList.get(position).getPassword());
							}

						}
					});

					last.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {

							position = customerList.size() - 1;

							firstNameTextField.setText(customerList.get(position).getFirstName());
							surnameTextField.setText(customerList.get(position).getSurname());
							ppsTextField.setText(customerList.get(position).getPPS());
							dobTextField.setText(customerList.get(position).getDOB());
							customerIDTextField.setText(customerList.get(position).getCustomerID());
							passwordTextField.setText(customerList.get(position).getPassword());
						}
					});
					
					findByAcc.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							String input = (String) JOptionPane.showInputDialog(null,
									"Please enter an account number");	
							
							String fname = "";
							String surname = "";
							String pps = "";
							String dob = "";
							String custID = "";
							String password = "";
							
							for(Customer c: customerList) {
								ArrayList<CustomerAccount> accounts = c.getAccounts();
								for(CustomerAccount ca: accounts) {
									if(input.equalsIgnoreCase(ca.getNumber())) {
										
										fname = c.getFirstName();
										surname = c.getSurname();
										pps = c.getPPS();
										dob = c.getDOB();
										custID = c.getCustomerID();
										password = c.getPassword();
										
									} 
								}
							}
							
							firstNameTextField.setText(fname);
							surnameTextField.setText(surname);
							ppsTextField.setText(pps);
							dobTextField.setText(dob);
							customerIDTextField.setText(custID);
							passwordTextField.setText(password);

						}
					});
					
					
					findBySurname.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							String input = (String) JOptionPane.showInputDialog(null,
									"Please enter a surname");
							
							String fname = "";
							String surname = "";
							String pps = "";
							String dob = "";
							String custID = "";
							String password = "";
							
							for(Customer c : customerList) {
								if(input.equalsIgnoreCase(c.getSurname())) {
									
									fname = c.getFirstName();
									surname = c.getSurname();
									pps = c.getPPS();
									dob = c.getDOB();
									custID = c.getCustomerID();
									password = c.getPassword();
								}
							}
							
							firstNameTextField.setText(fname);
							surnameTextField.setText(surname);
							ppsTextField.setText(pps);
							dobTextField.setText(dob);
							customerIDTextField.setText(custID);
							passwordTextField.setText(password);
						}
					});
					
					listAll.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							f.dispose();
							
							f = setUpFrame("List of Users");
							
							f.setVisible(true);							
							
							String[] columnNames = new String[]{"First Name", "Surname", "PPS", "DOB", "Customer ID", "Password"};
							
							ArrayList<Object[]> displayList = new ArrayList<Object[]>();
							for(int i = 0; i < customerList.size(); i ++) {
								displayList.add(new Object[] {
									
									customerList.get(i).getFirstName(),
									customerList.get(i).getSurname(),
									customerList.get(i).getPPS(),
									customerList.get(i).getDOB(),
									customerList.get(i).getCustomerID(),
									customerList.get(i).getPassword(),
									
								});
							}
							
							
							JTable table = new JTable();
							
							table.setModel(new DefaultTableModel(displayList.toArray(new Object[][] {}), columnNames));

							JButton returnButton = new JButton("Return");
							
							JPanel textPanel = new JPanel();

							textPanel.setLayout(new BorderLayout());
							textPanel.add(table.getTableHeader(), BorderLayout.NORTH);
							textPanel.add(table, BorderLayout.CENTER);
							textPanel.add(returnButton, BorderLayout.SOUTH);
							
							content.removeAll();
							content = f.getContentPane();
							content.add(textPanel);
							
							returnButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ae) {
									f.dispose();
									admin();
								}
							});
						}
					});

					cancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							dispose();
							admin();
						}
					});
					setContentPane(content);
					setSize(500, 400);
					setVisible(true);
				}
			}
		});

		accountButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				f.dispose();

				if (customerList.isEmpty()) {
					noCustomersInCustomerList(f);
				} else {
					boolean loop = true;

					boolean found = false;

					while (loop) {

						Object customerID = inputPane(f, "Customer ID of Customer You Wish to Add an Account to:");

						customer = searchForCustomer(customerID);
						if(customer != null) {
							found = true;
							loop = false;
						}

						if (found == false) {
							int reply = JOptionPane.showConfirmDialog(null, null, "User not found. Try again?",
									JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								loop = true;
							} else if (reply == JOptionPane.NO_OPTION) {
								f.dispose();
								loop = false;

								admin();
							}
						} else {
							loop = false;
							// a combo box in an dialog box that asks the admin what type of account they
							// wish to create (deposit/current)
							String[] choices = { "Current Account", "Deposit Account" };
							String account = (String) JOptionPane.showInputDialog(null, "Please choose account type",
									"Account Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[1]);

							if (account.equals("Current Account")) {
								// create current account
								boolean valid = true;
								double balance = 0;
								String number = String.valueOf("C" + (customerList.indexOf(customer) + 1) * 10
										+ (customer.getAccounts().size() + 1));// this simple algorithm generates the
																				// account number
								ArrayList<AccountTransaction> transactionList = new ArrayList<AccountTransaction>();
								int randomPIN = (int) (Math.random() * 9000) + 1000;
								String pin = String.valueOf(randomPIN);

								ATMCard atm = new ATMCard(randomPIN, valid);

								double overdraft = 0.00;
								boolean correctValue = true;
								//Admin setting the overdraft for a current account
								while (correctValue) {
									String overdraftInput = (String) JOptionPane.showInputDialog(null,
											"Please enter an overdraft amount");
									// Regex to ensure the user inputs a number followed by an optional decimal
									// point and following numbers
									if (!overdraftInput.matches("^[0-9]*\\.?[0-9]*$")) {

										JOptionPane.showMessageDialog(null,
												"Overdraft must contain a number and a decimal point", "Error",
												JOptionPane.OK_OPTION);
									} else {
										overdraft = Double.parseDouble(overdraftInput);
										correctValue = false;
									}
								}

								CustomerCurrentAccount current = new CustomerCurrentAccount(atm, overdraft, number,
										balance, transactionList);

								customer.getAccounts().add(current);
								JOptionPane.showMessageDialog(f,
										"Account number = " + number + "\n PIN = " + pin + "\n Balance = €" + balance
												+ "\n Overdraft = €" + overdraft,
										"Account created.", JOptionPane.INFORMATION_MESSAGE);

								f.dispose();
								admin();
							}

							if (account.equals("Deposit Account")) {
								// create deposit account

								double balance = 0, interest = 0;
								String number = String.valueOf("D" + (customerList.indexOf(customer) + 1) * 10
										+ (customer.getAccounts().size() + 1));// this simple algorithm generates the
																				// account number
								ArrayList<AccountTransaction> transactionList = new ArrayList<AccountTransaction>();
								
								CustomerDepositAccount deposit = new CustomerDepositAccount(interest, number, balance,
										transactionList);

								customer.getAccounts().add(deposit);
								JOptionPane.showMessageDialog(f, "Account number = " + number + "\nCurrent Interest Rate = " + interest, "Account created.",
										JOptionPane.INFORMATION_MESSAGE);

								f.dispose();
								admin();
							}

						}
					}
				}
			}
		});

		deleteCustomer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				boolean found = true, loop = true;

				if (customerList.isEmpty()) {
					noCustomersInCustomerList(f);
				} else {
					{
						Object customerID = inputPane(f, "Customer ID of Customer You Wish to Delete:");

						customer = searchForCustomer(customerID);
						if(customer != null) {
							found = true;
							loop = false;
						}

						if (found == false) {
							int reply = JOptionPane.showConfirmDialog(null, null, "User not found. Try again?",
									JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								loop = true;
							} else if (reply == JOptionPane.NO_OPTION) {
								f.dispose();
								loop = false;

								admin();
							}
						} else {
							if (customer.getAccounts().size() > 0) {
								JOptionPane.showMessageDialog(f,
										"This customer has accounts. \n You must delete a customer's accounts before deleting a customer ",
										"Oops!", JOptionPane.INFORMATION_MESSAGE);
							} else {
								customerList.remove(customer);
								JOptionPane.showMessageDialog(f, "Customer Deleted ", "Success.",
										JOptionPane.INFORMATION_MESSAGE);
							}
						}

					}
				}
			}
		});

		deleteAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				boolean found = true, loop = true;

				{					
					Object customerID = inputPane(f, "Customer ID of Customer from which you wish to delete an account");

					customer = searchForCustomer(customerID);
					if(customer != null) {
						found = true;
						loop = false;
					}

					if (found == false) {
						int reply = JOptionPane.showConfirmDialog(null, null, "User not found. Try again?",
								JOptionPane.YES_NO_OPTION);
						if (reply == JOptionPane.YES_OPTION) {
							loop = true;
						} else if (reply == JOptionPane.NO_OPTION) {
							f.dispose();
							loop = false;

							admin();
						}
					} else {
						
					}

				}
			}

		});
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				f.dispose();
				menuStart();
			}
		});
	}

	public void customer(Customer e1) {
		
		f = setUpFrame("Customer Menu");
		
		f.setVisible(true);

		if (e1.getAccounts().size() == 0) {
			JOptionPane.showMessageDialog(f,
					"This customer does not have any accounts yet. \n An admin must create an account for this customer \n for them to be able to use customer functionality. ",
					"Oops!", JOptionPane.INFORMATION_MESSAGE);
			f.dispose();
			menuStart();
		} else {
			JPanel buttonPanel = new JPanel();
			JPanel boxPanel = new JPanel();
			JPanel labelPanel = new JPanel();

			JLabel label = new JLabel("Select Account:");
			labelPanel.add(label);

			JButton returnButton = new JButton("Return");
			buttonPanel.add(returnButton);
			JButton continueButton = new JButton("Continue");
			buttonPanel.add(continueButton);

			JComboBox<String> box = new JComboBox<String>();
			for (int i = 0; i < e1.getAccounts().size(); i++) {
				box.addItem(e1.getAccounts().get(i).getNumber());
			}

			for (int i = 0; i < e1.getAccounts().size(); i++) {
				if (e1.getAccounts().get(i).getNumber() == box.getSelectedItem()) {
					acc = e1.getAccounts().get(i);
				}
			}

			boxPanel.add(box);
			content = f.getContentPane();
			content.setLayout(new GridLayout(3, 1));
			content.add(labelPanel);
			content.add(boxPanel);
			content.add(buttonPanel);

			returnButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					f.dispose();
					menuStart();
				}
			});

			continueButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {

					f.dispose();
					
					f = setUpFrame("Customer Menu");
					
					f.setVisible(true);

					JPanel statementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					JButton statementButton = createUserMenuButton("Display Bank Statement");
					statementPanel.add(statementButton);

					JPanel lodgementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					JButton lodgementButton = createUserMenuButton("Lodge money into account");
					lodgementPanel.add(lodgementButton);

					JPanel withdrawalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					JButton withdrawButton = createUserMenuButton("Withdraw money from account");
					withdrawalPanel.add(withdrawButton);

					JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
					JButton returnButton = new JButton("Exit Customer Menu");
					returnPanel.add(returnButton);

					JLabel label1 = new JLabel("Please select an option");
					
					content = f.getContentPane();
					content.setLayout(new GridLayout(5, 1));
					content.add(label1);
					content.add(statementPanel);
					content.add(lodgementPanel);
					content.add(withdrawalPanel);
					content.add(returnPanel);
					
					statementButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							f.dispose();
							f = setUpFrame("Customer Menu");
							
							f.setVisible(true);

							JLabel label1 = new JLabel("Summary of account transactions: ");

							JPanel returnPanel = new JPanel();
							JButton returnButton = new JButton("Return");
							returnPanel.add(returnButton);

							JPanel textPanel = new JPanel();

							textPanel.setLayout(new BorderLayout());
							JTextArea textArea = new JTextArea(40, 20);
							textArea.setEditable(false);
							textPanel.add(label1, BorderLayout.NORTH);
							textPanel.add(textArea, BorderLayout.CENTER);
							textPanel.add(returnButton, BorderLayout.SOUTH);

							JScrollPane scrollPane = new JScrollPane(textArea);
							textPanel.add(scrollPane);

							for (int i = 0; i < acc.getTransactionList().size(); i++) {
								textArea.append(acc.getTransactionList().get(i).toString());

							}

							textPanel.add(textArea);
							content.removeAll();

							content = f.getContentPane();
							content.setLayout(new GridLayout(1, 1));
							content.add(textPanel);

							returnButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ae) {
									f.dispose();
									customer(e1);
								}
							});
						}
					});

					lodgementButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							boolean loop = true;
							boolean on = true;
							double balance = 0;

							if (acc instanceof CustomerCurrentAccount) {
								int count = 3;
								int checkPin = ((CustomerCurrentAccount) acc).getAtm().getPin();
								loop = true;

								while (loop) {
									if (count == 0) {
										JOptionPane.showMessageDialog(f,
												"Pin entered incorrectly 3 times. ATM card locked.", "Pin",
												JOptionPane.INFORMATION_MESSAGE);
										((CustomerCurrentAccount) acc).getAtm().setValid(false);
										customer(e1);
										loop = false;
										on = false;
									}

									String Pin = JOptionPane.showInputDialog(f, "Enter 4 digit PIN;");
									int i = Integer.parseInt(Pin);

									if (on) {
										if (checkPin == i) {
											loop = false;
											JOptionPane.showMessageDialog(f, "Pin entry successful", "Pin",
													JOptionPane.INFORMATION_MESSAGE);

										} else {
											count--;
											JOptionPane.showMessageDialog(f,
													"Incorrect pin. " + count + " attempts remaining.", "Pin",
													JOptionPane.INFORMATION_MESSAGE);
										}

									}
								}

							}
							if (on == true) {
								String balanceTest = JOptionPane.showInputDialog(f, "Enter amount you wish to lodge:");// the
																														// isNumeric
																														// method
																														// tests
																														// to
																														// see
																														// if
																														// the
																														// string
																														// entered
																														// was
																														// numeric.
								if (isNumeric(balanceTest)) {

									balance = Double.parseDouble(balanceTest);
									loop = false;

								} else {
									JOptionPane.showMessageDialog(f, "You must enter a numerical value!", "Oops!",
											JOptionPane.INFORMATION_MESSAGE);
								}

								String euro = "\u20ac";
								acc.setBalance(acc.getBalance() + balance);
								// String date = new
								// SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
								Date date = new Date();
								String date2 = date.toString();
								String type = "Lodgement";
								double amount = balance;

								AccountTransaction transaction = new AccountTransaction(date2, type, amount);
								acc.getTransactionList().add(transaction);

								JOptionPane.showMessageDialog(f, balance + euro + " added do you account!", "Lodgement",
										JOptionPane.INFORMATION_MESSAGE);
								JOptionPane.showMessageDialog(f, "New balance = " + acc.getBalance() + euro,
										"Lodgement", JOptionPane.INFORMATION_MESSAGE);
							}

						}
					});

					withdrawButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							boolean loop = true;
							boolean on = true;
							double withdraw = 0;

							if (acc instanceof CustomerCurrentAccount) {
								int count = 3;
								int checkPin = ((CustomerCurrentAccount) acc).getAtm().getPin();
								loop = true;

								while (loop) {
									if (count == 0) {
										JOptionPane.showMessageDialog(f,
												"Pin entered incorrectly 3 times. ATM card locked.", "Pin",
												JOptionPane.INFORMATION_MESSAGE);
										((CustomerCurrentAccount) acc).getAtm().setValid(false);
										customer(e1);
										loop = false;
										on = false;
									}

									String Pin = JOptionPane.showInputDialog(f, "Enter 4 digit PIN;");
									int i = Integer.parseInt(Pin);

									if (on) {
										if (checkPin == i) {
											loop = false;
											JOptionPane.showMessageDialog(f, "Pin entry successful", "Pin",
													JOptionPane.INFORMATION_MESSAGE);

										} else {
											count--;
											JOptionPane.showMessageDialog(f,
													"Incorrect pin. " + count + " attempts remaining.", "Pin",
													JOptionPane.INFORMATION_MESSAGE);

										}

									}
								}

							}
							if (on == true) {
								String balanceTest = JOptionPane.showInputDialog(f,
										"Enter amount you wish to withdraw (max 500):");// the isNumeric method tests to
																						// see if the string entered was
																						// numeric.
								if (isNumeric(balanceTest)) {

									withdraw = Double.parseDouble(balanceTest);
									loop = false;

								} else {
									JOptionPane.showMessageDialog(f, "You must enter a numerical value!", "Oops!",
											JOptionPane.INFORMATION_MESSAGE);
								}
								if (withdraw > 500) {
									JOptionPane.showMessageDialog(f, "500 is the maximum you can withdraw at a time.",
											"Oops!", JOptionPane.INFORMATION_MESSAGE);
									withdraw = 0;
								}
								
								if(acc instanceof CustomerCurrentAccount) {
									if(withdraw > acc.getBalance() + ((CustomerCurrentAccount) acc).getOverdraft()) {
										JOptionPane.showMessageDialog(f, "Insufficient funds. You have exceeded your overdraft of " 
									+ ((CustomerCurrentAccount) acc).getOverdraft(),"Oops!",JOptionPane.INFORMATION_MESSAGE);
										withdraw = 0;
									}

								} else {
									if (withdraw > acc.getBalance()) {
										JOptionPane.showMessageDialog(f, "Insufficient funds.", "Oops!",
												JOptionPane.INFORMATION_MESSAGE);
										withdraw = 0;
									}
								}


								String euro = "\u20ac";
								if(acc instanceof CustomerCurrentAccount) {
									if(acc.getBalance() == 0) {
										((CustomerCurrentAccount) acc).setOverdraft(((CustomerCurrentAccount) acc).getOverdraft() - withdraw);
										JOptionPane.showMessageDialog(f, "New balance = " + acc.getBalance() + euro + "\n New Overdraft = " + ((CustomerCurrentAccount) acc).getOverdraft() , "Withdraw",
												JOptionPane.INFORMATION_MESSAGE);
									} else {
										acc.setBalance(acc.getBalance() - withdraw);
									}
								} else {
									acc.setBalance(acc.getBalance() - withdraw);
								}
								
								Date date = new Date();
								String date2 = date.toString();

								String type = "Withdraw";
								double amount = withdraw;

								AccountTransaction transaction = new AccountTransaction(date2, type, amount);
								acc.getTransactionList().add(transaction);

								JOptionPane.showMessageDialog(f, withdraw + euro + " withdrawn.", "Withdraw",
										JOptionPane.INFORMATION_MESSAGE);
								JOptionPane.showMessageDialog(f, "New balance = " + acc.getBalance() + euro, "Withdraw",
										JOptionPane.INFORMATION_MESSAGE);
								
							}

						}
					});

					returnButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							f.dispose();
							menuStart();
						}
					});
				}
			});
		}
	}

	public static boolean isNumeric(String str) // a method that tests if a string is numeric
	{
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	private static void readFromFile() {
		try {
			File myFile = new File("C:/Paddy/CustomerDetails.txt");
			Scanner fileReader = new Scanner(myFile);
			while (fileReader.hasNextLine()) {
				String data = fileReader.nextLine();
				if (data.contains("PPS number")) {
					String[] ppsSplit = data.split("= ");
					ppsFromFile = ppsSplit[1];
					System.out.println("PPS: " + ppsFromFile);
				} else if (data.contains("Surname")) {
					String[] surnameSplit = data.split("= ");
					surnameFromFile = surnameSplit[1];
					System.out.println("Surname: " + surnameFromFile);
				} else if (data.contains("First Name")) {
					String[] firstNameSplit = data.split("= ");
					firstNameFromFile = firstNameSplit[1];
					System.out.println("First: " + firstNameFromFile);
				} else if (data.contains("Date of Birth")) {
					String[] dobSplit = data.split("= ");
					dobFromFile = dobSplit[1];
					System.out.println("Date of Birth: " + dobFromFile);
				} else if (data.contains("Customer ID")) {
					String[] custIdSplit = data.split("= ");
					custIDFromFile = custIdSplit[1];
					System.out.println("Customer ID: " + custIDFromFile);
				} else if (data.contains("Password")) {
					String[] passwordSplit = data.split("= ");
					passwordFromFile = passwordSplit[1];
					System.out.println("Password: " + passwordFromFile);
				}
				
				ArrayList<CustomerAccount> accounts = new ArrayList<CustomerAccount>();
				
				if(!ppsFromFile.isEmpty() && !surnameFromFile.isEmpty() && !firstNameFromFile.isEmpty() && !dobFromFile.isEmpty() && !custIDFromFile.isEmpty() && !passwordFromFile.isEmpty()) {
					Customer cust = new Customer(ppsFromFile, surnameFromFile, firstNameFromFile, dobFromFile, custIDFromFile, passwordFromFile, accounts);
					customerList.add(cust);
					
					ppsFromFile = "";
					surnameFromFile = "";
					firstNameFromFile = "";
					dobFromFile = "";
					custIDFromFile = "";
					passwordFromFile = "";
				} else {
					
				}
				

			}

			fileReader.close();

		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}

	}
	
	private void createNewCustomer() {
		f.dispose();
		f1 = setUpFrame("Create New Customer");
		content = f1.getContentPane();
		content.setLayout(new BorderLayout());

		firstNameLabel = new JLabel("First Name:", SwingConstants.RIGHT);
		surnameLabel = new JLabel("Surname:", SwingConstants.RIGHT);
		ppsLabel = new JLabel("PPS Number:", SwingConstants.RIGHT);
		dobLabel = new JLabel("Date of birth", SwingConstants.RIGHT);
		firstNameTextField = new JTextField(20);
		surnameTextField = new JTextField(20);
		ppsTextField = new JTextField(20);
		dobTextField = new JTextField(20);
		JPanel panel = new JPanel(new GridLayout(6, 2));
		JPanel panel2;
		panel.add(firstNameLabel);
		panel.add(firstNameTextField);
		panel.add(surnameLabel);
		panel.add(surnameTextField);
		panel.add(ppsLabel);
		panel.add(ppsTextField);
		panel.add(dobLabel);
		panel.add(dobTextField);

		panel2 = new JPanel();
		JButton add = new JButton("Add");

		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String PPS = ppsTextField.getText();
				String firstName = firstNameTextField.getText();
				String surname = surnameTextField.getText();
				String DOB = dobTextField.getText();
				String password = "";

				String CustomerID = "ID" + PPS;

				ArrayList<String> ids = new ArrayList<String>();

				f1.dispose();

				boolean loop = true;
				while (loop) {
					password = JOptionPane.showInputDialog(f, "Enter 7 character Password;");

					if (password.length() != 7)// Making sure password is 7 characters
					{
						JOptionPane.showMessageDialog(null, null, "Password must be 7 charatcers long",
								JOptionPane.OK_OPTION);
					} else {
						loop = false;
					}
				}

				try {
					File myFile = new File("C:/Paddy/CustomerInfo.txt");
					Scanner fileReader = new Scanner(myFile);
					while (fileReader.hasNextLine()) {
						String data = fileReader.nextLine();
						ids.add(data);
					}
					fileReader.close();

				} catch (FileNotFoundException exception) {
					exception.printStackTrace();
				}

				if (!ids.contains(CustomerID)) {
					ArrayList<CustomerAccount> accounts = new ArrayList<CustomerAccount>();
					Customer customer = new Customer(PPS, surname, firstName, DOB, CustomerID, password,
							accounts);

					customerList.add(customer);

					try {
						FileWriter myWriter = new FileWriter("C:/Paddy/CustomerInfo.txt", true);
						FileWriter detailsWriter = new FileWriter("C:/Paddy/CustomerDetails.txt", true);

						myWriter.write(customer.getCustomerID() + "\n");
						detailsWriter.write(customer.toString());
						myWriter.close();
						detailsWriter.close();
						System.out.println("Successfully wrote to the file.");

					} catch (IOException exception) {
						exception.printStackTrace();
					}
					JOptionPane.showMessageDialog(f,
							"CustomerID = " + CustomerID + "\n Password = " + password, "Customer created.",
							JOptionPane.INFORMATION_MESSAGE);

					menuStart();
				} else {
					JOptionPane.showMessageDialog(f, "Customer Id already exists", "Error",
							JOptionPane.INFORMATION_MESSAGE);

					menuStart();
				}

			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f1.dispose();
				menuStart();
			}
		});

		panel2.add(add);
		panel2.add(cancel);

		content.add(panel, BorderLayout.CENTER);
		content.add(panel2, BorderLayout.SOUTH);

		f1.setVisible(true);
	}
	
	private void loginAdmin() {
		boolean loop = true, loop2 = true;
		boolean cont = false;
		while (loop) {
			Object adminUsername = JOptionPane.showInputDialog(f, "Enter Administrator Username:");

			if (!adminUsername.equals("admin"))// search admin list for admin with matching admin username
			{
				int reply = JOptionPane.showConfirmDialog(null, null, "Incorrect Username. Try again?",
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					loop = true;
				} else if (reply == JOptionPane.NO_OPTION) {
					f1.dispose();
					loop = false;
					loop2 = false;
					menuStart();
				}
			} else {
				loop = false;
			}
		}

		while (loop2) {
			Object adminPassword = JOptionPane.showInputDialog(f, "Enter Administrator Password;");

			if (!adminPassword.equals("admin11"))// search admin list for admin with matching admin password
			{
				int reply = JOptionPane.showConfirmDialog(null, null, "Incorrect Password. Try again?",
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {

				} else if (reply == JOptionPane.NO_OPTION) {
					f1.dispose();
					loop2 = false;
					menuStart();
				}
			} else {
				loop2 = false;
				cont = true;
			}
		}

		if (cont) {
			f.dispose();// Changed from f1 to prevent a null pointer exception
			loop = false;
			admin();
		}
	}
	
	private void loginCustomer() {
		boolean loop = true, loop2 = true;
		boolean cont = false;
		boolean found = false;
		Customer customer = null;
		while (loop) {
			Object customerID = JOptionPane.showInputDialog(f, "Enter Customer ID:");

			for (Customer aCustomer : customerList) {

				if (aCustomer.getCustomerID().equals(customerID))// search customer list for matching
																	// customer ID
				{
					found = true;
					customer = aCustomer;
				}
			}

			if (found == false) {
				int reply = JOptionPane.showConfirmDialog(null, null, "User not found. Try again?",
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					loop = true;
				} else if (reply == JOptionPane.NO_OPTION) {
					f.dispose();
					loop = false;
					loop2 = false;
					menuStart();
				}
			} else {
				loop = false;
			}

		}

		while (loop2) {
			Object customerPassword = JOptionPane.showInputDialog(f, "Enter Customer Password;");

			if (!customer.getPassword().equals(customerPassword))// check if customer password is correct
			{
				int reply = JOptionPane.showConfirmDialog(null, null, "Incorrect password. Try again?",
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {

				} else if (reply == JOptionPane.NO_OPTION) {
					f.dispose();
					loop2 = false;
					menuStart();
				}
			} else {
				loop2 = false;
				cont = true;
			}
		}

		if (cont) {
			f.dispose();
			loop = false;
			customer(customer);
		}
	}
	
	public Customer searchForCustomer(Object customerID) {
		Customer aCustomer = null;
		
		for (Customer c : customerList) {

			if (c.getCustomerID().equals(customerID)) {
				aCustomer = c;
			}
		}
		return aCustomer;		
	}
	
	@Override
	public JFrame setUpFrame(String name) {
		JFrame frame = new JFrame(name);
		frame.setSize(400, 400);
		frame.setLocation(200, 200);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		return frame;
	}
	
	@Override
	public JButton createUserMenuButton(String name) {
		JButton button = new JButton(name);
		button.setPreferredSize(new Dimension(250, 20));
		return button;
	}

	@Override
	public Container setUpMenuStartContainer(JFrame frame, JPanel userType, JPanel continuePanel) {
		Container content = frame.getContentPane();
		content.setLayout(new GridLayout(2, 1));
		content.add(userType);
		content.add(continuePanel);
		return content;
	}

	@Override
	public Container setUpAdminContainer(JFrame frame, JLabel label, JPanel account, JPanel charges, JPanel interest,
			JPanel editCust, JPanel nav, JPanel summary, JPanel deleteCust, JPanel overdraft, JPanel deleteAcc,
			JPanel returnPanel) {
		Container content = frame.getContentPane();
		content.setLayout(new GridLayout(11, 1));
		content.add(label);
		content.add(account);
		content.add(charges);
		content.add(interest);
		content.add(editCust);
		content.add(nav);
		content.add(summary);
		content.add(deleteCust);
		content.add(overdraft);
		content.add(deleteAcc);
		content.add(returnPanel);
		return content;
	}

	@Override
	public JPanel setUpPanel(FlowLayout flowLayout) {
		JPanel panel = new JPanel(flowLayout);
		return panel;
	}

	@Override
	public void noCustomersInCustomerList(JFrame frame) {
		JOptionPane.showMessageDialog(frame, "There are no customers yet!", "Oops!",
				JOptionPane.INFORMATION_MESSAGE);
		frame.dispose();
		admin();	
	}
	
	@Override
	public String inputPane(JFrame frame, String message) {
		String output = JOptionPane.showInputDialog(frame,message);
		return output;			
	}
	
	
	
	
}
