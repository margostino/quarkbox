package org.quarkbox.bootstrap;

import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
//import io.quarkus.micrometer.runtime.MicrometerRecorder;
//import io.quarkus.micrometer.runtime.binder.HttpBinderConfiguration;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

//@ApplicationScoped
public class ApplicationBootstrap {

//    @Inject
//    NamespaceMapping namespaceMapping;

//    @Produces
//    @Singleton
////    @BuildStep
////    @Record(STATIC_INIT)
//    public HttpProvider build() {
//        Map<String, DataProviderMapping> namespaces = namespaceMapping.mappings();
//        return RestClientBuilder.newBuilder()
//                                .baseUri(URI.create("url"))
//                                .build(HttpProvider.class);
//
//    }

//    @BuildStep
//    @Record(RUNTIME_INIT)
//    @Consume(SyntheticBeansRuntimeInitBuildItem.class)
//    void accessFoo(MicrometerRecorder recorder) {
//        recorder.configureHttpMetrics()
//    }

//    @BuildStep
//    @Record(STATIC_INIT)
//    SyntheticBeanBuildItem syntheticBean(MicrometerRecorder recorder) {
//        return SyntheticBeanBuildItem.configure(HttpBinderConfiguration.class)
//                                     .scope(Singleton.class)
//                                     .setRuntimeInit()
//                                     .runtimeValue(recorder.configureHttpMetrics(true, true, null, null, null))
//                                     .done();
//    }

//    @Produces
//    @Singleton
//    public HttpBinderConfiguration httpBinderConfiguration() {
//        return new HttpBinderConfiguration(true, true, null, null, null);
//
//    }
}
