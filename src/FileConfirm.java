import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FileConfirm extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	String selection;
	private JLabel dataPointsLabel, channelsLabel;

	public FileConfirm(JFrame parent, Data data) {
		super(parent, Dialog.ModalityType.APPLICATION_MODAL);
		
		JFileChooser fc = new JFileChooser(".");
		fc.showOpenDialog(this);
		File f = fc.getSelectedFile();
		if (!f.exists()) {
			return;
		}
		
		data.loadData(f);
		setBounds(100, 100, 450, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		setLocationRelativeTo(parent);
		
		JLabel lblTheSelectedFile = new JLabel("The Selected File Contains:");
		lblTheSelectedFile.setFont(new Font("Tahoma", Font.PLAIN, 30));
		contentPanel.add(lblTheSelectedFile, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPanel.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		channelsLabel = new JLabel(data.channels + "");
		channelsLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_1.add(channelsLabel);
		
		JLabel lblChannels = new JLabel("Channels");
		lblChannels.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_1.add(lblChannels);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		dataPointsLabel = new JLabel(data.dataPoints + "");
		dataPointsLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_2.add(dataPointsLabel);
		
		JLabel lblDataPoints = new JLabel("Data Points");
		lblDataPoints.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_2.add(lblDataPoints);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selection = "OK";
				FileConfirm.this.dispatchEvent(new WindowEvent(FileConfirm.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	
		JButton btnTranspose = new JButton("Transpose");
		btnTranspose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				data.transpose();
				dataPointsLabel.setText(data.dataPoints + "");
				channelsLabel.setText(data.channels + "");
			}
		});
		buttonPane.add(btnTranspose);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selection = "Cancel";
				data.matrix = null;
				FileConfirm.this.dispatchEvent(new WindowEvent(FileConfirm.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonPane.add(cancelButton);
	}
}
