# Discord Karaoke

Karaoke is an activity that many people enjoy during their free time.
This is a bot used to manage a Karaoke event on Discord.

**Warning:** Beware, this software does not compensate for conenction lag.

# Setup:

#### Operating System
This bot can only be run on a **Linux based environment**.

#### Compiler
This bot can be launched only using **Java 8 or above**.
```
java -jar Karaoke.jar
```

#### Youtube DL
This bot depends on having ``youtube-dl`` installed on your machine to download videos from **YouTube**.
```
sudo apt-get install youtube-dl
```
Please refer to [this link](https://linoxide.com/linux-how-to/install-use-youtube-dl-ubuntu/) for more information.

#### ffmpeg
``youtube-dl`` depends on having ``ffmpeg`` installed on your machine to convert video files to audio files.
```
sudo apt-get install ffmpeg
```

#### Sound Exchange (SoX)
This bot depends on having ``sox`` installed on your machine to remove the vocals from the downloaded soundtracks. An additional library ``libsox-fmt-mp3`` is required for formatting mp3 encoding.
```
sudo apt-get install sox
sudo apt-get install libsox-fmt-mp3
```

#### Discord Developer API
You must obstain a Discord developer API key [from here](https://discordapp.com/developers/applications/). Please paste the key in a single line in a file ``discord.key``. This file must be located in the same launch directory.

# Commands

## Setup
```
::setup <user> <URL> (--no-vocals)
```
Begin a new karaoke game in the server.
- **User** is the individual that will be performing.
- **URL** is the link to the YouTube video.
- **--no-vocals** is an optional tag to specify whether or not you wish to have the vocals removed.
```
::setup @Nagi#0001 https://www.youtube.com/watch?v=y6120QOlsfU 
::setup @Nagi#0001 https://www.youtube.com/watch?v=y6120QOlsfU --no-vocals
```

## Terminate
```
::reset
::kill
::restart
```
Resets the current karaoke game. This will terminate all ongoing games, downloads, and playbacks.

## Volume
```
::volume <level>
```
Controls the volume of the karaoke bot.
- **Level** is an integer between 0 to 100 that specifes the volume level range.

# Instructions

## 1. Start
Start the game by selecting an individual and a song.

![alt-text](https://www.kthisiscvpv.com/xEHzY1578787115ygXoE.png)

## 2. Ready Up
The bot will tell you when it's ready. The singer will then click the microphone to begin playback.

![alt-text](https://www.kthisiscvpv.com/nlB2G1578787190WKdry.png)

When the song is playing, all individuals in the channel except the bot and the singer will be server muted.

## 3. Done
When the song is over, the bot will disconnect. Afterwards, all previously muted members will be unmuted.

![alt-text](https://www.kthisiscvpv.com/Ck0y41578787285RuZ9Z.png)