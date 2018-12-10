import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import ru.mipt.edf.EDFHeader;
import ru.mipt.edf.EDFSignal;
import ru.mipt.edf.EDFWriter;

public class SaveFileDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	public SaveFileDialog(JFrame parent, Data data, DraggableLine left, DraggableLine right) {
		super(parent, Dialog.ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 600, 157);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		int leftTrim = (left.enabled? left.getXReal():0);
		int rightTrim = (right.enabled? right.getXReal():data.dataPoints);
		
		JLabel lblRowsXCols = new JLabel("The new file will contain: " + (rightTrim-leftTrim) + " (" + (left.enabled? left.getXReal():0) + " - " + (right.enabled? right.getXReal():data.dataPoints) + ")" + " samples");
		lblRowsXCols.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPanel.add(lblRowsXCols);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	
		JButton okButton = new JButton("OK");
		okButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveFile(data, left, right);
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Succesful", "Save", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(SaveFileDialog.this, "Save Failed", "Save", JOptionPane.ERROR_MESSAGE);
				}
				SaveFileDialog.this.dispatchEvent(new WindowEvent(SaveFileDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonPane.add(okButton);
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

	private void saveFile(Data data, DraggableLine left, DraggableLine right) throws IOException {
		
		JFileChooser fc = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("edf", "edf");
		fc.setFileFilter(filter);
		fc.showSaveDialog(this);
		
		EDFSignal signal = data.result.getSignal();
		EDFHeader header = data.result.getHeader();
		
		int leftTrim = (left.enabled? left.getXReal():0);
		int rightTrim = (right.enabled? right.getXReal():data.dataPoints);
		
		if ((rightTrim-leftTrim) % header.getNumberOfSamples()[0] != 0) {
			JOptionPane.showMessageDialog(SaveFileDialog.this, "Must be a multiple of " + header.getNumberOfSamples()[0], "Save", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		//Fix header
		header.numberOfRecords = (rightTrim-leftTrim)/header.getNumberOfSamples()[0];
		
		//Trim the signal
		short[][] vals = signal.getDigitalValues();
		
		for (int i = 0; i < vals.length; i++)
			vals[i] = Arrays.copyOfRange(vals[i], leftTrim, rightTrim);
		
		FileOutputStream fout = new FileOutputStream(fc.getSelectedFile());
		EDFWriter.writeIntoOutputStream(header, fout);
		EDFWriter.writeIntoOutputStream(signal, header, fout);
		fout.close();
	}
	
}
