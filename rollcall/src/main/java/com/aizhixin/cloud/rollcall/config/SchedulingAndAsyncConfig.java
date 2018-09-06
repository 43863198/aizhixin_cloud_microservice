package com.aizhixin.cloud.rollcall.config;


//@Configuration
//@EnableScheduling
//@EnableAsync
//public class SchedulingAndAsyncConfig implements AsyncConfigurer, SchedulingConfigurer {
//
//    @Bean
//    public ThreadPoolTaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(30);
////        scheduler.setThreadNamePrefix("task-");
//        scheduler.setAwaitTerminationSeconds(300);
//        scheduler.setWaitForTasksToCompleteOnShutdown(true);
//        return scheduler;
//    }
//
//    @Override
//    public Executor getAsyncExecutor() {
//        Executor executor = this.taskScheduler();
//        return executor;
//    }
//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar registrar) {
//        TaskScheduler scheduler = this.taskScheduler();
//        registrar.setTaskScheduler(scheduler);
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return null;
//    }
//}