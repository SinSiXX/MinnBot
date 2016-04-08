# jMinnBot
Simple to setup Discord Chat Bot.

# Download

Download the runnable jar [here](https://www.dropbox.com/s/r5u2msps2tuuqcc/MinnBot2.0.jar?dl=0).

# How to use

1. Download the runnable jar [here](https://www.dropbox.com/s/r5u2msps2tuuqcc/MinnBot2.0.jar?dl=0).
2. Create a bot account for discord.
3. Invite the bot account to a guild.
4. Run the jar and click "launch".
5. Populate the automatically generated jar 
<div class="highlight highlight-text-json">
<pre>
{
    "owner": "86699011792191488",
    "prefix": "!",
    "token": "AOIHIPUHD34534.JKHOS6876OH6IKHJ.OHOIHJF",
	  "inviteurl":"https://discordapp.com/oauth2/authorize?&client_id=13468425138731684&scope=bot&permissions=67108863"
}
</pre>
</div>
6. Click "launch" again.
7. Make sure the bot has permission to both read and write messages in the channel you want to use the bot in.
8. Type (i.e.) !help or !info in a channel and read the instructions.
 
 Create MinnBot instance: 
 <pre><code>
    public static void main(String[] a) {
 		console = new MinnBotUserInterface(); // Creates Window
 		console.setVisible(true); // Makes window visible
 		console.pack(); // Packs window
 	}
 </code>
 </pre>
 <pre><code>
    JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("BotConfig.json"))));
 	String token = obj.getString("token").replace(" ", "");
 	boolean isBot = false;
 	JDA api;
 	try {
 		api = new JDABuilder().setBotToken(token).setAudioEnabled(false).setAutoReconnect(true).buildBlocking();
 		isBot = true;
 	} catch (LoginException e) {
 		try {
 			String email = obj.getString("email");
 			String pass = obj.getString("password");
 			api = new JDABuilder().setEmail(email).setPassword(pass).setAudioEnabled(false)
 							.setAutoReconnect(true).buildBlocking();
 		} catch (Exception e1) {
 			throw e;
 		}
 	}
 	String pre = obj.getString("prefix");
 	String ownerId = obj.getString("owner");
 	String inviteUrl = obj.getString("inviteurl");
 	MinnBot bot = new MinnBot(pre, ownerId, inviteUrl, isBot, console.logger, api);
 	api.addEventListener(bot.initCommands(api));
 	as.setApi(api);
 	MinnBotUserInterface.bot = bot;
 </code></pre>
 
# Support

Official development discord: [MinnBot Development](https://discord.gg/0mcttggeFpaqAWLI)

# Requirements

<ul>
<li>JDA: https://github.com/DV8FromTheWorld/JDA/</li>
<li>Java 8: https://www.java.com/en/download/</li>
</ul>