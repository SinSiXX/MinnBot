package minn.minnbot.gui;

import javax.swing.*;
import java.awt.*;

public class PopupDefaultLog extends JFrame {

	private static final long serialVersionUID = 4147401599586295405L;
	
	private TextArea textArea;
	
	public void writeln(String input) {
		textArea.append("\n" + input);
	}
	
	public void flush() {
		textArea.setText("Default Logs");
	}

	public PopupDefaultLog() {
		this.setResizable(false);
		setMinimumSize(new Dimension(645, 300));
//		setMinimumSize(new Dimension(645, 300));
		setTitle("Default log");
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setForeground(new Color(255, 255, 255));
		panel.setBackground(new Color(0, 0, 0));
		panel.setBorder(null);
		panel.setBounds(0, 0, 639, 271);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
		textArea.setText("Default Logs");
		textArea.setForeground(new Color(255, 255, 255));
		textArea.setBackground(new Color(0, 0, 0));
		textArea.setBounds(0, 0, 639, 271);
		panel.add(textArea);
		setVisible(false);
		pack();
	}

}
