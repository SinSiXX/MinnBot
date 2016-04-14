package minn.minnbot.gui;

import javax.swing.*;
import java.awt.*;

public class PopupErrorLog extends JFrame {

	private static final long serialVersionUID = 4147401599586295405L;
	
	private TextArea textArea;
	
	public void writeln(String input) {
		textArea.append("\n" + input);
	}

	public void flush() {
		textArea.setText("Error Logs");
	}
	
	public PopupErrorLog() {
		this.setResizable(false);
		setMinimumSize(new Dimension(655, 300));
//		setMinimumSize(new Dimension(640, 300));
		setTitle("Error log");
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
		textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
		textArea.setText("Error Logs");
		textArea.setForeground(new Color(255, 51, 0));
		textArea.setBackground(new Color(0, 0, 0));
		textArea.setBounds(0, 0, 647, 271);
		panel.add(textArea);
		setVisible(false);
		pack();
	}

}
