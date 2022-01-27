package org.acme;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.resteasy.core.ResteasyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Path("/hello")
public class GreetingResource {

    public static final Logger log = LoggerFactory.getLogger("test");

    @Inject
    ManagedExecutor executor;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws InterruptedException {
        ResteasyContext.pushContext(String.class, "value1");
        log("helloResource.1");
        CountDownLatch l1 = new CountDownLatch(1);
        CountDownLatch l2 = new CountDownLatch(1);
        executor.submit(() -> {
            log("async.1");
            l1.countDown();
            l2.await();
            log("async.2");
            return null;
        });
        l1.await();

        ResteasyContext.pushContext(String.class, "value2");

        log("helloResource.2");
        l2.countDown();

        return "Hello RESTEasy";
    }

    @GET
    @Path("/cme")
    @Produces(MediaType.TEXT_PLAIN)
    public String cme() throws InterruptedException {
        ResteasyContext.pushContext(String.class, "value1");
        CountDownLatch l1 = new CountDownLatch(1);
        CountDownLatch l2 = new CountDownLatch(1);
        executor.submit(() -> {
            final Map<Class<?>, Object> contextDataMap = ResteasyContext.getContextDataMap();
            try {
                boolean first = true;
                for (Map.Entry<Class<?>, Object> entry : contextDataMap.entrySet()) {
                    Class<?> aClass = entry.getKey();
                    Object o = entry.getValue();
                    if (first) {
                        l1.countDown();
                        try {
                            l2.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        first = false;
                    }
                }
            } catch (Exception e) {
                log.error("Error in async Thread", e);
            }
            return null;
        });
        l1.await();

        ResteasyContext.pushContext(Long.class, 1L);
        l2.countDown();

        return "Hello RESTEasy";
    }

    static void log(String source) {
        Map<Class<?>, Object> contextDataMap = ResteasyContext.getContextDataMap(false);
        log.info("Source: {} dataMap: hash={}, {}", source, System.identityHashCode(contextDataMap), contextDataMap);
    }
}
