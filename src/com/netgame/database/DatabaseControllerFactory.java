package com.netgame.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.electrotank.electroserver5.extensions.ManagedObjectFactory;
import com.electrotank.electroserver5.extensions.ManagedObjectFactoryLifeCycle;
import com.electrotank.electroserver5.extensions.api.ManagedObjectFactoryApi;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;

public class DatabaseControllerFactory implements ManagedObjectFactory, ManagedObjectFactoryLifeCycle {

    private ManagedObjectFactoryApi api;
    private DatabaseController controller;

    @Override
    public void init(EsObjectRO parameters) {

        try {
            controller = new DatabaseController(loadProperties(parameters));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to spin up controller", e);
        }
    }

    @Override
    public void releaseObject(Object arg0) {
        throw new UnsupportedOperationException("Not supported");
    }

    private Properties loadProperties(EsObjectRO parameters) {
        String fileName = parameters.getString("propertiesFileName", "database.properties");
        InputStream in = getClass().getResourceAsStream("/" + fileName);

        if (null == in) {
            throw new IllegalStateException(
                    "Unable to load properties file '" + fileName + "' as defined in 'propertiesFileName'");
        }

        Properties properties = new Properties();

        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load properties file '" + fileName + "' as defined in 'propertiesFileName'", e);
        }

        return properties;
    }

    @Override
    public Object acquireObject(EsObjectRO arg) {

        return controller;
    }

    @Override
    public void destroy() {
    }

    @Override
    public ManagedObjectFactoryApi getApi() {
        return api;
    }

    @Override
    public void setApi(ManagedObjectFactoryApi api) {
        this.api = api;
    }
}
