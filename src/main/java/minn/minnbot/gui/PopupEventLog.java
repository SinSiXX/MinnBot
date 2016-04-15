package minn.minnbot.gui;

import javax.swing.*;
import java.awt.*;

public class PopupEventLog extends JFrame {

	private static final long serialVersionUID = 4147401599586295405L;
	
	private TextArea textArea;
	
	public void writeln(String input) {
		textArea.append("\n" + input);
	}

	public void flush() {
		textArea.setText("Event Logs");
	}
	
	public PopupEventLog() {
		this.setResizable(false);
		setMinimumSize(new Dimension(655, 300));
//		setMinimumSize(new Dimension(640, 300));
		setTitle("Event log");
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setForeground(new Color(255, 255, 255));
		panel.setBackground(new Color(0, 0, 0));
		panel.setBorder(null);
		panel.setBounds(0, 0, 649, 271);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Century", Font.BOLD, 10));
		textArea.setText("Event Logs");
		textArea.setForeground(new Color(255, 126, 47));
		textArea.setBackground(new Color(0, 0, 0));
		textArea.setBounds(0, 0, 647, 271);
		panel.add(textArea);
		setVisible(false);
		pack();
	}

}
