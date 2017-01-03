package net.valfridsson.gastolibro;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.valfridsson.gastolibro.health.SimpleHealthCheck;
import net.valfridsson.gastolibro.resources.HelloWorldResource;
import org.skife.jdbi.v2.DBI;

public class GastolibroApplication extends Application<GastolibroConfiguration> {

    private DBI jdbi;

    public static void main(String... args) throws Exception {
        new GastolibroApplication().run(args);
    }

    @Override
    public String getName() {
        return "gastolibro";
    }

    @Override
    public void initialize(Bootstrap<GastolibroConfiguration> bootstrap) {
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(new MigrationsBundle<GastolibroConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(GastolibroConfiguration configuration) {
                return configuration.database;
            }
        });
    }

    @Override
    public void run(GastolibroConfiguration configuration, Environment environment) {
        jdbi = new DBIFactory().build(environment, configuration.database, getName());
        environment.healthChecks().register("simple", new SimpleHealthCheck());
        environment.jersey().register(new HelloWorldResource());
    }

    public DBI getDbi() {
        return jdbi;
    }
}