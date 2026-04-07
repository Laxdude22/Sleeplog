# SleepLog

SleepLog is a lightweight Fabric server mod that logs player sleep activity and night skips.

## Features

- Logs when players start sleeping
- Shows how many players are sleeping
- Lists which players are sleeping
- Logs when the night is skipped

## Example Output

```text
[SleepLog] Sleeping now: 2/5 - Alice, Bob
[SleepLog] Night skipped (2/5 sleeping): Alice, Bob
```

## Requirements

- Minecraft
- Fabric Loader
- Java

## Build

```bash
gradle build --refresh-dependencies
```

Output:

```text
build/libs/sleeplog-1.2.0.jar
```

## License

This project is licensed under the GNU General Public License v3.0.
See the LICENSE file for details.
