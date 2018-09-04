import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

public class SaveFileDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			SaveFileDialog dialog = new SaveFileDialog(null, null, null);
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public SaveFileDialog(JFrame parent, Data data, DraggableLine left, DraggableLine right) {
		//super(parent, Dialog.ModalityType.APPLICATION_MODAL);
		super(parent);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 157);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblRowsXCols = new JLabel("Rows x Cols");
		lblRowsXCols.setFont(new Font("Tahoma", Font.PLAIN, 30));
		contentPanel.add(lblRowsXCols);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		
		JButton btnRxc = new JButton(data.channels + "x" + data.dataPoints);
		btnRxc.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnRxc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveFile(data);
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Succesful", "Save", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Failed", "Save", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				SaveFileDialog.this.dispatchEvent(new WindowEvent(SaveFileDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonPane.add(btnRxc);
	
	
		JButton btnCxr = new JButton(data.dataPoints + "x" + data.channels);
		btnCxr.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnCxr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					data.transpose();
					saveFile(data);
					data.transpose();
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Succesful", "Save", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Failed", "Save", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				SaveFileDialog.this.dispatchEvent(new WindowEvent(SaveFileDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonPane.add(btnCxr);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
	
	private void saveFile(Data data) throws IOException {
		FileWriter fw = new FileWriter(new File("new_eeg.csv"));
		
		for (int i = 0; i < data.channels; i++) {
			String s = "";
			
			double[] d = data.matrix.get(i);
			for (int j = 0; j < data.dataPoints; j++) {
				s+= d[j];
				if (j != data.dataPoints-1) s+= ",";
			}
			
			fw.write(s + "\n");
		}
		
		fw.close();
	}

}
