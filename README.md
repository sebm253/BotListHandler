# BotListHandler

This handler requires [JDA](https://github.com/DV8FromTheWorld/JDA), so if your bot doesn't use JDA, it won't work.

## Getting started

### Replace `%VERSION%` with the latest release tag: [![Release](https://jitpack.io/v/caneleex/BotListHandler.svg)](https://jitpack.io/#caneleex/BotListHandler)

**Gradle**
```gradle
repositories {
  maven {
    url 'https://jitpack.io'
  }
}

dependencies {
  implementation group: 'com.github.caneleex', name: 'BotListHandler', version: '%VERSION%'
}
```

**Maven**
```xml
<repositories>
  <repository>
    <id>jitpack</id>
    <name>jitpack</name>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.caneleex</groupId>
    <artifactId>BotListHandler</artifactId>
    <version>%VERSION%</version>
  </dependency>
</dependencies>
```

## Initialization

Using the `addBotList` method:
```java
BotListHandler botListHandler = new BLHBuilder().addBotList(BotList.TOP_GG, "top_gg_token")
  .addBotList(BotList.DBOATS, "dboats_token")
  .build();
```
Using the constructor:
```java
Map<BotList, String> botLists = new HashMap<>();
botLists.put(BotList.DBL, "dbl_token");
botLists.put(BotList.DEL, "del_token");

BotListHandler botListHandler = new BLHBuilder(botLists).build();
```
Using the `setBotLists` method:
```java
Map<BotList, String> botLists = new HashMap<>();
botLists.put(BotList.BOTLIST_SPACE, "botlist_space_token");
botLists.put(BotList.DBOTS_GG, "dbots_gg_token");

BotListHandler botListHandler = new BLHBuilder().setBotLists(botLists).build();
```

## Implementation

There are 2 ways to use BotListHandler:

### Event based (recommended)

```java
JDA jda = JDABuilder.create("token", intents)
  .addEventListeners(new BLHListener(botListHandler))
  .build();
  
jda.awaitReady(); // optional, but if you want to update the stats after a ReadyEvent, it's required
```

### Automatic stats posting
```java
JDA jda = JDABuilder.create("token", intents)
  .build();
  
jda.awaitReady(); // optional

BotListHandler botListHandler = new BLHBuilder(jda, botLists)
  .setAutoPostDelay(20, TimeUnit.SECONDS).build();
// or
BotListHandler botListHandler = new BLHBuilder(jda).setBotLists(botLists)
  .setAutoPostDelay(3, TimeUnit.MINUTES).build();
```

### You can store the `BotListHandler` instance so if some token provided at compile-time is invalid, you can use the `swapToken` method to hotswap it at runtime.

## Currently supported bot lists

[botlist.space](https://botlist.space)

[botsfordiscord.com](https://botsfordiscord.com)

[botsondiscord.xyz](https://botsondiscord.xyz)

[discordbotlist.com](https://discordbotlist.com)

[discord.boats](https://discord.boats)

[discordbots.co](https://discordbots.co)

[discord.bots.gg](https://discord.bots.gg)

[discordextremelist.xyz](https://discordextremelist.xyz)

[discordservices.net](https://discordservices.net)

[top.gg](https://top.gg)
