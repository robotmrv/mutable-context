# mutable-context Project
Shows mutable context state problem

Test case 1 
1. start application
2. make http request GET http://localhost:8080/hello 
3. check logs 
ER: ResteasyContext mutation in parent thread does not change values in propagated context
AR: ResteasyContext mutation in parent thread change values in propagated context.
   see logs for `Source: helloResource.2` and `Source: async.2`. dataMap has the same hashCode value,
   and values for key `String` are the same. `Source: async.2` should have the same value as in `Source: async.1`

Test case 2
1. start application
2. make http request GET http://localhost:8080/hello/cme
3. check logs
   ER: no logs for logger `test`
   AR: error log
```
ERROR [test] (executor-thread-0) Error in async Thread: java.util.ConcurrentModificationException
	at java.base/java.util.HashMap$HashIterator.nextNode(HashMap.java:1584)
	at java.base/java.util.HashMap$EntryIterator.next(HashMap.java:1617)
	at java.base/java.util.HashMap$EntryIterator.next(HashMap.java:1615)
	at org.acme.GreetingResource.lambda$cme$1(GreetingResource.java:59)
	at io.smallrye.context.impl.wrappers.SlowContextualCallable.call(SlowContextualCallable.java:21)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:268)
	at io.quarkus.vertx.core.runtime.VertxCoreRecorder$13.runWith(VertxCoreRecorder.java:543)
	at org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2449)
	at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1478)
	at org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:29)
	at org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:29)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.base/java.lang.Thread.run(Thread.java:1502)
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```
