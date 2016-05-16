package minn.minnbot.gui;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.managers.AccountManager;
import net.dv8tion.jda.utils.AvatarUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.UnsupportedEncodingException;

public class AccountSettings extends JFrame {

	private static final long serialVersionUID = 1L;
	private JDA api;
	private MinnBotUserInterface i;
	private AccountManager manager;
	private boolean isVisible;
	private JTextField txtUsername;
	private JTextField txtGame;
	private JTextField txtAvatar;
	private JTextField nameField;
	private JTextField gameField;
	private JTextField avatarField;
	private TextArea textArea;
	private JTextArea txtrMindTheRate;
	private JScrollPane scrollPane;

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		this.isVisible = isVisible;
	}

	public void setApi(JDA api) {
		if (api != null) {
			this.api = api;
			manager = api.getAccountManager();
			init();
			// setVisible(true);
		}
	}

	public AccountSettings(MinnBotUserInterface i) {
		this.i = i;
		this.setMaximumSize(new Dimension(550, 200));
		this.setMinimumSize(new Dimension(550, 200));

		
	}

	public void init() {
		manager.update();
		getContentPane().setBackground(new Color(0, 128, 128));
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 271, 82);
		getContentPane().add(panel);
		panel.setBackground(new Color(0, 139, 139));
		panel.setLayout(null);

		txtUsername = new JTextField();
		txtUsername.setBounds(0, 0, 86, 20);
		panel.add(txtUsername);
		txtUsername.setEditable(false);
		txtUsername.setText("USERNAME");
		txtUsername.setColumns(10);

		txtGame = new JTextField();
		txtGame.setBounds(0, 31, 86, 20);
		panel.add(txtGame);
		txtGame.setEditable(false);
		txtGame.setText("GAME");
		txtGame.setColumns(10);

		txtAvatar = new JTextField();
		txtAvatar.setBounds(0, 62, 86, 20);
		panel.add(txtAvatar);
		txtAvatar.setEditable(false);
		txtAvatar.setText("AVATAR");
		txtAvatar.setColumns(10);

		nameField = new JTextField();
		nameField.setBounds(96, 0, 175, 20);
		panel.add(nameField);
		nameField.setText(api.getSelfInfo().getUsername());
		nameField.setColumns(10);

		gameField = new JTextField();
		gameField.setBounds(96, 31, 175, 20);
		panel.add(gameField);
		gameField.setText(api.getSelfInfo().getCurrentGame());
		gameField.setColumns(10);

		avatarField = new JTextField();
		avatarField.setBounds(96, 62, 175, 20);
		panel.add(avatarField);
		avatarField.setText("C:\\Path\\File.jpg");
		avatarField.setColumns(10);

		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setText("Event Log");
		textArea.setForeground(new Color(50, 205, 50));
		textArea.setBackground(new Color(0, 0, 0));
		textArea.setBounds(392, 11, 142, 151);
		getContentPane().add(textArea);

		JButton btnUpdate = new JButton("UPDATE");
		btnUpdate.addActionListener(e -> {
            try {
                textArea.setEditable(true);
                textArea.setText("[REQUESTING]\n");
                name(nameField.getText());
                game(gameField.getText());
                avatar(new File(avatarField.getText()));
                textArea.setEditable(false);
                manager.update();
            } catch (Exception e2) {
                i.writeEvent("[MINNBOT] " + e2.getMessage());
            }
        });
		btnUpdate.setBounds(297, 11, 89, 80);
		getContentPane().add(btnUpdate);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 101, 376, 60);
		getContentPane().add(scrollPane);

		txtrMindTheRate = new JTextArea();
		txtrMindTheRate.setEditable(false);
		scrollPane.setViewportView(txtrMindTheRate);
		txtrMindTheRate.setForeground(new Color(255, 215, 0));
		txtrMindTheRate.setBackground(new Color(0, 0, 128));
		txtrMindTheRate.setFont(new Font("Constantia", Font.BOLD, 10));
		txtrMindTheRate.setText(
				"Mind the rate limits:\r\n- Names: 2 Changes Per Hour\r\n- Game: 5 Changes Per Minute\r\n- Avatar: NO RATE LIMIT... ABOOOOOOSE");
		setBackground(new Color(0, 128, 128));
		setTitle("Account Manager");
		setResizable(false);
		setVisible(isVisible);
		pack();
	}

	public void name(String input) {
		if (input.isEmpty() || input.equals(api.getSelfInfo().getUsername()))
			return;
		manager.setUsername(input);
		textArea.append("Name change.\n");
	}

	public void avatar(File avatar) {
		if (!avatar.exists() || !avatar.isFile())
			return;
		String path = avatar.getPath().toLowerCase();
		if (!path.endsWith(".png") && !path.endsWith(".jpg"))
			return;
		try {
			AvatarUtil.Avatar av = AvatarUtil.getAvatar(avatar);
			manager.setAvatar(av);
			textArea.append("Avatar change.\n");
			avatarField.setText("C:\\Path\\Picture.jpg");
		} catch (UnsupportedEncodingException e) {
			i.writeEvent("[ACCOUNT-MANAGER] " + e.getMessage());
		}
	}

	public void game(String game) {
		if (game.isEmpty() || game.equals(api.getSelfInfo().getCurrentGame()))
			return;
		manager.setGame(game);
		textArea.append("Game change.\n");
	}

	public void idle(boolean idle) {
		manager.setIdle(true);
	}
}
