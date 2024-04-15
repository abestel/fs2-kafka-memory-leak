# FS2 Kafka - Memory Leak

This project is a simple example of a memory leak that occurs when using FS2 Kafka.

To run the project, you need to have Docker installed. Then, you can run the following command:

```bash
docker compose up -d kafka-init
```

Then run the simple application:

```bash
sbt run
```

You can monitor the growth of the CallbackStack by executing:

```bash
./scripts/monitor-gc.sh
```

You can also force a GC by executing:

```bash
jcmd fs2.kafka.MemoryLeaker GC.run
```

With the current version of FS2 Kafka (3.4.0), the memory leak is present, and the callback stack seems to grow monotonically, even when explicitly executing garbage collection:

```
 295:            22            528  cats.effect.CallbackStack$Node
 295:            22            528  cats.effect.CallbackStack$Node
 296:            22            528  cats.effect.CallbackStack$Node
  76:           635          15240  cats.effect.CallbackStack$Node
  73:           641          15384  cats.effect.CallbackStack$Node
  73:           644          15456  cats.effect.CallbackStack$Node
  73:           649          15576  cats.effect.CallbackStack$Node
  73:           653          15672  cats.effect.CallbackStack$Node
  73:           658          15792  cats.effect.CallbackStack$Node
  69:           663          15912  cats.effect.CallbackStack$Node
  69:           667          16008  cats.effect.CallbackStack$Node
  69:           673          16152  cats.effect.CallbackStack$Node
  69:           676          16224  cats.effect.CallbackStack$Node
  69:           681          16344  cats.effect.CallbackStack$Node
  68:           685          16440  cats.effect.CallbackStack$Node
  67:           690          16560  cats.effect.CallbackStack$Node
  67:           695          16680  cats.effect.CallbackStack$Node
  67:           699          16776  cats.effect.CallbackStack$Node
  67:           704          16896  cats.effect.CallbackStack$Node
  66:           709          17016  cats.effect.CallbackStack$Node
  67:           715          17160  cats.effect.CallbackStack$Node
  66:           718          17232  cats.effect.CallbackStack$Node
  65:           723          17352  cats.effect.CallbackStack$Node
  64:           729          17496  cats.effect.CallbackStack$Node
  64:           732          17568  cats.effect.CallbackStack$Node
  64:           737          17688  cats.effect.CallbackStack$Node
  64:           742          17808  cats.effect.CallbackStack$Node
  63:           747          17928  cats.effect.CallbackStack$Node
  63:           751          18024  cats.effect.CallbackStack$Node
  63:           757          18168  cats.effect.CallbackStack$Node
  63:           760          18240  cats.effect.CallbackStack$Node
  62:           765          18360  cats.effect.CallbackStack$Node
  62:           769          18456  cats.effect.CallbackStack$Node
  62:           774          18576  cats.effect.CallbackStack$Node
  62:           779          18696  cats.effect.CallbackStack$Node
  62:           783          18792  cats.effect.CallbackStack$Node
  61:           788          18912  cats.effect.CallbackStack$Node
  61:           793          19032  cats.effect.CallbackStack$Node
  61:           797          19128  cats.effect.CallbackStack$Node
  61:           803          19272  cats.effect.CallbackStack$Node
  60:           807          19368  cats.effect.CallbackStack$Node
```

Executing the modified version present in https://github.com/fd4s/fs2-kafka/pull/1318 the memory leak seems to disappear:

```
 294:            22            528  cats.effect.CallbackStack$Node
 295:            22            528  cats.effect.CallbackStack$Node
 295:            22            528  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  82:           501          12024  cats.effect.CallbackStack$Node
  82:           501          12024  cats.effect.CallbackStack$Node
  82:           501          12024  cats.effect.CallbackStack$Node
  82:           501          12024  cats.effect.CallbackStack$Node
  82:           501          12024  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           502          12048  cats.effect.CallbackStack$Node
  83:           502          12048  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           502          12048  cats.effect.CallbackStack$Node
  83:           502          12048  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           502          12048  cats.effect.CallbackStack$Node
  83:           502          12048  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  83:           501          12024  cats.effect.CallbackStack$Node
  84:           501          12024  cats.effect.CallbackStack$Node
  84:           501          12024  cats.effect.CallbackStack$Node
  84:           501          12024  cats.effect.CallbackStack$Node
  84:           501          12024  cats.effect.CallbackStack$Node
  84:           501          12024  cats.effect.CallbackStack$Node
  84:           501          12024  cats.effect.CallbackStack$Node
  84:           502          12048  cats.effect.CallbackStack$Node
  84:           501          12024  cats.effect.CallbackStack$Node
  84:           501          12024  cats.effect.CallbackStack$Node
```