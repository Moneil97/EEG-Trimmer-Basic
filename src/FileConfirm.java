import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;

public class FileConfirm extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FileConfirm dialog = new FileConfirm();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FileConfirm() {
		setBounds(100, 100, 450, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblTheSelectedFile = new JLabel("The Selected File Contains:");
			lblTheSelectedFile.setFont(new Font("Tahoma", Font.PLAIN, 30));
			contentPanel.add(lblTheSelectedFile, BorderLayout.NORTH);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			panel.setLayout(new GridLayout(0, 1, 0, 0));
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				{
					JLabel label = new JLabel("#");
					label.setFont(new Font("Tahoma", Font.PLAIN, 20));
					panel_1.add(label);
				}
				{
					JLabel lblChannels = new JLabel("Channels");
					lblChannels.setFont(new Font("Tahoma", Font.PLAIN, 20));
					panel_1.add(lblChannels);
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				{
					JLabel label = new JLabel("#");
					label.setFont(new Font("Tahoma", Font.PLAIN, 20));
					panel_1.add(label);
				}
				{
					JLabel lblDataPoints = new JLabel("Data Points");
					lblDataPoints.setFont(new Font("Tahoma", Font.PLAIN, 20));
					panel_1.add(lblDataPoints);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton btnTranspose = new JButton("Transpose");
				buttonPane.add(btnTranspose);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
