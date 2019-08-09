package com.esb.execution;

import com.esb.api.component.ProcessorSync;
import com.esb.api.message.Message;
import com.esb.graph.ExecutionGraph;
import com.esb.graph.ExecutionNode;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static com.esb.execution.ExecutionUtils.nextNode;
import static com.esb.execution.ExecutionUtils.nullSafeMap;
import static reactor.core.publisher.Mono.from;

public class ProcessorSyncExecutor implements FlowExecutor {

    @Override
    public Publisher<EventContext> execute(ExecutionNode executionNode, ExecutionGraph graph, Publisher<EventContext> publisher) {

        ProcessorSync processorSync = (ProcessorSync) executionNode.getComponent();

        Mono<EventContext> mono = from(publisher).handle(nullSafeMap(messageContext -> {
            // The context contains the input Flow Message.
            Message inMessage = messageContext.getMessage();

            // Apply the input Message to the processor and we
            // let it process it (transform) to its new value.
            Message outMessage = processorSync.apply(inMessage);

            // We replace in the context the new output message.
            messageContext.replaceWith(outMessage);

            return messageContext;

        }));

        ExecutionNode next = nextNode(executionNode, graph);

        // Move on building the flux for the following
        // processors in the execution graph definition.
        return FlowExecutorFactory.get().build(next, graph, mono);
    }
}
