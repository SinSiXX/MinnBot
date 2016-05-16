package minn.minnbot.gui;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ServerLog extends JFrame {

	private static final long serialVersionUID = 3482201082197055631L;
	private List<Guild> guilds;
	private Choice choice;
	private TextArea textArea;
	private JDA api;
	private GuildListener listener;
	private Guild selectedGuild;
	private boolean enabled;

	public ServerLog(List<Guild> guilds, JDA api) {
		if (guilds == null || guilds.isEmpty() || api == null)
			throw new IllegalArgumentException("No guilds available.");
		this.guilds = guilds;
		this.api = api;
		ServerLog log = this;
		setResizable(false);
		setMinimumSize(new Dimension(600, 400));
		setTitle("Server Log");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		textArea = new TextArea();
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(Color.BLACK);
		textArea.setBounds(10, 36, 574, 325);
		panel.add(textArea);

		choice = new Choice();

		choice.addItemListener(e -> {
            String item = choice.getSelectedItem();
            for (Guild g : log.guilds) {
                if (!g.getName().equals(item))
                    continue;
                selectedGuild = g;
            }
            if (enabled) {
                if (listener != null) {
                    log.api.removeEventListener(listener);
                }
                listener = new GuildListener(selectedGuild, textArea);
                log.api.addEventListener(listener);
            }
        });
		choice.setBackground(Color.WHITE);
		choice.setBounds(10, 10, 275, 20);
		for (Guild g : log.guilds) {
			choice.add(g.getName());
		}
		panel.add(choice);

		JToggleButton tglbtnEnabled = new JToggleButton("Dynamic");
		tglbtnEnabled.addActionListener(e -> {
            enabled = !enabled;
            if (enabled) {
                String item = choice.getSelectedItem();
                for (Guild g : log.guilds) {
                    if (!g.getName().equals(item))
                        continue;
                    selectedGuild = g;
                }
                if (listener != null) {
                    log.api.removeEventListener(listener);
                }
                listener = new GuildListener(selectedGuild, textArea);
                log.api.addEventListener(listener);
            }
        });
		enabled = false;
		tglbtnEnabled.setBackground(Color.WHITE);
		tglbtnEnabled.setBounds(477, 10, 107, 23);
		panel.add(tglbtnEnabled);

		pack();
	}
}
