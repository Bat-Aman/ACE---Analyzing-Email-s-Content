package ace_gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

//import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
//import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
//import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.mail.*;
import javax.mail.internet.*;


//import com.google.common.base.Strings;
import com.google.common.io.Files;
import javax.swing.JTable;


public class MainWindow extends JFrame {

	private JFrame extractEml;
	private JLabel convertingEmail_label;
	private JProgressBar progressBar;
	private JButton startExtractionBtn;
	private JTextArea textArea;
	private JTable tableView;

	DefaultTableModel dmt;
	
	/**
	 * Function to initialize the Frame contents.
	 */
	
	public void initialize() {
		
		String[] headers = new String[] {
				"To", "From", "Subject", "Attachment's FileName"
		};
		dmt = new DefaultTableModel(headers, 0);
		dmt.setColumnIdentifiers(headers);
		
		extractEml = new JFrame();
		extractEml.setResizable(true);
		extractEml.setTitle("EML Extractor");
		extractEml.getContentPane().setBackground(Color.WHITE);
		extractEml.setBackground(Color.WHITE);
		extractEml.setBounds(100, 100, 700, 703);
		extractEml.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		extractEml.getContentPane().setLayout(null);
		extractEml.setLocationRelativeTo(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 507, 210);
		extractEml.getContentPane().add(scrollPane);
		scrollPane.setBorder(new TitledBorder(null, "EML Files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPane.setBackground(Color.WHITE);
		
		final JList<String> list = new JList<String>();
		scrollPane.setViewportView(list);
		final DefaultListModel<String> listModel = new DefaultListModel<String>();
		list.setModel(listModel);
		list.setBackground(new Color(255, 255, 255));
		list.setBorder(null);
		listModel.addListDataListener(new ListDataListener() {
			public void contentsChanged() {
				convertingEmail_label.setText("Email 0 of " + listModel.getSize());
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				contentsChanged();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				contentsChanged();
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				contentsChanged();
			}
			
		});
		
		final JFileChooser directory_chooser = new JFileChooser();
		directory_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		final JButton addFolderBtn = new JButton("Add Folders");
		addFolderBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (directory_chooser.showOpenDialog(extractEml) == JFileChooser.APPROVE_OPTION) {
					addFolderBtn.setEnabled(false);
					extractEml.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							for(File f : Files.fileTreeTraverser().preOrderTraversal(directory_chooser.getSelectedFile())) {
								if(f.getName().endsWith(".eml")) {
									listModel.addElement(f.getAbsolutePath());
								}
							}
							
							addFolderBtn.setEnabled(true);
							extractEml.setCursor(Cursor.getDefaultCursor());
						
						}
						
					});
				}
			}
			
		});
		
		addFolderBtn.setBounds(527, 63, 140, 34);
		extractEml.getContentPane().add(addFolderBtn);
		
		final JFileChooser file_chooser = new JFileChooser();
		file_chooser.setMultiSelectionEnabled(true);
		
		JButton addFilesBtn = new JButton("Add File(s)");
		addFilesBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(file_chooser.showOpenDialog(extractEml) == JFileChooser.APPROVE_OPTION) {
					for(File f : file_chooser.getSelectedFiles()) {
						listModel.addElement(f.getAbsolutePath());
					}
				}
				
			}
			
		});
		
		addFilesBtn.setBounds(527, 18, 140, 34);
		extractEml.getContentPane().add(addFilesBtn);
		
		JButton clearListBtn = new JButton("Clear List");
		clearListBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				listModel.clear();
			}
		});
		
		clearListBtn.setBounds(527, 153, 140, 34);
		extractEml.getContentPane().add(clearListBtn);
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		optionsPanel.setBackground(Color.WHITE);
		optionsPanel.setBounds(10, 232, 664, 55);
		extractEml.getContentPane().add(optionsPanel);
		optionsPanel.setLayout(null);
		
		final JCheckBox extractAttachments = new JCheckBox("Extract Attachments");
		extractAttachments.setBackground(Color.WHITE);
		extractAttachments.setBounds(6, 21, 180, 23);
		optionsPanel.add(extractAttachments);
		
		JPanel progressPanel = new JPanel();
		progressPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		progressPanel.setBackground(Color.WHITE);
		progressPanel.setBounds(10, 298, 664, 127);
		extractEml.getContentPane().add(progressPanel);
		progressPanel.setLayout(null);
		
		startExtractionBtn = new JButton("Extract");
		startExtractionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startExtractionBtn.setEnabled(false);
				extractEml.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							startExtraction(Collections.list(listModel.elements()), extractAttachments.isSelected());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}).start();	
			}
		});
		
		startExtractionBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
		startExtractionBtn.setBounds(117, 75, 425, 41);
		progressPanel.add(startExtractionBtn);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 32, 644, 32);
		progressPanel.add(progressBar);
		
		convertingEmail_label = new JLabel("Email 0 of 0");
		convertingEmail_label.setBounds(12, 8, 400, 14);
		progressPanel.add(convertingEmail_label);
		
		JButton removeSelected = new JButton("Remove Selected");
		removeSelected.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for(String s : list.getSelectedValuesList()) {
					listModel.removeElement(s);
				}
			}
		});
		
		removeSelected.setBounds(527, 108, 140, 34);
		extractEml.getContentPane().add(removeSelected);
		
		tableView = new JTable();
		JScrollPane logPane = new JScrollPane(tableView);
		logPane.setBounds(3, 436, 671, 227);
		logPane.setBorder(new TitledBorder(null, "Extracted Contents", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		logPane.setBackground(Color.WHITE);
		extractEml.getContentPane().add(logPane);
		
		logPane.setViewportView(tableView);
		
		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener( ) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String csvFilepath = "";				
				JFileChooser jf = new JFileChooser();
				int x = jf.showSaveDialog(null);
				if(x == JFileChooser.APPROVE_OPTION) {
					csvFilepath = jf.getSelectedFile().getAbsolutePath();
				}
				//System.out.println(csvFilepath);
				exportToCSV(tableView, csvFilepath);
			}
			
		});
		
		saveBtn.setBounds(527, 198, 140, 34);
		extractEml.getContentPane().add(saveBtn);
	}
	
	/**
	 * Function to extract Email's Structured content using JavaMail.
	 * 
	 * @param enumeration
	 */
	
	private void startExtraction(List<String> l, boolean extractAttachments) throws Exception {
		try {
			ArrayList<String> params = new ArrayList<String>();
			int listSize = l.size();
			
			if (extractAttachments) {
				params.add("--extract-attachments");
			}
			
			for(String temp: l) {
				extract(temp, listSize);
			}
			
		} finally {
			startExtractionBtn.setEnabled(true);
			extractEml.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	private void extract(String filePath, int listSize) throws Exception {
		try {
			List<String> fromEmailID = new ArrayList<String>();
			List<String> toEmailID = new ArrayList<String>();
			List<String> attachedFiles = new ArrayList<String>();
			String subject;
			
			String new_filePath = filePath.replace("\\", "\\\\");
			System.out.println(new_filePath);
			File emlFile = new File(new_filePath);
			Properties props = System.getProperties();
			props.put("mail host", "smtp.dummyDomain.com");
			props.put("mail.transport.protocol", "smtp");
			
			Session mailSession = Session.getDefaultInstance(props, null);
			InputStream source = new FileInputStream(emlFile);
			MimeMessage message = new MimeMessage(mailSession, source);
			
			String contentType = message.getContentType();
			if(contentType.contains("multipart")) {
				System.out.println("Attachment Found");
				
				Multipart multipart = (Multipart) message.getContent();
				int numberOfParts = multipart.getCount();
				
				for(int i = 0; i < numberOfParts; i++) {
					MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
					if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
						String attachmentFileName = part.getFileName();
						attachedFiles.add(attachmentFileName);
					}
				}
			}
	
			Address[] address;
			
			// From
			if((address = message.getFrom()) != null) {
				for(int i = 0; i <address.length; i++) {
					fromEmailID.add(address[i].toString());
				}
			}
			
			//To
			if((address = message.getRecipients(Message.RecipientType.TO)) != null) {
				for(int i = 0; i < address.length; i++) {
					toEmailID.add(address[i].toString());
				}
			}
			
			//Subject
			if(message.getSubject() != null) {
				subject = message.getSubject();
			} else {
				subject ="";
			}
						
			for(int i = 0; i < listSize; i++) {
				final String progressBarText = "Email " + (i + 1) + " of " + listSize;
				final int percent = (int) Math.ceil(((i + 1d) * 100d) / listSize);
				
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						convertingEmail_label.setText(progressBarText);
						progressBar.setValue(percent);	
					}
					
				});
			}
			viewTable(toEmailID, fromEmailID, subject, attachedFiles);
			
		} catch (Exception e) {
			e.printStackTrace();

		}
		
	}
	
	private void viewTable(List<String> toEmail, List<String> fromEmail, String subject, List<String> attachedFiles) {
		
		Object[] data = new Object[] {
				toEmail, fromEmail, subject, attachedFiles
		};
		
		dmt.addRow(data);
		tableView.setModel(dmt);
	}
	
	private void exportToCSV(JTable tableData, String csvFilepath) {
		try {
			TableModel model = tableData.getModel();
			FileWriter csv = new FileWriter(new File(csvFilepath));
			
			for(int i = 0; i < model.getColumnCount(); i++) {
				csv.write(model.getColumnName(i) + ",");
			}
			
			csv.write("\n");
			
			for(int i = 0; i < model.getRowCount(); i++) {
				for(int j = 0; j < model.getColumnCount(); j++) {
					csv.write(model.getValueAt(i, j).toString().replaceAll(",", " | ") + ",");
				}
				csv.write("\n");
			
			}
			csv.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Create the Application.
	 */
	
	public MainWindow() {
		initialize();
	}
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.extractEml.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
