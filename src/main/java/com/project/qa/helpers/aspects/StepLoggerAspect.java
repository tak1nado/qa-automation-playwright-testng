package com.project.qa.helpers.aspects;

import io.qameta.allure.Step;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;
import java.util.stream.Stream;

@Aspect
public class StepLoggerAspect {
    protected final Logger logger = LoggerFactory.getLogger(StepLoggerAspect.class);

    @Value("${logging.steps:false}")
    private boolean loggingSteps;

    @Before("@annotation(io.qameta.allure.Step)")
    public void logBeforeMethod(JoinPoint joinPoint) {
        if (loggingSteps) {
            String text = getLogText(joinPoint);
            logger.info("Step: " + text);
        }
    }

    private String getLogText(JoinPoint joinPoint) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Step stepClass = methodSignature.getMethod().getAnnotation(Step.class);
            if (Objects.nonNull(stepClass)) {
                String mainStepText = stepClass.value();
                String stepParamRegexp = "[{].*?[}]";
                if (mainStepText.matches(".*" + stepParamRegexp + ".*")) {
                    Object[] methodArgs = joinPoint.getArgs();
                    if (methodArgs != null && methodArgs.length > 0) {
                        mainStepText = String.format(
                                mainStepText.replaceAll(stepParamRegexp, "%s"),
                                Stream.of(methodArgs).map(Object::toString).toArray()
                        );
                    }
                }
                return mainStepText;
            } else {
                return methodSignature.getMethod().getName();
            }
        } catch (Exception e) {
            String errorStepMessage = "Cannot retrieve step text information";
            logger.error(errorStepMessage);
            return errorStepMessage;
        }
    }
}