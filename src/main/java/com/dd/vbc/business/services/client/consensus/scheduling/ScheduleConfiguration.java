package com.dd.vbc.business.services.client.consensus.scheduling;

import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfiguration {

    @Bean
    public Scheduler scheduler() {
        return new Scheduler();
    }

    @Bean
    public ObjectFactoryCreatingFactoryBean leaderHeartBeatTimerTaskObjectFactory() {

        ObjectFactoryCreatingFactoryBean factoryBean = new ObjectFactoryCreatingFactoryBean();
        factoryBean.setTargetBeanName("leaderHeartBeatTimerTask");
        return factoryBean;
    }

    @Bean
    public ObjectFactoryCreatingFactoryBean followerHeartBeatTimeoutTaskObjectFactory() {

        ObjectFactoryCreatingFactoryBean factoryBean = new ObjectFactoryCreatingFactoryBean();
        factoryBean.setTargetBeanName("followerHeartBeatTimeoutTask");
        return factoryBean;
    }
}
