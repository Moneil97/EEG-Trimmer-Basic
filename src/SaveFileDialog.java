import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SaveFileDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public SaveFileDialog(JFrame parent, Data data, DraggableLine left, DraggableLine right) {
		super(parent, Dialog.ModalityType.APPLICATION_MODAL);
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

		JButton btnRxc = new JButton(data.channels + "x(" + (left.enabled? left.getXReal():0) + " - " + (right.enabled? right.getXReal():data.dataPoints) + ")");
		btnRxc.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnRxc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveFileRC(data, left, right);
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Succesful", "Save", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Failed", "Save", JOptionPane.ERROR_MESSAGE);
					//e1.printStackTrace();
				}
				SaveFileDialog.this.dispatchEvent(new WindowEvent(SaveFileDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonPane.add(btnRxc);
	
	
		JButton btnCxr = new JButton("(" + (left.enabled? left.getXReal():0) + " - " + (right.enabled? right.getXReal():data.dataPoints) + ")x" + data.channels);
		btnCxr.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnCxr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveFileCR(data, left, right);
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Succesful", "Save", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Failed", "Save", JOptionPane.ERROR_MESSAGE);
					//e1.printStackTrace();
				}
				SaveFileDialog.this.dispatchEvent(new WindowEvent(SaveFileDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonPane.add(btnCxr);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SaveFileDialog.this.dispatchEvent(new WindowEvent(SaveFileDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
	
	private void saveFileRC(Data data, DraggableLine left, DraggableLine right) throws Exception {
		JFileChooser fc = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("csv", "csv");
		fc.setFileFilter(filter);
		fc.showSaveDialog(this);
		FileWriter fw = new FileWriter(fc.getSelectedFile());
		
		for (int i = 0; i < data.channels; i++) {
			String s = "";
			
			double[] d = data.matrix.get(i);
			for (int j = (left.enabled? left.getXReal():0); j < (right.enabled? right.getXReal():data.dataPoints); j++) {
				s+= d[j];
				if (j != data.dataPoints-1) s+= ",";
			}
			
			fw.write(s + "\n");
		}
		fw.close();
	}
	
	private void saveFileCR(Data data, DraggableLine left, DraggableLine right) throws Exception {
		JFileChooser fc = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("csv", "csv");
		fc.setFileFilter(filter);
		fc.showSaveDialog(this);
		FileWriter fw = new FileWriter(fc.getSelectedFile());
		
		for (int j = (left.enabled? left.getXReal():0); j < (right.enabled? right.getXReal():data.dataPoints); j++) {
			String s = "";
			
			for (int i = 0; i < data.channels; i++) {
				s+= data.matrix.get(i)[j];
				if (i != data.channels-1) s+= ",";
			}
			
			fw.write(s + "\n");
		}
		fw.close();
	}

}
