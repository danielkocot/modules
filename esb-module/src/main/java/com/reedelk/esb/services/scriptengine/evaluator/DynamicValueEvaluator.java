package com.reedelk.esb.services.scriptengine.evaluator;

import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.message.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.DynamicValue;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Optional;

public class DynamicValueEvaluator extends AbstractDynamicValueEvaluator {

    public DynamicValueEvaluator(ScriptEngine engine, Invocable invocable) {
        super(engine, invocable);
    }

    @Override
    public <T> Optional<T> evaluate(DynamicValue<T> dynamicValue, Message message, FlowContext flowContext) {
        if (dynamicValue == null) {
            return Optional.empty();
            // Script
        } else if (dynamicValue.isScript()) {
            if (dynamicValue.isEvaluateMessagePayload()) {
                // We avoid evaluating the payload (optimization)
                return convert(message.payload(), dynamicValue.getEvaluatedType());
            } else {
                return execute(dynamicValue, INLINE_SCRIPT, message, flowContext);
            }
        } else {
            // Not a script
            T converted = DynamicValueConverterFactory.convert(dynamicValue.getBody(), String.class, dynamicValue.getEvaluatedType());
            return Optional.ofNullable(converted);
        }
    }

    @Override
    public <T> Optional<T> evaluate(DynamicValue<T> dynamicValue, Throwable exception, FlowContext flowContext) {
        if (dynamicValue == null) {
            return Optional.empty();
            // Script
        } else if (dynamicValue.isScript()) {
            return execute(dynamicValue, INLINE_ERROR_SCRIPT, exception, flowContext);
            // Not a script
        } else {
            T converted = DynamicValueConverterFactory.convert(dynamicValue.getBody(), String.class, dynamicValue.getEvaluatedType());
            return Optional.ofNullable(converted);
        }
    }

    private <T> Optional<T> execute(DynamicValue<T> dynamicValue, String template, Object... args) {
        // If script is empty, no need to evaluate it.
        if (dynamicValue.isEmptyScript()) {
            return Optional.empty();
        }

        String functionName = functionNameOf(dynamicValue, template);
        try {
            Object evaluationResult = invocable.invokeFunction(functionName, args);
            return convert(evaluationResult, dynamicValue.getEvaluatedType());
        } catch (ScriptException | NoSuchMethodException e) {
            throw new ESBException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> convert(Object valueToConvert, Class<?> targetClazz) {
        if (valueToConvert == null) {
            return Optional.empty();
        } else if (sourceAssignableToTarget(valueToConvert.getClass(), targetClazz)) {
            return Optional.of((T) valueToConvert);
        } else {
            T converted = (T) DynamicValueConverterFactory.convert(valueToConvert, valueToConvert.getClass(), targetClazz);
            return Optional.of(converted);
        }
    }
}
