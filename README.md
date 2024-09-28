# durak

`durak` is an implementation of the classic russian card game [_Durak_](https://en.wikipedia.org/wiki/Durak).

The game is multiplayer, from 2 to 6 players. A _host_ player creates the game session, then waits for other players to
join.
When all players have joined the session, the game starts.

**Create a new game session**

```bash
durak create --player-count 2 --player-name Mario
```

**Join a game session**

```bash
durak join create --player-name Luigi
```

## Commands

### Create game session

`durak create` creates a new game session. It accepts the following input:

| Name                    | Type    | Default | Optional | Description                                          |
|-------------------------|---------|---------|----------|------------------------------------------------------|
| `--player-count`, `-pc` | Integer |         | no       | How many players will be playing                     |
| `--player-name`, `-pn`  | String  |         | no       | The name of the local player                         |
| `--port`, `-p`          | Integer | 9999    | yes      | Port to be used for communicating with other players |

### Join game session

`durak join` joins an existing game session. It accepts the following inputs:

| Name                   | Type    | Default   | Optional | Description                                     |
|------------------------|---------|-----------|----------|-------------------------------------------------|
| `--host`, `-h`         | String  | localhost | yes      | The url of the host                             |
| `--port`, `-p`         | Integer | 9999      | yes      | Port to be used for communicating with the host |
| `--player-name`, `-pn` | String  |           | no       | The name of the local player                    |

## Running the game

### Gradle run

You can run `durak` via the Gradle `run` task:

```bash
./gradlew run --args="create-multiplayer -pc 2 -pn Mario"
```

```bash
./gradlew run --args="join-multiplayer -pn Mario"
```

### Java jar

Build the game by running:

```gradle
./gradlew shadowJar
```

Then run the jar file:

```gradle
 java -jar build/libs/durak-0.0.1-all.jar create -pc 2 -pn Mario
```
