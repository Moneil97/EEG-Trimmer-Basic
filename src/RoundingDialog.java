import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RoundingDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;
	boolean badExit = false;

	public RoundingDialog(JFrame parent, Data data, DraggableLine left, DraggableLine right, int freq) {
		super(parent, Dialog.ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		setLayout(new GridLayout(0, 1));
		setLocationRelativeTo(null);
		
		Font tah20 = new Font("Tahoma", Font.PLAIN, 20);
		
		int leftTrim = (left.enabled? left.getXReal():0);
		int rightTrim = (right.enabled? right.getXReal():data.dataPoints);
		
		int numSelected = rightTrim-leftTrim;
		int toRemove =  numSelected % freq;
		int toAdd = freq - toRemove;
		
		JLabel text2 = new JLabel(" You selected sample " + leftTrim + " through " + rightTrim + 
				" (" + Math.round((float)numSelected/freq*100.0)/100.0 + " seconds)");
		text2.setFont(tah20);
		this.add(text2);
		
		JLabel text = new JLabel(" The number of samples must be a multiple of: " + freq + " (1 second)");
		text.setFont(tah20);
		this.add(text);
		
		JLabel rndUp = new JLabel(" Round samples up to " + (numSelected+toAdd)/freq + " seconds");
		rndUp.setFont(tah20);
		this.add(rndUp);
		
		JPanel ups = new JPanel();
		ups.setLayout(new GridLayout(0, 2));
		JButton upLeft = new JButton("Sample " + (leftTrim-toAdd) + " - " + rightTrim);
		upLeft.setFont(tah20);
		if ((leftTrim-toAdd) < 0)
			upLeft.setEnabled(false);
		upLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				left.setXReal(leftTrim-toAdd);
				close();
			}
		});
		
		JButton upRight = new JButton("Sample " + leftTrim + " - " + (rightTrim+toAdd));
		upRight.setFont(tah20);
		if ((rightTrim+toAdd) >= data.dataPoints)
			upRight.setEnabled(false);
		upRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				right.setXReal(rightTrim+toAdd);
				close();
			}
		});
		ups.add(upLeft);
		ups.add(upRight);
		add(ups);
		
		JLabel rndDown = new JLabel(" Round samples down to " + numSelected/freq + " seconds");
		rndDown.setFont(tah20);
		this.add(rndDown);
		
		JPanel downs = new JPanel();
		downs.setLayout(new GridLayout(0, 2));
		JButton downsLeft = new JButton("Sample " + (leftTrim+toRemove) + " - " + rightTrim);
		downsLeft.setFont(tah20);
		if ((leftTrim+toRemove) >= rightTrim)
			downsLeft.setEnabled(false);
		downsLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				left.setXReal(leftTrim+toRemove);
				close();
			}
		});
		JButton downsRight = new JButton("Sample " + leftTrim + " - " + (rightTrim-toRemove));
		downsRight.setFont(tah20);
		if (leftTrim >= (rightTrim-toRemove))
			downsRight.setEnabled(false);
		downsRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				right.setXReal(rightTrim-toRemove);
				close();
			}
		});
		downs.add(downsLeft);
		downs.add(downsRight);
		add(downs);
		
		add(new JLabel(""));
		
		JButton cancel = new JButton("Cancel");
		cancel.setFont(tah20);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				badExit = true;
				close();
			}
		});
		add(cancel);
	}
	
	private void close() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dispatchEvent(new WindowEvent(RoundingDialog.this, WindowEvent.WINDOW_CLOSING));
	}

}
