package org.bookmark.pro.service.task.handler;

import com.intellij.openapi.components.Service;
import org.bookmark.pro.service.task.ScheduledService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service(Service.Level.PROJECT)
public final class ScheduledServiceHandler implements ScheduledService {
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    @Override
    public void inspectionFileBookmark(long delay) {

    }

    public void shutdown() {
        service.shutdown();
    }
}
