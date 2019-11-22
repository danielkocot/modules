package com.reedelk.esb.services;

import com.reedelk.esb.module.ModulesManager;
import com.reedelk.esb.services.configuration.DefaultConfigurationService;
import com.reedelk.esb.services.converter.DefaultConverterService;
import com.reedelk.esb.services.file.DefaultModuleFileProvider;
import com.reedelk.esb.services.hotswap.DefaultHotSwapService;
import com.reedelk.esb.services.hotswap.HotSwapListener;
import com.reedelk.esb.services.module.DefaultModuleService;
import com.reedelk.esb.services.module.EventListener;
import com.reedelk.esb.services.scriptengine.ScriptEngine;
import com.reedelk.runtime.api.service.ConfigurationService;
import com.reedelk.runtime.api.service.ConverterService;
import com.reedelk.runtime.api.service.ScriptEngineService;
import com.reedelk.runtime.system.api.HotSwapService;
import com.reedelk.runtime.system.api.ModuleService;
import com.reedelk.runtime.system.api.SystemProperty;
import com.reedelk.runtime.system.api.file.ModuleFileProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ServicesManager {

    private static final Dictionary<String, ?> NO_PROPERTIES = new Hashtable<>();

    private final ConfigurationAdmin configurationAdmin;
    private final HotSwapListener hotSwapListener;
    private final ModulesManager modulesManager;
    private final SystemProperty systemProperty;
    private final EventListener eventListener;

    private List<ServiceRegistration<?>> registeredServices = new ArrayList<>();

    private DefaultConfigurationService configurationService;
    private ScriptEngine scriptEngineService;

    public ServicesManager(EventListener eventListener,
                           HotSwapListener hotSwapListener,
                           ModulesManager modulesManager,
                           SystemProperty systemProperty,
                           ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
        this.hotSwapListener = hotSwapListener;
        this.systemProperty = systemProperty;
        this.modulesManager = modulesManager;
        this.eventListener = eventListener;
    }

    public void registerServices(BundleContext context) {
        registerModuleService(context);
        registerHotSwapService(context);
        registerConverterService(context);
        registerScriptEngineService(context);
        registerConfigurationService(context);
        registerModuleFileProviderService(context);
    }

    public void unregisterServices() {
        registeredServices.forEach(ServiceRegistration::unregister);
    }

    public DefaultConfigurationService configurationService() {
        return configurationService;
    }

    public ScriptEngine scriptEngineService() {
        return scriptEngineService;
    }

    private void registerConfigurationService(BundleContext context) {
        configurationService = new DefaultConfigurationService(configurationAdmin, systemProperty);
        configurationService.initialize();
        ServiceRegistration<ConfigurationService> registration =
                context.registerService(ConfigurationService.class, configurationService, NO_PROPERTIES);
        registeredServices.add(registration);
    }

    private void registerScriptEngineService(BundleContext context) {
        scriptEngineService = ScriptEngine.INSTANCE;
        ServiceRegistration<ScriptEngineService> registration =
                context.registerService(ScriptEngineService.class, scriptEngineService, NO_PROPERTIES);
        registeredServices.add(registration);
    }

    private void registerHotSwapService(BundleContext context) {
        DefaultHotSwapService service = new DefaultHotSwapService(context, hotSwapListener);
        ServiceRegistration<HotSwapService> registration =
                context.registerService(HotSwapService.class, service, NO_PROPERTIES);
        registeredServices.add(registration);
    }

    private void registerModuleService(BundleContext context) {
        DefaultModuleService service = new DefaultModuleService(context, modulesManager, eventListener);
        ServiceRegistration<ModuleService> registration =
                context.registerService(ModuleService.class, service, NO_PROPERTIES);
        registeredServices.add(registration);
    }

    private void registerModuleFileProviderService(BundleContext context) {
        ModuleFileProvider service = new DefaultModuleFileProvider(context, modulesManager);
        ServiceRegistration<ModuleFileProvider> registration =
                context.registerService(ModuleFileProvider.class, service, NO_PROPERTIES);
        registeredServices.add(registration);
    }

    private void registerConverterService(BundleContext context) {
        ConverterService service = DefaultConverterService.getInstance();
        ServiceRegistration<ConverterService> registration =
                context.registerService(ConverterService.class, service, NO_PROPERTIES);
        registeredServices.add(registration);
    }
}
