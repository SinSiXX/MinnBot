package minn.minnbot.gui;

import minn.minnbot.MinnBot;
import minn.minnbot.entities.Logger;
import minn.minnbot.entities.impl.LoggerImpl;
import minn.minnbot.util.TimeUtil;
import net.dv8tion.jda.JDA;
import org.eclipse.wb.swing.FocusTraversalOnArray;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MinnBotUserInterface extends JFrame {

    private static final long serialVersionUID = 7465709685820328753L;
    public static MinnBot bot;
    private static TextArea eventArea;
    private static TextArea defaultArea;
    private static PopupEventLog popupErrorLog;
    private static PopupDefaultLog popupLog;
    private static AccountSettings as;
    public final Logger logger;
    private JButton btnLaunch;
    private JButton btnStop;
    private ServerLog sLog;
    private JButton btnServerLog;
    private JButton btnRestart;

    public MinnBotUserInterface() {
        popupErrorLog = new PopupEventLog();
        popupLog = new PopupDefaultLog();
        MinnBotUserInterface c = this;
        c.setMinimumSize(new Dimension(539, 495));
        this.setResizable(false);
        getContentPane().setForeground(new Color(51, 0, 51));
        getContentPane().setBackground(new Color(0, 102, 102));
        getContentPane().setLayout(null);

        JPanel panel_2 = new JPanel();
        panel_2.setBounds(30, 366, 472, 95);
        panel_2.setBackground(new Color(0, 102, 102));
        getContentPane().add(panel_2);
        panel_2.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(10, 11, 513, 355);
        getContentPane().add(panel);
        panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel.setToolTipText("Logger output");
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(null);

        defaultArea = new TextArea();
        defaultArea.setText("Default Logs");
        defaultArea.setForeground(Color.WHITE);
        defaultArea.setBackground(Color.BLACK);
        defaultArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupLog.setVisible(true);
            }
        });
        defaultArea.setFont(new Font("Century", Font.BOLD, 10));
        defaultArea.setBounds(10, 8, 493, 244);
        panel.add(defaultArea);

        eventArea = new TextArea();
        eventArea.setBounds(10, 258, 493, 87);
        panel.add(eventArea);
        eventArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popupErrorLog.setVisible(true);
            }
        });
        eventArea.setText("Event Logs");
        eventArea.setFont(new Font("Century", Font.BOLD, 10));
        eventArea.setForeground(new Color(255, 126, 47));
        eventArea.setBackground(Color.BLACK);

        /* SETTING LOGGER */
        logger = new LoggerImpl(this);

        JCheckBox chckbxLogErrors = new JCheckBox("Log Events");
        chckbxLogErrors.setForeground(Color.WHITE);
        chckbxLogErrors.setBackground(Color.DARK_GRAY);
        chckbxLogErrors.addActionListener(e -> logger.toggleErrorLog());
        chckbxLogErrors.setBounds(334, 7, 126, 23);
        panel_2.add(chckbxLogErrors);

        JCheckBox chckbxLogMessages = new JCheckBox("Log Messages");
        chckbxLogMessages.setForeground(Color.WHITE);
        chckbxLogMessages.setBackground(Color.DARK_GRAY);
        chckbxLogMessages.addActionListener(e -> logger.toggleMessageLog());
        chckbxLogMessages.setBounds(334, 36, 126, 23);
        panel_2.add(chckbxLogMessages);

        JButton btnAccount = new JButton("Account Settings");
        btnAccount.setForeground(Color.WHITE);
        btnAccount.setBackground(Color.DARK_GRAY);
        btnAccount.addActionListener(e -> {
            if (as != null) {
                as.setTitle("Account Settings");
                as.setVisible(true);
            } else {
                writeEvent("[MINNBOT] Bot must be launched to access account settings.");
            }
        });
        btnAccount.setBounds(106, 36, 222, 23);
        btnAccount.setEnabled(false);
        panel_2.add(btnAccount);

        JButton btnGenerateCommandJson = new JButton("Store Information as Jsons.");
        btnGenerateCommandJson.setForeground(Color.WHITE);
        btnGenerateCommandJson.setBackground(Color.DARK_GRAY);

        btnGenerateCommandJson.addActionListener(e -> {
            try {
                bot.handler.generateJson("commands.json");
                bot.handler.saveTags();
                if(logger.saveToJson()){
                    eventArea.append(String.format("\n%s Error log saved.", String.format("%s [INFO]", TimeUtil.timeStamp())));
                }
                try {
                    bot.handler.savePrefixMap();
                } catch (Exception ex) {
                    logger.logThrowable(ex);
                }
            } catch (Exception e1) {
                eventArea.append("\nThe bot must be launched to generate the commands.");
            }
        });
        btnGenerateCommandJson.setBounds(106, 7, 222, 23);
        btnGenerateCommandJson.setEnabled(false);
        panel_2.add(btnGenerateCommandJson);


        btnStop = new JButton("Stop");
        btnStop.setForeground(Color.WHITE);
        btnStop.addActionListener(e -> {
            try {
                JDA api = bot.api;
                if (api != null) {
                    api.shutdown(false);
                    btnAccount.setEnabled(false);
                    btnGenerateCommandJson.setEnabled(false);
                    btnLaunch.setEnabled(true);
                    btnRestart.setEnabled(false);
                    bot.handler.saveTags();
                    if (sLog != null) {
                        bot.api.removeEventListener(sLog);
                        sLog.setVisible(false);
                        sLog = null;
                    }
                    writeln("[MINNBOT] Shutdown complete.");
                } else {
                    writeEvent("[ERROR] MinnBot is not currently running.");
                }
            } catch (Exception ex) {
                writeEvent("[ERROR] Shutdown failed: \n> " + ex.getMessage());
            }
        });
        btnStop.setBounds(10, 36, 86, 23);
        panel_2.add(btnStop);
        btnStop.setBackground(Color.DARK_GRAY);


        JButton btnClearConsole = new JButton("Clear Console");
        btnClearConsole.setForeground(Color.WHITE);
        btnClearConsole.setBackground(Color.DARK_GRAY);
        btnClearConsole.addActionListener(e -> {
            defaultArea.setText("Message Logs");
            eventArea.setText("Event Logs");
            popupErrorLog.flush();
            popupLog.flush();
        });
        btnClearConsole.setBounds(334, 66, 126, 23);

        panel_2.add(btnClearConsole);

        btnServerLog = new JButton("Server Logs");
        btnServerLog.setEnabled(false);
        btnServerLog.setForeground(Color.WHITE);
        btnServerLog.setBackground(Color.DARK_GRAY);
        btnServerLog.addActionListener(e -> {
            if (bot.api != null) {
                if (sLog == null) {
                    try {
                        sLog = new ServerLog(bot.api.getGuilds(), bot.api);
                    } catch (Exception e1) {
                        writeEvent("[MINNBOT] Exception - " + e1.getMessage());
                        return;
                    }
                    sLog.setVisible(true);
                } else
                    sLog.setVisible(true);
            } else {
                writeEvent("[MINNBOT] You can not access this window without loggin in.");
            }
        });
        btnServerLog.setBounds(106, 66, 222, 23);
        panel_2.add(btnServerLog);

        btnRestart = new JButton("Restart");
        btnRestart.setForeground(Color.WHITE);
        btnRestart.setBackground(Color.DARK_GRAY);
        btnRestart.addActionListener(e -> {
            // FIXME
            try {
                if (bot.api != null) {
                    btnStop.getActionListeners()[0].actionPerformed(null);
                    MinnBot.launch(c);
                    btnAccount.setEnabled(true);
                    btnGenerateCommandJson.setEnabled(true);
                    btnLaunch.setEnabled(false);
                    btnRestart.setEnabled(true);
                } else {
                    throw new IllegalArgumentException("Bot is not running.");
                }

            } catch (Exception e1) {
                e1.printStackTrace();
                writeEvent("[MINNBOT] " + e1.getMessage());
            }
        });
        btnRestart.setEnabled(false);
        btnRestart.setBounds(10, 66, 86, 23);
        panel_2.add(btnRestart);

        btnLaunch = new JButton("Launch");
        btnLaunch.setForeground(Color.WHITE);
        btnLaunch.setBounds(10, 7, 86, 23);
        panel_2.add(btnLaunch);
        btnLaunch.addActionListener(e -> {
            try {
                MinnBot.launch(c);
                btnAccount.setEnabled(true);
                btnGenerateCommandJson.setEnabled(true);
                btnLaunch.setEnabled(false);
                btnRestart.setEnabled(true);
                btnServerLog.setEnabled(true);
            } catch (Exception exception) {
                if (bot != null)
                    bot.log(exception.getMessage());
                else
                    exception.printStackTrace();
            }
        });
        btnLaunch.setBackground(Color.DARK_GRAY);

        setTitle("MinnBot");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0));
        setForeground(new Color(10, 16, 37));
        setFont(new Font("Consolas", Font.BOLD, 12));
        setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{btnLaunch, btnStop, btnGenerateCommandJson,
                btnAccount, chckbxLogErrors, chckbxLogMessages}));


    }

    public void writeEvent(String input) {
        popupErrorLog.writeln(input);
        eventArea.append("\n" + input);
    }

    public void writeln(String input) {
        popupLog.writeln(input);
        defaultArea.append("\n" + input);
    }

    public void setAccountSettings(AccountSettings as) {
        if (as != null) {
            MinnBotUserInterface.as = as;
        }
    }
}
