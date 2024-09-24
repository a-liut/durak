# durak

`durak` is an implementation of the classic russian card game [_Durak_](https://en.wikipedia.org/wiki/Durak).

The game is multiplayer, from 2 to 6 players. A _host_ player creates the game session, then waits for other players to join.
When all players have joined the session, the game starts.

**Create a new game session**
```bash
durak create --player-count 2 --player-name Alice
```

**Join a game session**
```bash
durak join create --player-name Bob
```

## Commands

### Create game session

`durak create` creates a new game session. It accepts the following input:

- `--player-count <n>`, `-pc <n>` (Integer): how many players will be playing
- `--player-name <name>`, `-pn <name>` (String): the name of the local player
- `--port` (Integer, _default_: 9999): port to be used for communicating with other players

### Join game session

`durak join` joins an existing game session. It accepts the following input:

- `--host` (String, _default_: `"localhost"`): the url of the host
- `--port` (Integer, _default_: 9999): port to be used for communicating with the host
- `--player-name <name>`, `-pn <name>` (String): the name of the local player

## Architecture

The game is entirely developed in Kotlin, and uses sockets to enable communication between players.
Sockets are created during the initialization of the game, and kept open until the game ends.

There are two modes in which the application runs:

- as a _host_
- as a _client_

### The host

The host stores the game state and send messages to other players via sockets.
It receives input from each player and updates the game state when needed.

### The client

The clients do not store any game data: they only send and receive messages to and from the host.

## Building the game
