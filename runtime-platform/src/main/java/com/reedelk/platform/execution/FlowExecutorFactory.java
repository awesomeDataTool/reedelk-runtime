package com.reedelk.platform.execution;

import com.reedelk.platform.component.foreach.ForEachExecutor;
import com.reedelk.platform.component.foreach.ForEachWrapper;
import com.reedelk.platform.component.fork.ForkExecutor;
import com.reedelk.platform.component.fork.ForkWrapper;
import com.reedelk.platform.component.router.RouterExecutor;
import com.reedelk.platform.component.router.RouterWrapper;
import com.reedelk.platform.component.stop.StopExecutor;
import com.reedelk.platform.component.trycatch.TryCatchExecutor;
import com.reedelk.platform.component.trycatch.TryCatchWrapper;
import com.reedelk.platform.graph.ExecutionGraph;
import com.reedelk.platform.graph.ExecutionNode;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.component.ProcessorAsync;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.component.Stop;
import org.reactivestreams.Publisher;

import java.util.*;

import static java.lang.String.format;

public class FlowExecutorFactory {

    private static final FlowExecutorFactory INSTANCE = new FlowExecutorFactory();

    private static final Map<Class<?>, FlowExecutor> COMPONENT_EXECUTOR;
    static {
        Map<Class<?>, FlowExecutor> tmp = new HashMap<>();
        tmp.put(Stop.class, new StopExecutor());
        tmp.put(ForkWrapper.class, new ForkExecutor());
        tmp.put(RouterWrapper.class, new RouterExecutor());
        tmp.put(ForEachWrapper.class, new ForEachExecutor());
        tmp.put(TryCatchWrapper.class, new TryCatchExecutor());
        tmp.put(ProcessorSync.class, new ProcessorSyncExecutor());
        tmp.put(ProcessorAsync.class, new ProcessorAsyncExecutor());
        COMPONENT_EXECUTOR = Collections.unmodifiableMap(tmp);
    }

    private FlowExecutorFactory() {
    }

    public static FlowExecutorFactory get() {
        return INSTANCE;
    }

    public Publisher<MessageAndContext> execute(Publisher<MessageAndContext> publisher, ExecutionNode next, ExecutionGraph graph) {
        return executorOf(next.getComponent())
                .execute(publisher, next, graph);
    }

    FlowExecutor executorOf(Component component) {
        if (COMPONENT_EXECUTOR.containsKey(component.getClass())) {
            return COMPONENT_EXECUTOR.get(component.getClass());
        }
        // We check if any of the superclasses implement a known
        // type for which a builder has been defined.
        Class<?>[] componentInterfaces = component.getClass().getInterfaces();
        return executorOf(componentInterfaces)
                .orElseThrow(() ->
                        new IllegalStateException(format("Could not find executor for class [%s]", component.getClass())));
    }

    private Optional<FlowExecutor> executorOf(Class<?>[] componentInterfaces) {
        Set<Class<?>> fluxBuilderInterfaceNames = COMPONENT_EXECUTOR.keySet();
        for (Class<?> interfaceClazz : componentInterfaces) {
            if (fluxBuilderInterfaceNames.contains(interfaceClazz)) {
                return Optional.of(COMPONENT_EXECUTOR.get(interfaceClazz));
            }
        }
        return Optional.empty();
    }
}
