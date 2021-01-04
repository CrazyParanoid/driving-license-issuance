package ru.mvd.driving.license.config;

import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.mvd.driving.license.infrastructure.events.IntegrationEventsListener;

@TestConfiguration
public class IntegrationEventsListenerConfiguration {

    @Bean
    public BeanPostProcessor messageRequestListenerPostProcessor() {
        return new ProxiedMockPostProcessor(IntegrationEventsListener.class);
    }

    public static class ProxiedMockPostProcessor implements BeanPostProcessor {
        private final Class<?> mockedClass;

        public ProxiedMockPostProcessor(Class<?> mockedClass) {
            this.mockedClass = mockedClass;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName)
                throws BeansException {
            if (mockedClass.isInstance(bean)) {
                return Mockito.mock(mockedClass, AdditionalAnswers.delegatesTo(bean));
            }
            return bean;
        }
    }

}
